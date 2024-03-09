package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sca_question")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ScaQuestion extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`question`")
    private String question;

    @Column(name = "`order`")
    private Integer order;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "`type`")
    private Long type;

    @ManyToOne
    @JsonIgnoreProperties(value = "scaQuestions", allowSetters = true)
    @JoinColumn(name = "form_id")
    private ScaForm form;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Option> options = new ArrayList<>();
}
