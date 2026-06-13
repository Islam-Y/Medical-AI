package com.medic.user.event;

import com.medic.user.service.UserProfileService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthEventListenerTest {

    @Test
    void handleDelegatesToProfileService() {
        // Arrange
        UserProfileService userProfileService = mock(UserProfileService.class);
        AuthEventListener listener = new AuthEventListener(userProfileService);

        // Act
        listener.handle("message");

        // Assert
        verify(userProfileService).handleAuthEvent("message");
    }
}
