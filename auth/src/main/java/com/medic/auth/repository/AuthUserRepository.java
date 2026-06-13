package com.medic.auth.repository;

import com.medic.auth.entity.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository extends JpaRepository<AuthUserEntity, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<AuthUserEntity> findByEmailIgnoreCase(String email);
}
