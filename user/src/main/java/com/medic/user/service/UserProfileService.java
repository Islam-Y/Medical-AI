package com.medic.user.service;

import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.user.UserProfileUpdatedEvent;
import com.medic.user.dto.UpdateUserProfileRequest;
import com.medic.user.dto.UserProfileResponse;
import com.medic.user.entity.UserProfileEntity;
import com.medic.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Transactional
    public UserProfileResponse getProfile(UUID userId) {
        return toResponse(profileFor(userId, null));
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateUserProfileRequest request) {
        UserProfileEntity profile = profileFor(userId, null);
        profile.update(request);
        UserProfileEntity saved = userProfileRepository.save(profile);
        outboxService.enqueue(
                TopicNames.USER_EVENTS,
                EventTypes.USER_PROFILE_UPDATED,
                saved.getId(),
                EventEnvelope.create(
                        EventTypes.USER_PROFILE_UPDATED,
                        UUID.randomUUID(),
                        saved.getUserId(),
                        new UserProfileUpdatedEvent(saved.getId())
                )
        );
        return toResponse(saved);
    }

    @Transactional
    public void handleAuthEvent(String message) {
        JsonNode root = readTree(message);
        if (!EventTypes.USER_REGISTERED.equals(root.path("eventType").asString())) {
            return;
        }
        UUID userId = UUID.fromString(root.path("userId").asString());
        String email = root.path("payload").path("email").asString();
        UserProfileEntity profile = profileFor(userId, email);
        profile.updateEmail(email);
        userProfileRepository.save(profile);
    }

    private UserProfileEntity profileFor(UUID userId, String email) {
        return userProfileRepository.findByUserId(userId)
                .orElseGet(() -> userProfileRepository.save(new UserProfileEntity(userId, email)));
    }

    private JsonNode readTree(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid event payload", exception);
        }
    }

    private UserProfileResponse toResponse(UserProfileEntity profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getEmail(),
                profile.getHeightCm(),
                profile.getWeightKg(),
                profile.getBirthDate(),
                profile.getSex(),
                profile.getKnownDiagnoses(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
