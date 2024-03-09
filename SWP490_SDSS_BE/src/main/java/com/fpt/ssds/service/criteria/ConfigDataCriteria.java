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
public class ConfigDataCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter configKey;

    private StringFilter configValue;

    private StringFilter branchCode;

    private LongFilter branchId;

    private StringFilter createdBy;

    private BooleanFilter allowUpdate;

    private StringFilter type;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    public ConfigDataCriteria(ConfigDataCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.configKey = other.configKey == null ? null : other.configKey.copy();
        this.configValue = other.configValue == null ? null : other.configValue.copy();
        this.branchCode = other.branchCode == null ? null : other.branchCode.copy();
        this.branchId = other.branchId == null ? null : other.branchId.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.allowUpdate = other.allowUpdate == null ? null : other.allowUpdate.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
    }

    @Override
    public Criteria copy() {
        return new ConfigDataCriteria(this);
    }
}
