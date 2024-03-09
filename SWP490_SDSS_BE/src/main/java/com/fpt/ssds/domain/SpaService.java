package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "service")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SpaService extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "booked_count")
    private Long bookedCount;

    @ManyToOne
    @JsonIgnoreProperties(value = "spaServices", allowSetters = true)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JsonIgnoreProperties(value = "spaServices", allowSetters = true)
    @JoinColumn(name = "equipment_type_id")
    private EquipmentType equipmentType;
}
