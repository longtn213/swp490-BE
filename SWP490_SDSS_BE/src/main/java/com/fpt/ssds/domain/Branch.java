package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "branch")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Branch extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "country")
    private String country;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "hotline")
    private String hotline;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "branch")
    @JsonIgnore
    private Set<ConfigData> configDataSet = new HashSet<>();

    @OneToMany(mappedBy = "branch")
    @JsonIgnore
    private Set<EquipmentBranch> equipmentBranches = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "branches", allowSetters = true)
    @JoinColumn(name = "state_id")
    private Location state;

    @ManyToOne
    @JsonIgnoreProperties(value = "branches", allowSetters = true)
    @JoinColumn(name = "city_id")
    private Location city;

    @ManyToOne
    @JsonIgnoreProperties(value = "branches", allowSetters = true)
    @JoinColumn(name = "district_id")
    private Location district;

    @OneToMany(mappedBy = "branch")
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}
