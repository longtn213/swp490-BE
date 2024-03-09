package com.fpt.ssds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "voucher")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Voucher extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "value")
    private Double value;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "max_qty_per_user")
    private Long maxQtyPerUser;

    @Column(name = "min_order_value")
    private Long minOrderValue;

    @Column(name = "max_value_discount")
    private Long maxValueDiscount;
}
