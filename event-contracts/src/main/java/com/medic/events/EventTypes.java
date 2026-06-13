package com.medic.events;

public final class EventTypes {

    public static final String USER_REGISTERED = "UserRegistered";
    public static final String USER_PROFILE_UPDATED = "UserProfileUpdated";
    public static final String DOCUMENT_UPLOADED = "DocumentUploaded";
    public static final String DOCUMENT_EXTRACTION_COMPLETED = "DocumentExtractionCompleted";
    public static final String HEALTH_RECORD_CHANGED = "HealthRecordChanged";
    public static final String NOTIFICATION_CREATED = "NotificationCreated";

    private EventTypes() {
    }
}
