package com.medic.document.event;

import com.medic.document.service.DocumentService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DocumentEventListenerTest {

    @Test
    void handleDelegatesToDocumentService() {
        // Arrange
        DocumentService documentService = mock(DocumentService.class);
        DocumentEventListener listener = new DocumentEventListener(documentService);

        // Act
        listener.handle("message");

        // Assert
        verify(documentService).handleDocumentEvent("message");
    }
}
