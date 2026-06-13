package com.medic.user.controller;

import com.medic.user.dto.UpdateUserProfileRequest;
import com.medic.user.dto.UserProfileResponse;
import com.medic.user.service.JwtService;
import com.medic.user.service.UserProfileService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileControllerTest {

    private final UserProfileService userProfileService = mock(UserProfileService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final UserProfileController controller = new UserProfileController(userProfileService, jwtService);

    @Test
    void meUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserProfileResponse expected = new UserProfileResponse(UUID.randomUUID(), userId, "user@example.com", null, null, null, null, null, null, null);
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(userProfileService.getProfile(userId)).thenReturn(expected);

        // Act
        UserProfileResponse response = controller.me("Bearer token");

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(userProfileService).getProfile(userId);
    }

    @Test
    void updateUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
                BigDecimal.valueOf(180),
                BigDecimal.valueOf(80),
                LocalDate.parse("1990-01-01"),
                "male",
                "None"
        );
        UserProfileResponse expected = new UserProfileResponse(UUID.randomUUID(), userId, "user@example.com", request.heightCm(), request.weightKg(), request.birthDate(), request.sex(), request.knownDiagnoses(), null, null);
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(userProfileService.updateProfile(userId, request)).thenReturn(expected);

        // Act
        UserProfileResponse response = controller.update("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(userProfileService).updateProfile(userId, request);
    }
}
