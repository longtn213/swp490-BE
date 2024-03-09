package com.fpt.ssds.domain.enumeration;

public enum AppointmentMasterAction {
    CHECKIN("checkin"),
    CHECKOUT("checkout"),
    OTHER("other");

    private final String value;

    AppointmentMasterAction(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isExisted(String givenAction) {
        for (AppointmentMasterAction appointmentMasterAction : AppointmentMasterAction.values()) {
            if (appointmentMasterAction.getValue().equals(givenAction)) {
                return true;
            }
        }
        return false;
    }

    public static AppointmentMasterAction getAppointmentMasterAction(String action) {
        for (AppointmentMasterAction appointmentMasterAction : AppointmentMasterAction.values()) {
            if (appointmentMasterAction.getValue().equals(action)) {
                return appointmentMasterAction;
            }
        }
        return AppointmentMasterAction.OTHER;
    }
}
