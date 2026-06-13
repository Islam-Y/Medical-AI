package com.medic.healthrecord.service;

import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.health.HealthRecordChangedEvent;
import com.medic.healthrecord.dto.CreateDiagnosisRequest;
import com.medic.healthrecord.dto.CreateObservationRequest;
import com.medic.healthrecord.dto.CreateSymptomRequest;
import com.medic.healthrecord.dto.DiagnosisResponse;
import com.medic.healthrecord.dto.ObservationResponse;
import com.medic.healthrecord.dto.SymptomResponse;
import com.medic.healthrecord.dto.TimelineItemResponse;
import com.medic.healthrecord.entity.DiagnosisEntity;
import com.medic.healthrecord.entity.ObservationEntity;
import com.medic.healthrecord.entity.SymptomEntity;
import com.medic.healthrecord.repository.DiagnosisRepository;
import com.medic.healthrecord.repository.ObservationRepository;
import com.medic.healthrecord.repository.SymptomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HealthRecordService {

    private final ObservationRepository observationRepository;
    private final SymptomRepository symptomRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Transactional
    public ObservationResponse addObservation(UUID userId, CreateObservationRequest request) {
        ObservationEntity observation = observationRepository.save(new ObservationEntity(
                userId,
                request.name(),
                request.value(),
                request.unit(),
                request.referenceRange(),
                request.observedAt(),
                request.sourceDocumentId()
        ));
        enqueueChanged(observation.getId(), userId, "observation", "created");
        return toObservationResponse(observation);
    }

    @Transactional(readOnly = true)
    public List<ObservationResponse> observations(UUID userId) {
        return observationRepository.findByUserIdOrderByObservedAtDesc(userId).stream()
                .map(this::toObservationResponse)
                .toList();
    }

    @Transactional
    public SymptomResponse addSymptom(UUID userId, CreateSymptomRequest request) {
        SymptomEntity symptom = symptomRepository.save(new SymptomEntity(
                userId,
                request.name(),
                request.intensity(),
                request.notes(),
                request.observedAt()
        ));
        enqueueChanged(symptom.getId(), userId, "symptom", "created");
        return toSymptomResponse(symptom);
    }

    @Transactional(readOnly = true)
    public List<SymptomResponse> symptoms(UUID userId) {
        return symptomRepository.findByUserIdOrderByObservedAtDesc(userId).stream()
                .map(this::toSymptomResponse)
                .toList();
    }

    @Transactional
    public DiagnosisResponse addDiagnosis(UUID userId, CreateDiagnosisRequest request) {
        DiagnosisEntity diagnosis = diagnosisRepository.save(new DiagnosisEntity(
                userId,
                request.name(),
                request.diagnosedAt(),
                request.source()
        ));
        enqueueChanged(diagnosis.getId(), userId, "diagnosis", "created");
        return toDiagnosisResponse(diagnosis);
    }

    @Transactional(readOnly = true)
    public List<DiagnosisResponse> diagnoses(UUID userId) {
        return diagnosisRepository.findByUserIdOrderByDiagnosedAtDesc(userId).stream()
                .map(this::toDiagnosisResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimelineItemResponse> timeline(UUID userId) {
        Stream<TimelineItemResponse> observationItems = observationRepository.findByUserIdOrderByObservedAtDesc(userId).stream()
                .map(observation -> new TimelineItemResponse("observation", observation.getId(), observation.getName(), observation.getObservedAt()));
        Stream<TimelineItemResponse> symptomItems = symptomRepository.findByUserIdOrderByObservedAtDesc(userId).stream()
                .map(symptom -> new TimelineItemResponse("symptom", symptom.getId(), symptom.getName(), symptom.getObservedAt()));
        Stream<TimelineItemResponse> diagnosisItems = diagnosisRepository.findByUserIdOrderByDiagnosedAtDesc(userId).stream()
                .map(diagnosis -> new TimelineItemResponse("diagnosis", diagnosis.getId(), diagnosis.getName(), diagnosis.getDiagnosedAt()));
        return Stream.of(observationItems, symptomItems, diagnosisItems)
                .flatMap(stream -> stream)
                .sorted(Comparator.comparing(TimelineItemResponse::occurredAt).reversed())
                .toList();
    }

    @Transactional
    public void handleDocumentEvent(String message) {
        JsonNode root = readTree(message);
        if (!EventTypes.DOCUMENT_EXTRACTION_COMPLETED.equals(root.path("eventType").asString())) {
            return;
        }
        UUID userId = UUID.fromString(root.path("userId").asString());
        UUID documentId = UUID.fromString(root.path("payload").path("documentId").asString());
        for (JsonNode node : root.path("payload").path("observations")) {
            ObservationEntity observation = observationRepository.save(new ObservationEntity(
                    userId,
                    node.path("name").asString(),
                    node.path("value").decimalValue(),
                    node.path("unit").asString(),
                    nullableText(node.path("referenceRange")),
                    Instant.parse(node.path("observedAt").asString()),
                    documentId
            ));
            enqueueChanged(observation.getId(), userId, "observation", "extracted");
        }
    }

    private void enqueueChanged(UUID recordId, UUID userId, String recordType, String changeType) {
        outboxService.enqueue(
                TopicNames.HEALTH_RECORD_EVENTS,
                EventTypes.HEALTH_RECORD_CHANGED,
                recordId,
                EventEnvelope.create(
                        EventTypes.HEALTH_RECORD_CHANGED,
                        UUID.randomUUID(),
                        userId,
                        new HealthRecordChangedEvent(recordId, recordType, changeType)
                )
        );
    }

    private JsonNode readTree(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid event payload", exception);
        }
    }

    private String nullableText(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        return node.asString();
    }

    private ObservationResponse toObservationResponse(ObservationEntity observation) {
        return new ObservationResponse(
                observation.getId(),
                observation.getName(),
                observation.getValue(),
                observation.getUnit(),
                observation.getReferenceRange(),
                observation.getObservedAt(),
                observation.getSourceDocumentId()
        );
    }

    private SymptomResponse toSymptomResponse(SymptomEntity symptom) {
        return new SymptomResponse(symptom.getId(), symptom.getName(), symptom.getIntensity(), symptom.getNotes(), symptom.getObservedAt());
    }

    private DiagnosisResponse toDiagnosisResponse(DiagnosisEntity diagnosis) {
        return new DiagnosisResponse(diagnosis.getId(), diagnosis.getName(), diagnosis.getDiagnosedAt(), diagnosis.getSource());
    }
}
