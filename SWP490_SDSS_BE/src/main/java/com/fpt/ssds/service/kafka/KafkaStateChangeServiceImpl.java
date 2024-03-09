package com.fpt.ssds.service.kafka;

import com.fpt.ssds.service.dto.kafka.KafkaDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaStateChangeServiceImpl implements KafkaStateChangeService {

//    @Value("${spring.kafka.topics.state-change}")
//    private String topicName;
//
//    private final KafkaProducerService kafkaProducerService;
//
//    @Override
//    public void notifyStateChange(KafkaDataDTO data) {
//        kafkaProducerService.send(topicName, data.getMetadata().getBranchCode(), data);
//    }
//
//    @Override
//    public void notifyStateChangeWithKey(KafkaDataDTO data, String key) {
//        kafkaProducerService.send(topicName, key, data);
//    }
}
