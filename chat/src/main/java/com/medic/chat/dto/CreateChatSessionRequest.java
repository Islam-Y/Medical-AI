package com.medic.chat.dto;

import jakarta.validation.constraints.Size;

public record CreateChatSessionRequest(
        @Size(max = 120) String title
) {
}
