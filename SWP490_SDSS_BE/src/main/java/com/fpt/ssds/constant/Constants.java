package com.fpt.ssds.constant;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String FAKE_EMAIL_PREFIX = "ssdsFakeEmail";

    private Constants() {
    }

    public class COMMON_TYPE {
        public static final String SERVICE_CATEGORY = "serviceCategory";
        public static final String EQUIPMENT_TYPE = "equipmentType";
        public static final String APPOINTMENT_MASTER_STATUS = "apptMasterStatus";
        public static final String APPOINTMENT_SERVICE_STATUS = "apptServiceStatus";
        public static final String OVERDUE_STATUS = "overdueStatus";

        public static final String REASON_MESSAGE = "reasonMessage";

        public static final String CONFIRM_ACTION = "confirmAction";
        public static final String AVAILABLE_STATUS = "availableStatus";
        public static final String BRANCH = "branch";
        public static final String PAYMENT_METHOD = "paymentMethod";
    }

    public class MESSAGE_TYPE {
        public static final String CANCEL = "CANCEL";
    }

    public class CONFIG_KEY {
        public static final String MAX_CUSTOMER_A_TIME = "MAX_CUSTOMER_A_TIME";
        public static final String PERIOD_BETWEEN_APPOINTMENT = "PERIOD_BETWEEN_APPOINTMENT";
        public static final String MAX_PERIOD_BOOKING_TIME = "MAX_PERIOD_BOOKING_TIME";
        public static final String START_WORKING_TIME_IN_DAY = "START_WORKING_TIME_IN_DAY";
        public static final String END_WORKING_TIME_IN_DAY = "END_WORKING_TIME_IN_DAY";

        public static final String APPOINTMENT_MASTER_REQUIRE_CONFIRMATION = "APPOINTMENT_MASTER_REQUIRE_CONFIRMATION";

        public static final String DURATION_AUTO_GEN_APPOINTMENT_TRACKING_TIME = "DURATION_AUTO_GEN_APPOINTMENT_TRACKING_TIME";
        public static final String AM_INTERVAL_ALLOW_LATE = "AM_INTERVAL_ALLOW_LATE";
    }

    public class BATCH {
        public static final String MIN_DATE_MILLISECONDS = "15829904000";
        public static final String JOB_APPOINTMENT_TRACKING = "JOB_APPOINTMENT_TRACKING";
        public static final String NEXT_BEGIN_TIME = "NEXT_BEGIN_TIME";
    }

    public class COMMON {
        public static final String USER = "ATT-USER";
        public static final String USER_BRANCH = "ATT-USER-BRANCH";
        public static final String REQUEST_INFO = "ATT-REQUEST-INFO";
        public static final String REQUEST_ID = "ATT-REQUEST-ID";
        public static final String HEADER_REQUEST_ID = "X-SSDS-RequestID";
    }

    public class APPOINTMENT_MASTER_STATUS {
        public static final String WAITING_FOR_CONFIRMATION = "WAITING_FOR_CONFIRMATION";
        public static final String READY = "READY";
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELED = "CANCELED";
        public static final String PENDING = "PENDING";
        public static final String CLOSED = "CLOSED";
    }

    public class APPOINTMENT_SERVICE_STATUS {
        public static final String WAITING_FOR_CONFIRMATION = "WAITING_FOR_CONFIRMATION";
        public static final String READY = "READY";
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELED = "CANCELED";
    }

    public class LOOKUP_KEY {
        public static final String APPOINTMENT_MASTER_STATUS = "APPOINTMENT_MASTER_STATUS";
        public static final String APPOINTMENT_SERVICE_STATUS = "APPOINTMENT_SERVICE_STATUS";
        public static final String OVERDUE_STATUS = "OVERDUE_STATUS";

        public static final String ACTION_CONFIRM = "ACTION_CONFIRM";

        public static final String FORM_RESULT_STATUS = "FORM_RESULT_STATUS";

        public static final String AVAILABLE_STATUS = "AVAILABLE_STATUS";
        public static final String PAYMENT_METHOD = "PAYMENT_METHOD";

    }

    public class FORM_RESULT_STATUS {

        public static final String WAITING_FOR_RESULT = "WAITING_FOR_RESULT";

        public static final String DONE_ANSWER = "DONE_ANSWER";
    }

    public class ROLE {
        public static final String CUSTOMER = "CUSTOMER";
        public static final String MANAGER = "MANAGER";
        public static final String RECEPTIONIST = "RECEPTIONIST";

        public static final String SPECIALIST = "SPECIALIST";

    }

    public class ACTION_CONFIRM {
        public static final String CONFIRM = "CONFIRM";
        public static final String CANCEL = "CANCEL";
    }

    public class REF_TYPE {
        public static final String APPOINTMENT_MASTER = "Appointment master";
        public static final String APPOINTMENT_SERVICE = "Appointment service";
    }

    public class BRANCH {
        public static final String SKIN_WISDOM_YEN_LANG = "SKIN_WISDOM_YEN_LANG";
    }

    public class CONFIG_TYPE {
        public static final String TIME = "time";
        public static final String NUMBER = "number";
        public static final String TEXT = "text";
    }

    public class OVERDUE_STATUS {
        public static final String OVERDUE = "OVERDUE";
        public static final String ONTIME = "ONTIME";
        public static final String OVERDUESOON = "OVERDUESOON";
    }

    public class REQUEST_HEADER {
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String BRANCH_CODE_HEADER = "Branch-code";
    }

    public class KAFKA_TYPE {
        public static final String AM_CONFIRMED = "AM_CONFIRMED";
    }

    public class PERIOD {
        public static final String MONTH = "month";
        public static final String WEEK = "week";
        public static final String DAY = "day";
    }
}
