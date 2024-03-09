package com.fpt.ssds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "question_answer")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class QuestionAnswer extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text_answer")
    private String textAnswer;

    @Column(name = "text_question")
    private String textQuestion;

    @ManyToOne
    @JsonIgnoreProperties(value = "questionAnswers", allowSetters = true)
    @JoinColumn(name = "result_id")
    private ScaResult result;
}
