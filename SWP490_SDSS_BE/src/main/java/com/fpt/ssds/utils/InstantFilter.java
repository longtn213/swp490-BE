package com.fpt.ssds.utils;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import tech.jhipster.service.filter.RangeFilter;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class InstantFilter extends RangeFilter<Instant> {
    private static final long serialVersionUID = 1L;

    protected String greater;
    protected String less;
    protected String greaterOrEqual;
    protected String lessOrEqual;

    public InstantFilter() {
    }

    public InstantFilter(InstantFilter filter) {
        super(filter);
    }

    public InstantFilter copy() {
        return new InstantFilter(this);
    }

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setEquals(Instant equals) {
        super.setEquals(equals);
        return this;
    }

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setNotEquals(Instant equals) {
        super.setNotEquals(equals);
        return this;
    }

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setIn(List<Instant> in) {
        super.setIn(in);
        return this;
    }

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setNotIn(List<Instant> notIn) {
        super.setNotIn(notIn);
        return this;
    }

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setGreaterThan(Instant equals) {
        super.setGreaterThan(equals);
        return this;
    }

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setLessThan(Instant equals) {
        super.setLessThan(equals);
        return this;
    }

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setGreaterThanOrEqual(Instant equals) {
        super.setGreaterThanOrEqual(equals);
        return this;
    }

    /**
     * @deprecated
     */
    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    @Deprecated
    public InstantFilter setGreaterOrEqualThan(Instant equals) {
        super.setGreaterThanOrEqual(equals);
        return this;
    }

    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setLessThanOrEqual(Instant equals) {
        super.setLessThanOrEqual(equals);
        return this;
    }

    /**
     * @deprecated
     */
    @DateTimeFormat(
        iso = DateTimeFormat.ISO.DATE_TIME
    )
    @Deprecated
    public InstantFilter setLessOrEqualThan(Instant equals) {
        super.setLessThanOrEqual(equals);
        return this;
    }

    public String getGreater() {
        return greater;
    }

    public void setGreater(String greater) {
        if (!StringUtils.isEmpty(greater)) {
            Instant instant = DateUtils.stringTimeWithZoneToDate(greater);
            if (Objects.isNull(instant)) {
                throw new SSDSBusinessException(ErrorConstants.INPUT_INVALID);
            }
            this.setGreaterThan(instant);
        }
        this.greater = greater;
    }

    public String getLess() {
        return less;
    }

    public void setLess(String less) {
        if (!StringUtils.isEmpty(less)) {
            Instant instant = DateUtils.stringTimeWithZoneToDate(less);
            if (Objects.isNull(instant)) {
                throw new SSDSBusinessException(ErrorConstants.INPUT_INVALID);
            }
            this.setLessThan(instant);
        }
        this.less = less;
    }

    public String getGreaterOrEqual() {
        return greaterOrEqual;
    }

    public void setGreaterOrEqual(String greaterOrEqual) {
        if (!StringUtils.isEmpty(greaterOrEqual)) {
            Instant instant = DateUtils.stringTimeWithZoneToDate(greaterOrEqual);
            if (Objects.isNull(instant)) {
                throw new SSDSBusinessException(ErrorConstants.INPUT_INVALID);
            }
            this.setGreaterThanOrEqual(instant);
        }
        this.greaterOrEqual = greaterOrEqual;
    }

    public String getLessOrEqual() {
        return lessOrEqual;
    }

    public void setLessOrEqual(String lessOrEqual) {
        if (!StringUtils.isEmpty(lessOrEqual)) {
            Instant instant = DateUtils.stringTimeWithZoneToDate(lessOrEqual);
            if (Objects.isNull(instant)) {
                throw new SSDSBusinessException(ErrorConstants.INPUT_INVALID);
            }
            this.setLessThanOrEqual(instant);
        }
        this.lessOrEqual = lessOrEqual;
    }
}
