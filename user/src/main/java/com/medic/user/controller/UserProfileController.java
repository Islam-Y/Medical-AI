package com.medic.user.controller;

import com.medic.user.dto.UpdateUserProfileRequest;
import com.medic.user.dto.UserProfileResponse;
import com.medic.user.service.JwtService;
import com.medic.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final JwtService jwtService;

    @GetMapping("/me")
    public UserProfileResponse me(@RequestHeader("Authorization") String authorization) {
        return userProfileService.getProfile(jwtService.parseBearerUserId(authorization));
    }

    @PatchMapping("/me")
    public UserProfileResponse update(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        return userProfileService.updateProfile(jwtService.parseBearerUserId(authorization), request);
    }
}
