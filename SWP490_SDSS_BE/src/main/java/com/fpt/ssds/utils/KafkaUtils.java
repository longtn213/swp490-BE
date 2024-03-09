package com.fpt.ssds.utils;

import com.fpt.ssds.service.dto.kafka.KafkaDataDTO;
import com.fpt.ssds.service.dto.kafka.KafkaMetadataDTO;

import java.util.Date;

public class KafkaUtils {

    public static KafkaDataDTO buildKafkaDataDTO(String type, String branchCode, Object data) {
        KafkaMetadataDTO meta = new KafkaMetadataDTO();
        meta.setType(type);
        meta.setRequestId(Utils.generateUUID());
        meta.setTimestamp((new Date().getTime()));
        return new KafkaDataDTO(meta, data);
    }
}
