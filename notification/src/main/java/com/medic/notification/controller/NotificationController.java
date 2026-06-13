package com.medic.notification.controller;

import com.medic.notification.dto.NotificationResponse;
import com.medic.notification.service.JwtService;
import com.medic.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    @GetMapping
    public List<NotificationResponse> notifications(@RequestHeader("Authorization") String authorization) {
        return notificationService.notifications(jwtService.parseBearerUserId(authorization));
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id
    ) {
        return notificationService.markRead(jwtService.parseBearerUserId(authorization), id);
    }
}
