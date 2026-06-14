package com.medic.consent.service;

import com.medic.consent.dto.ConsentResponse;
import com.medic.consent.dto.GrantConsentRequest;
import com.medic.consent.entity.ConsentEntity;
import com.medic.consent.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;

    @Transactional
    public ConsentResponse grant(UUID userId, GrantConsentRequest request) {
        return toResponse(consentRepository.save(new ConsentEntity(userId, request.scope(), request.subject())));
    }

    @Transactional(readOnly = true)
    public List<ConsentResponse> consents(UUID userId) {
        return consentRepository.findByUserIdOrderByGrantedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ConsentResponse revoke(UUID userId, UUID consentId) {
        ConsentEntity consent = consentRepository.findByIdAndUserId(consentId, userId)
                .orElseThrow(() -> new ConsentNotFoundException(consentId));
        consent.revoke();
        return toResponse(consentRepository.save(consent));
    }

    private ConsentResponse toResponse(ConsentEntity consent) {
        return new ConsentResponse(consent.getId(), consent.getScope(), consent.getStatus(), consent.getSubject(), consent.getGrantedAt(), consent.getRevokedAt());
    }
}
