package com.fpt.ssds.service.kafka;

public interface KafkaProducerService<V> {

    void send(String topic, Object message);

    void send(String topic, String key, Object message);
}
