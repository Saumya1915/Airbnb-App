package com.codingShuttle.projects.airBnbApp.entity;

import com.codingShuttle.projects.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Guest {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
