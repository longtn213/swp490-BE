package com.fpt.ssds.service.criteria;

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
public class UserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fullName;

    private StringFilter phoneNumber;

    private StringFilter username;

    private BooleanFilter gender;

    private StringFilter email;

    private BooleanFilter isActive;

    private StringFilter roleName;

    private StringFilter branchName;

    private StringFilter roleCode;

    private StringFilter branchCode;

    public UserCriteria(UserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.username = other.username == null ? null : other.username.copy();
        this.gender = other.gender == null ? null : other.gender.copy();
        this.isActive = other.isActive == null ? null : other.isActive.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.roleName = other.roleName == null ? null : other.roleName.copy();
        this.branchName = other.branchName == null ? null : other.branchName.copy();
        this.roleCode = other.roleCode == null ? null : other.roleCode.copy();
        this.branchCode = other.branchCode == null ? null : other.branchCode.copy();
    }

    @Override
    public Criteria copy() {
        return new UserCriteria(this);
    }
}
