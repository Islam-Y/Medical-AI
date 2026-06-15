package com.medic.healthrecord.service;

import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.document.DocumentExtractionCompletedEvent;
import com.medic.events.document.ExtractedObservation;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HealthRecordServiceTest {

    private final ObservationRepository observationRepository = mock(ObservationRepository.class);
    private final SymptomRepository symptomRepository = mock(SymptomRepository.class);
    private final DiagnosisRepository diagnosisRepository = mock(DiagnosisRepository.class);
    private final OutboxService outboxService = mock(OutboxService.class);
    private HealthRecordService service;

    @BeforeEach
    void setUp() {
        service = new HealthRecordService(
                observationRepository,
                symptomRepository,
                diagnosisRepository,
                outboxService,
                new ObjectMapper()
        );
    }

    @Test
    void addObservationStoresObservationAndPublishesChange() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Instant observedAt = Instant.parse("2026-06-13T10:00:00Z");
        CreateObservationRequest request = new CreateObservationRequest("Ferritin", BigDecimal.TEN, "ng/mL", "30-300", observedAt, null);
        when(observationRepository.save(any(ObservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ObservationResponse response = service.addObservation(userId, request);

        // Assert
        assertThat(response.name()).isEqualTo("Ferritin");
        assertThat(response.value()).isEqualByComparingTo(BigDecimal.TEN);
        verify(outboxService).enqueue(any(), any(), any(), any());
    }

    @Test
    void handleDocumentEventCreatesExtractedObservation() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        ExtractedObservation extracted = new ExtractedObservation("Vitamin D", BigDecimal.valueOf(22), "ng/mL", "30-100", Instant.parse("2026-06-13T10:00:00Z"));
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                EventTypes.DOCUMENT_EXTRACTION_COMPLETED,
                UUID.randomUUID(),
                userId,
                new DocumentExtractionCompletedEvent(
                        documentId,
                        UUID.randomUUID(),
                        "EXTRACTED",
                        "medical-ai-extractions",
                        "artifact.md",
                        "text/markdown",
                        "stub-analysis",
                        "0.0.1",
                        List.of(extracted)
                )
        ));
        when(observationRepository.save(any(ObservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.handleDocumentEvent(message);

        // Assert
        verify(observationRepository).save(any(ObservationEntity.class));
        verify(outboxService).enqueue(any(), any(), any(), any());
    }

    @Test
    void timelineSortsItemsDescending() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ObservationEntity first = new ObservationEntity(userId, "Ferritin", BigDecimal.ONE, "ng/mL", null, Instant.parse("2026-01-01T10:00:00Z"), null);
        ObservationEntity second = new ObservationEntity(userId, "Vitamin D", BigDecimal.TEN, "ng/mL", null, Instant.parse("2026-02-01T10:00:00Z"), null);
        when(observationRepository.findByUserIdOrderByObservedAtDesc(userId)).thenReturn(List.of(first, second));
        when(symptomRepository.findByUserIdOrderByObservedAtDesc(userId)).thenReturn(List.of());
        when(diagnosisRepository.findByUserIdOrderByDiagnosedAtDesc(userId)).thenReturn(List.of());

        // Act
        List<TimelineItemResponse> timeline = service.timeline(userId);

        // Assert
        assertThat(timeline).extracting(TimelineItemResponse::title).containsExactly("Vitamin D", "Ferritin");
    }

    @Test
    void addSymptomStoresSymptomAndPublishesChange() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateSymptomRequest request = new CreateSymptomRequest("Back pain", 5, "Morning", Instant.parse("2026-06-13T10:00:00Z"));
        when(symptomRepository.save(any(SymptomEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SymptomResponse response = service.addSymptom(userId, request);

        // Assert
        assertThat(response.name()).isEqualTo("Back pain");
        assertThat(response.intensity()).isEqualTo(5);
        verify(outboxService).enqueue(any(), any(), any(), any());
    }

    @Test
    void addDiagnosisStoresDiagnosisAndPublishesChange() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateDiagnosisRequest request = new CreateDiagnosisRequest("Flat feet", Instant.parse("2026-06-13T10:00:00Z"), "Orthopedist");
        when(diagnosisRepository.save(any(DiagnosisEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        DiagnosisResponse response = service.addDiagnosis(userId, request);

        // Assert
        assertThat(response.name()).isEqualTo("Flat feet");
        assertThat(response.source()).isEqualTo("Orthopedist");
        verify(outboxService).enqueue(any(), any(), any(), any());
    }

    @Test
    void listMethodsMapStoredRecords() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ObservationEntity observation = new ObservationEntity(userId, "Ferritin", BigDecimal.TEN, "ng/mL", null, Instant.parse("2026-06-13T10:00:00Z"), null);
        SymptomEntity symptom = new SymptomEntity(userId, "Back pain", 4, "Morning", Instant.parse("2026-06-12T10:00:00Z"));
        DiagnosisEntity diagnosis = new DiagnosisEntity(userId, "Flat feet", Instant.parse("2026-06-11T10:00:00Z"), "Orthopedist");
        when(observationRepository.findByUserIdOrderByObservedAtDesc(userId)).thenReturn(List.of(observation));
        when(symptomRepository.findByUserIdOrderByObservedAtDesc(userId)).thenReturn(List.of(symptom));
        when(diagnosisRepository.findByUserIdOrderByDiagnosedAtDesc(userId)).thenReturn(List.of(diagnosis));

        // Act
        List<ObservationResponse> observations = service.observations(userId);
        List<SymptomResponse> symptoms = service.symptoms(userId);
        List<DiagnosisResponse> diagnoses = service.diagnoses(userId);

        // Assert
        assertThat(observations).extracting(ObservationResponse::name).containsExactly("Ferritin");
        assertThat(symptoms).extracting(SymptomResponse::name).containsExactly("Back pain");
        assertThat(diagnoses).extracting(DiagnosisResponse::name).containsExactly("Flat feet");
    }
}
