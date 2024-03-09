package com.fpt.ssds.service.kafka;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.service.AppointmentMasterService;
import com.fpt.ssds.service.dto.kafka.KafkaDataDTO;
import com.fpt.ssds.utils.JsonSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaStateChangeListener {
//    private final AppointmentMasterService appointmentMasterService;
//
//    @KafkaListener(topics = "${spring.kafka.topics.state-change}", groupId = "${spring.kafka.groups.state-change}")
//    public void consumeStateChange(String data,
//                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//                                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
//        log.info(String.format("[SSDS] [CONSUMER] [STATE_CHANGE] - Topic: %s - Message: %s - Key: %s", topic, data, key));
//        try {
//            KafkaDataDTO kafkaDataDTO = JsonSupport.toObject(data, KafkaDataDTO.class);
//            String type = kafkaDataDTO.getMetadata().getType();
//            if (Constants.KAFKA_TYPE.AM_CONFIRMED.equals(type)) {
//                Long amId = JsonSupport.toObject(kafkaDataDTO.getData(), Long.class);
//                String branchCode = kafkaDataDTO.getMetadata().getBranchCode();
//                appointmentMasterService.sendConfirmMessage(amId, branchCode);
//                return;
//            }
//        } catch (Exception e) {
//            log.error("KafkaListener topic: {} with error {}", topic, e.getMessage(), e);
//        }
//    }
}
