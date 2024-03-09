package com.fpt.ssds.service;

import com.fpt.ssds.domain.Branch;

import java.time.Instant;

public interface PerformanceMetricService {

    void summaryMetricInDay(Branch branch, Instant time);
}
