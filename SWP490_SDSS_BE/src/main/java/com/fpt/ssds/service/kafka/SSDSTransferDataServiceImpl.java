package com.fpt.ssds.service.kafka;

import com.fpt.ssds.service.dto.kafka.KafkaDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SSDSTransferDataServiceImpl implements SSDSTransferDataService {

//    @Value("${spring.kafka.topics.state-change}")
//    private String topicName;
//
//    @Autowired
//    private KafkaProducerService kafkaProducerService;
//
//    @Override
//    public void transferData(KafkaDataDTO data) {
//        kafkaProducerService.send(topicName, data);
//    }
}
