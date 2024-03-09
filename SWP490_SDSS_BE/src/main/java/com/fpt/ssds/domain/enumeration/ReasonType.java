package com.fpt.ssds.domain.enumeration;

public enum ReasonType {
    CANCEL("cancel"),
    OTHER("other");
    private final String value;

    ReasonType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isExisted(String type) {
        for (ReasonType reasonType : ReasonType.values()) {
            if (reasonType.getValue().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static ReasonType getReasonType(String type) {
        for (ReasonType reasonType : ReasonType.values()) {
            if (reasonType.getValue().equals(type)) {
                return reasonType;
            }
        }
        return ReasonType.OTHER;
    }
}
