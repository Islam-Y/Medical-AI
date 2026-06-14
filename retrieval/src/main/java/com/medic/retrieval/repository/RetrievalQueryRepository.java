package com.medic.retrieval.repository;

import com.medic.retrieval.entity.RetrievalQueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RetrievalQueryRepository extends JpaRepository<RetrievalQueryEntity, UUID> {
}
