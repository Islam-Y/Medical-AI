package com.medic.indexing.event;

import com.medic.indexing.service.IndexingService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SourceEventListenerTest {

    @Test
    void handleDelegatesToIndexingService() {
        // Arrange
        IndexingService indexingService = mock(IndexingService.class);
        SourceEventListener listener = new SourceEventListener(indexingService);

        // Act
        listener.handle("message");

        // Assert
        verify(indexingService).handleSourceEvent("message");
    }
}
