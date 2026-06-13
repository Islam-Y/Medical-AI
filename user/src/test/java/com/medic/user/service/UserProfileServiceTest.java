package com.medic.user.service;

import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.user.UserRegisteredEvent;
import com.medic.user.dto.UpdateUserProfileRequest;
import com.medic.user.dto.UserProfileResponse;
import com.medic.user.entity.UserProfileEntity;
import com.medic.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {

    private final UserProfileRepository userProfileRepository = mock(UserProfileRepository.class);
    private final OutboxService outboxService = mock(OutboxService.class);
    private UserProfileService service;

    @BeforeEach
    void setUp() {
        service = new UserProfileService(userProfileRepository, outboxService, new ObjectMapper());
    }

    @Test
    void getProfileCreatesMissingProfile() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserProfileResponse response = service.getProfile(userId);

        // Assert
        assertThat(response.userId()).isEqualTo(userId);
        verify(userProfileRepository).save(any(UserProfileEntity.class));
    }

    @Test
    void updateProfileStoresProfileAndPublishesEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserProfileEntity profile = new UserProfileEntity(userId, "user@example.com");
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(
                BigDecimal.valueOf(180),
                BigDecimal.valueOf(80),
                LocalDate.parse("1990-01-01"),
                "male",
                "None"
        );
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(profile)).thenReturn(profile);

        // Act
        UserProfileResponse response = service.updateProfile(userId, request);

        // Assert
        assertThat(response.heightCm()).isEqualByComparingTo("180");
        assertThat(response.knownDiagnoses()).isEqualTo("None");
        verify(outboxService).enqueue(
                org.mockito.ArgumentMatchers.eq(TopicNames.USER_EVENTS),
                org.mockito.ArgumentMatchers.eq(EventTypes.USER_PROFILE_UPDATED),
                org.mockito.ArgumentMatchers.eq(profile.getId()),
                any()
        );
    }

    @Test
    void handleAuthEventCreatesProfileFromRegistration() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                EventTypes.USER_REGISTERED,
                UUID.randomUUID(),
                userId,
                new UserRegisteredEvent(userId, "user@example.com")
        ));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.handleAuthEvent(message);

        // Assert
        ArgumentCaptor<UserProfileEntity> captor = ArgumentCaptor.forClass(UserProfileEntity.class);
        verify(userProfileRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues()).anySatisfy(profile -> {
            assertThat(profile.getUserId()).isEqualTo(userId);
            assertThat(profile.getEmail()).isEqualTo("user@example.com");
        });
    }

    @Test
    void handleAuthEventIgnoresDifferentEventType() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                EventTypes.USER_PROFILE_UPDATED,
                UUID.randomUUID(),
                userId,
                "payload"
        ));

        // Act
        service.handleAuthEvent(message);

        // Assert
        verify(userProfileRepository, never()).save(any());
    }
}
