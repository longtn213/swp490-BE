package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "equipment_branch")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EquipmentBranch extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = "equipmentBranches", allowSetters = true)
    @JoinColumn(name = "equipment_type_id")
    private EquipmentType equipmentType;

    @ManyToOne
    @JsonIgnoreProperties(value = "equipmentBranches", allowSetters = true)
    @JoinColumn(name = "branch_id")
    private Branch branch;
}
