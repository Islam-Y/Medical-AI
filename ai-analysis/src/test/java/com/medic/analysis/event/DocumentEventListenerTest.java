package com.medic.analysis.event;

import com.medic.analysis.service.AnalysisJobService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DocumentEventListenerTest {

    @Test
    void handleDelegatesToAnalysisJobService() {
        // Arrange
        AnalysisJobService analysisJobService = mock(AnalysisJobService.class);
        DocumentEventListener listener = new DocumentEventListener(analysisJobService);

        // Act
        listener.handle("message");

        // Assert
        verify(analysisJobService).handleDocumentEvent("message");
    }
}
