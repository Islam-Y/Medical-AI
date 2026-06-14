package com.medic.consent.controller;

import com.medic.consent.dto.ConsentResponse;
import com.medic.consent.dto.GrantConsentRequest;
import com.medic.consent.entity.ConsentScope;
import com.medic.consent.entity.ConsentStatus;
import com.medic.consent.service.ConsentService;
import com.medic.consent.service.JwtService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsentControllerTest {

    private final ConsentService consentService = mock(ConsentService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final ConsentController controller = new ConsentController(consentService, jwtService);

    @Test
    void grantUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        GrantConsentRequest request = new GrantConsentRequest(ConsentScope.AI_ANALYSIS, "documents");
        ConsentResponse expected = new ConsentResponse(UUID.randomUUID(), request.scope(), ConsentStatus.ACTIVE, request.subject(), Instant.now(), null);
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(consentService.grant(userId, request)).thenReturn(expected);

        // Act
        ConsentResponse response = controller.grant("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(consentService).grant(userId, request);
    }

    @Test
    void consentsUseAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ConsentResponse expected = new ConsentResponse(UUID.randomUUID(), ConsentScope.RESEARCH_EVALUATION, ConsentStatus.ACTIVE, "research", Instant.now(), null);
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(consentService.consents(userId)).thenReturn(List.of(expected));

        // Act
        List<ConsentResponse> response = controller.consents("Bearer token");

        // Assert
        assertThat(response).containsExactly(expected);
        verify(consentService).consents(userId);
    }

    @Test
    void revokeUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();
        ConsentResponse expected = new ConsentResponse(consentId, ConsentScope.DATA_EXPORT, ConsentStatus.REVOKED, "export", Instant.now(), Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(consentService.revoke(userId, consentId)).thenReturn(expected);

        // Act
        ConsentResponse response = controller.revoke("Bearer token", consentId);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(consentService).revoke(userId, consentId);
    }
}
