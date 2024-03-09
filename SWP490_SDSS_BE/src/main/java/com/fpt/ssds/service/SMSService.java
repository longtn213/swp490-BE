package com.fpt.ssds.service;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.ValidationRequest;
import com.twilio.type.PhoneNumber;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSService {
    @Value("${ssds.twilio.account_sid}")
    String accountSid;

    @Value("${ssds.twilio.auth_token}")
    String authToken;

    @Value("${ssds.twilio.phone_number}")
    String serverPhoneNumber;

    public void sendMessage(String toPhoneNumber) {
        if (toPhoneNumber.startsWith("0")) {
            toPhoneNumber = toPhoneNumber.replace("0", "+84");
        }

        Twilio.init(accountSid, authToken);
        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(serverPhoneNumber),
                "Test phần gửi SMS .-. b có nhận được tin nhắn này khum?")
            .create();
    }

    public void sendMessage(String textMessage, String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.replace("0", "+84");
        }

        try {
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(serverPhoneNumber),
                    textMessage)
                .create();
        } catch (Exception e) {
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                if (StringUtils.contains(apiException.getMessage(), "is not a mobile number")) {
                    throw new SSDSBusinessException(null, "Số điện thoại " + phoneNumber + " không tồn tại. Vui lòng kiểm tra và thử lại.");
                }
            }
        }
    }
}
