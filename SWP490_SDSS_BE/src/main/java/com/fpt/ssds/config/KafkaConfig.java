////package com.fpt.ssds.config;
////
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Properties;
//
//@Configuration
//public class KafkaConfig {
//    Properties props = new Properties();
//    props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor");
//props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG + ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, confluent.monitoring.interceptor.bootstrap.servers, "<CCLOUD_BOOTSTRAP_SERVER>");
//props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG + ProducerConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
//props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG + SaslConfigs.SASL_MECHANISM, "PLAIN");
//props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG + SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username='<CCLOUD_API_KEY>' password='<CCLOUD_API_SECRET>';");
//props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor");
//props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG + ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, confluent.monitoring.interceptor.bootstrap.servers, "<CCLOUD_BOOTSTRAP_SERVER>");
//props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG + ProducerConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
//props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG + SaslConfigs.SASL_MECHANISM, "PLAIN");
//props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG + SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username='<CCLOUD_API_KEY>' password='<CCLOUD_API_SECRET>';");
//}
