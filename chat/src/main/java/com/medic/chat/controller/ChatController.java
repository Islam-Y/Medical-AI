package com.medic.chat.controller;

import com.medic.chat.dto.ChatMessageResponse;
import com.medic.chat.dto.ChatSessionResponse;
import com.medic.chat.dto.CreateChatSessionRequest;
import com.medic.chat.dto.SendChatMessageRequest;
import com.medic.chat.service.ChatService;
import com.medic.chat.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtService jwtService;

    @PostMapping("/sessions")
    public ChatSessionResponse createSession(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateChatSessionRequest request
    ) {
        return chatService.createSession(jwtService.parseBearerUserId(authorization), request);
    }

    @GetMapping("/sessions")
    public List<ChatSessionResponse> sessions(@RequestHeader("Authorization") String authorization) {
        return chatService.sessions(jwtService.parseBearerUserId(authorization));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public List<ChatMessageResponse> messages(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID sessionId
    ) {
        return chatService.messages(jwtService.parseBearerUserId(authorization), sessionId);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ChatMessageResponse sendMessage(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID sessionId,
            @Valid @RequestBody SendChatMessageRequest request
    ) {
        return chatService.sendMessage(jwtService.parseBearerUserId(authorization), sessionId, request);
    }
}
