package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "equipment_type")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EquipmentType extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "equipmentType")
    @JsonIgnore
    private Set<EquipmentBranch> equipmentBranches = new HashSet<>();

    @OneToMany(mappedBy = "equipmentType")
    @JsonIgnore
    private Set<SpaService> spaServices = new HashSet<>();
}
