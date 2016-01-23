package com.spb.kbv.messageapp.services;

public final class Events {
    private Events(){
    }

    public static final int OPERATION_CREATED = 0;
    public static final int OPERATION_DELETED = 1;

    public static final int ENTITY_CONTACT_REQUEST = 1;
    public static final int ENTITY_MESSAGE = 2;

    public static class OnNotificationReceivedEvent {
        public int operationType;
        public int entityType;
        public String entityId;
        public String entityOwnerId;
        public String entityOwnerName;

        public OnNotificationReceivedEvent(int operationType, int entityType, String entityId, String entityOwnerId, String entityOwnerName) {
            this.operationType = operationType;
            this.entityType = entityType;
            this.entityId = entityId;
            this.entityOwnerId = entityOwnerId;
            this.entityOwnerName = entityOwnerName;
        }
    }
}
