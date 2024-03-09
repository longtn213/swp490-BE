package com.fpt.ssds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "voucher")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class VoucherUsage extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
