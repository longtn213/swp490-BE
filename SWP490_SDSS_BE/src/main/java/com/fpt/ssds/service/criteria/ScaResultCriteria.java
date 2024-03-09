package com.fpt.ssds.service.criteria;

import com.fpt.ssds.utils.InstantFilter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ScaResultCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter CustomerId;

    private StringFilter customerName;

    private StringFilter customerPhoneNumber;

    private StringFilter statusCode;

    private StringFilter RepliedByName;

    private InstantFilter createdDate;

    @Override
    public Criteria copy() {
        return null;
    }
}
