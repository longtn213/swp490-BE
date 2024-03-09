package com.fpt.ssds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "location")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Location extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "division_name")
    private String divisionName;

    @Column(name = "division_code")
    private String divisionCode;

    @Column(name = "division_level")
    private String divisionLevel;

    @Column(name = "division_parent_id")
    private Long divisionParentId;
}
