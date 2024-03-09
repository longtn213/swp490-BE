package com.fpt.ssds.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Notification extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "deep_link")
    private String deepLink;

    @Column(name = "is_seen")
    private Boolean isSeen;

    @ManyToMany
    @JoinTable(name = "notification_user",
        joinColumns = @JoinColumn(name = "notification_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> users = new HashSet<>();
}
