package com.fpt.ssds.service.criteria;

import com.fpt.ssds.utils.InstantFilter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class AppointmentTrackingCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter time;

    private LongFilter maxQty;

    private LongFilter bookedQty;

    private BooleanFilter isAvailable;

    private StringFilter branchCode;

    @Override
    public Criteria copy() {
        return null;
    }

    public AppointmentTrackingCriteria(AppointmentTrackingCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.time = other.time == null ? null : other.time.copy();
        this.maxQty = other.maxQty == null ? null : other.maxQty.copy();
        this.bookedQty = other.bookedQty == null ? null : other.bookedQty.copy();
        this.isAvailable = other.isAvailable == null ? null : other.isAvailable.copy();
        this.branchCode = other.branchCode == null ? null : other.branchCode.copy();
    }
}
