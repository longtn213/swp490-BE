package com.fpt.ssds.service.criteria;

import com.fpt.ssds.utils.InstantFilter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ServiceCriteria implements Serializable, Criteria {
    static final long serialVersionUID = 1L;

    LongFilter id;

    StringFilter name;

    StringFilter code;

    StringFilter description;

    LongFilter duration;

    BooleanFilter isActive;

    StringFilter categoryCode;

    StringFilter equipmentCode;

    DoubleFilter currentPrice;

    StringFilter createdBy;

    InstantFilter createdDate;

    StringFilter lastModifiedBy;

    InstantFilter lastModifiedDate;

    LongFilter bookedCount;

    public ServiceCriteria(ServiceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.duration = other.duration == null ? null : other.duration.copy();
        this.isActive = other.isActive == null ? null : other.isActive.copy();
        this.currentPrice = other.currentPrice == null ? null : other.currentPrice.copy();
        this.categoryCode = other.categoryCode == null ? null : other.categoryCode.copy();
        this.equipmentCode = other.equipmentCode == null ? null : other.equipmentCode.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.bookedCount = other.bookedCount == null ? null : other.bookedCount.copy();
    }

    @Override
    public Criteria copy() {
        return new ServiceCriteria(this);
    }

    @Override
    public String toString() {
        return "ServiceCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (duration != null ? "duration=" + duration + ", " : "") +
            (isActive != null ? "isActive=" + isActive + ", " : "") +
            (currentPrice != null ? "currentPrice=" + currentPrice + ", " : "") +
            (categoryCode != null ? "categoryCode=" + categoryCode + ", " : "") +
            (equipmentCode != null ? "equipmentCode=" + equipmentCode + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            '}';
    }
}
