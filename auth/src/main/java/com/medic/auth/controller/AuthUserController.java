package com.medic.auth.controller;

import com.medic.auth.dto.AuthUserResponse;
import com.medic.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthUserController {

    private final AuthService authService;

    @GetMapping("/me")
    public AuthUserResponse me(@RequestHeader("Authorization") String authorization) {
        return authService.currentUser(authorization);
    }
}
