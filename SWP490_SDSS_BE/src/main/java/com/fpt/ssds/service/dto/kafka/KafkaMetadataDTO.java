package com.fpt.ssds.service.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMetadataDTO implements Serializable {
    private String type;
    private String requestId;
    private long timestamp;
    private String branchCode;
}

