package com.fpt.ssds.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "lookup")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Lookup extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lookup_key")
    private String lookupKey;

    @Column(name = "lookup_value")
    private String code;

    @Column(name = "display_val")
    private String name;

    @Column(name = "description")
    private String description;
}
