package com.medic.events;

public final class EventTypes {

    public static final String USER_REGISTERED = "UserRegistered";
    public static final String USER_PROFILE_UPDATED = "UserProfileUpdated";
    public static final String DOCUMENT_UPLOADED = "DocumentUploaded";
    public static final String DOCUMENT_EXTRACTION_COMPLETED = "DocumentExtractionCompleted";
    public static final String HEALTH_RECORD_CHANGED = "HealthRecordChanged";
    public static final String CHAT_MESSAGE_CREATED = "ChatMessageCreated";
    public static final String RETRIEVAL_QUERY_EXECUTED = "RetrievalQueryExecuted";
    public static final String INDEX_ENTRY_UPDATED = "IndexEntryUpdated";
    public static final String EVALUATION_RUN_COMPLETED = "EvaluationRunCompleted";
    public static final String CONSENT_GRANTED = "ConsentGranted";
    public static final String CONSENT_REVOKED = "ConsentRevoked";
    public static final String AUDIT_EVENT_RECORDED = "AuditEventRecorded";
    public static final String NOTIFICATION_CREATED = "NotificationCreated";

    private EventTypes() {
    }
}
