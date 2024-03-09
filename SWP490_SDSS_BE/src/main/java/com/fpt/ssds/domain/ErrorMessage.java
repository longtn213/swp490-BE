package com.fpt.ssds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "error_message")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ErrorMessage extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "return_code", unique = true)
    private String returnCode;

    @Column(name = "message_vi")
    private String messageVi;

    @Column(name = "message_en")
    private String messageEn;

}
