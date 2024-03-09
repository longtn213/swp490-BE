package com.fpt.ssds.domain.enumeration;

public enum StorageSource {
    GCS("GCS"),
    IMGUR("IMGUR"),
    OTHER("OTHER");

    private final String value;

    StorageSource(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StorageSource getStorageSource(String value) {
        for (StorageSource attachmentType : StorageSource.values()) {
            if (attachmentType.getValue().equals(value)) {
                return attachmentType;
            }
        }
        return StorageSource.OTHER;
    }
}
