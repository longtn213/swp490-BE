package com.fpt.ssds.service.dto;

import java.time.Instant;

public interface MetricPerHour {
    Integer getTime();

    Integer getNumOfAm();

    Double getTotal();

    String getType();
}
