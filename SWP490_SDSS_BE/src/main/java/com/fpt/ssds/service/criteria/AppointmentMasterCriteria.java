package com.fpt.ssds.service.criteria;

import com.fpt.ssds.utils.InstantFilter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import javax.persistence.Column;
import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class AppointmentMasterCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter expectedStartTime;

    private InstantFilter expectedEndTime;

    private InstantFilter actualStartTime;

    private InstantFilter actualEndTime;

    private InstantFilter cancelTime;

    private StringFilter cancelBy;

    private DoubleFilter total;

    private DoubleFilter payAmount;

    private StringFilter branchCode;

    private LongFilter customerId;

    private StringFilter customerName;

    private StringFilter customerPhoneNumber;

    private StringFilter statusCode;

    private StringFilter note;

    private StringFilter overdueStatus;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private StringFilter cancelReason;

    private InstantFilter lastModifiedDate;

    private StringFilter paymentMethod;

    public AppointmentMasterCriteria(AppointmentMasterCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.expectedStartTime = other.expectedStartTime == null ? null : other.expectedStartTime.copy();
        this.expectedEndTime = other.expectedEndTime == null ? null : other.expectedEndTime.copy();
        this.actualStartTime = other.actualStartTime == null ? null : other.actualStartTime.copy();
        this.actualEndTime = other.actualEndTime == null ? null : other.actualEndTime.copy();
        this.cancelTime = other.cancelTime == null ? null : other.cancelTime.copy();
        this.cancelBy = other.cancelBy == null ? null : other.cancelBy.copy();
        this.total = other.total == null ? null : other.total.copy();
        this.payAmount = other.payAmount == null ? null : other.payAmount.copy();
        this.branchCode = other.branchCode == null ? null : other.branchCode.copy();
        this.note = other.note == null ? null : other.note.copy();
        this.cancelReason = other.cancelReason == null ? null : other.cancelReason.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.overdueStatus = other.overdueStatus == null ? null : other.overdueStatus.copy();
        this.paymentMethod = other.paymentMethod == null ? null : other.paymentMethod.copy();
    }

    @Override
    public Criteria copy() {
        return null;
    }
}
