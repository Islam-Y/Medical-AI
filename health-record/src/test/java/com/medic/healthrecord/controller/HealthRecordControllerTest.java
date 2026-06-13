package com.medic.healthrecord.controller;

import com.medic.healthrecord.dto.CreateObservationRequest;
import com.medic.healthrecord.dto.CreateSymptomRequest;
import com.medic.healthrecord.dto.ObservationResponse;
import com.medic.healthrecord.dto.SymptomResponse;
import com.medic.healthrecord.service.HealthRecordService;
import com.medic.healthrecord.service.JwtService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HealthRecordControllerTest {

    private final HealthRecordService healthRecordService = mock(HealthRecordService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final HealthRecordController controller = new HealthRecordController(healthRecordService, jwtService);

    @Test
    void createObservationUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateObservationRequest request = new CreateObservationRequest("Ferritin", BigDecimal.TEN, "ng/mL", "30-300", Instant.now(), null);
        ObservationResponse expected = new ObservationResponse(UUID.randomUUID(), "Ferritin", BigDecimal.TEN, "ng/mL", "30-300", request.observedAt(), null);
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(healthRecordService.addObservation(userId, request)).thenReturn(expected);

        // Act
        ObservationResponse response = controller.createObservation("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(healthRecordService).addObservation(userId, request);
    }

    @Test
    void symptomsUseAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        SymptomResponse symptom = new SymptomResponse(UUID.randomUUID(), "Back pain", 4, "After training", Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(healthRecordService.symptoms(userId)).thenReturn(List.of(symptom));

        // Act
        List<SymptomResponse> response = controller.symptoms("Bearer token");

        // Assert
        assertThat(response).containsExactly(symptom);
        verify(healthRecordService).symptoms(userId);
    }

    @Test
    void createSymptomUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateSymptomRequest request = new CreateSymptomRequest("Back pain", 4, "After training", Instant.now());
        SymptomResponse expected = new SymptomResponse(UUID.randomUUID(), "Back pain", 4, "After training", request.observedAt());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(healthRecordService.addSymptom(userId, request)).thenReturn(expected);

        // Act
        SymptomResponse response = controller.createSymptom("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(healthRecordService).addSymptom(userId, request);
    }
}
