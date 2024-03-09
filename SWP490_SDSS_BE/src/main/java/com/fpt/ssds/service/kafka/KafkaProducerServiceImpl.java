package com.fpt.ssds.service.kafka;

import com.fpt.ssds.utils.JsonSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void send(String topic, Object message) {
        String msgContent = JsonSupport.toJson(message);
        if (StringUtils.isEmpty(topic)) {
            return;
        }
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, msgContent);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("[PUBLIC] [PRODUCER] Sent message=[" + msgContent +
                    "] with partition=[" + result.getRecordMetadata().partition() +
                    "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("[PUBLIC] [PRODUCER] Unable to send message=["
                    + msgContent + "] due to : " + ex.getMessage());
            }
        });

    }

    @Override
    public void send(String topic, String key, Object message) {
        String msgContent = JsonSupport.toJson(message);
        if (StringUtils.isEmpty(topic)) {
            return;
        }
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, msgContent);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("[PRODUCER] Sent message=[" + msgContent +
                    "] with key=[" + key +
                    "] with partition=[" + result.getRecordMetadata().partition() +
                    "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("[PRODUCER] Unable to send message=[" + msgContent + "] due to : " + ex.getMessage());
            }
        });
    }
}
