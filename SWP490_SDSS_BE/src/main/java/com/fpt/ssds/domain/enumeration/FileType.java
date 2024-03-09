package com.fpt.ssds.domain.enumeration;

public enum FileType {
    SERVICE("service"),

    SCA_RESULT("sca-result"),

    BRANCH("branch"),

    INVOICE("invoice"),
    IMG_BEFORE("imgBefore"),
    IMG_AFTER("imgAfter"),
    AVATAR("avatar"),
    OTHER("OTHER");

    private final String value;

    FileType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isExisted(String type) {
        for (FileType fileType : FileType.values()) {
            if (fileType.getValue().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static FileType getFileType(String value) {
        for (FileType fileType : FileType.values()) {
            if (fileType.getValue().equals(value)) {
                return fileType;
            }
        }
        return FileType.OTHER;
    }
}
