package com.fpt.ssds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sca_form")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ScaForm extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "`code`", unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "`is_active`")
    private boolean active;

    @OneToMany(mappedBy = "form")
    private List<ScaQuestion> questions = new ArrayList<>();
}
