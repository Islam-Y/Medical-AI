package com.medic.chat.repository;

import com.medic.chat.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {

    List<ChatSessionEntity> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    Optional<ChatSessionEntity> findByIdAndUserId(UUID id, UUID userId);
}
