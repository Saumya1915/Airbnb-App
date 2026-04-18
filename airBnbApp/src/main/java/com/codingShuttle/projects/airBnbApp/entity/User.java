package com.codingShuttle.projects.airBnbApp.entity;

import com.codingShuttle.projects.airBnbApp.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="app_user")// because it won't allow us to create
// username table as internally it has a user table
public class User {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    @ElementCollection(fetch = FetchType.LAZY)
    //@ElementCollection
    //Tells JPA this is a collection of value types, not entities
    //Since Role is an enum (not a full @Entity),
    // it can't use @OneToMany — so @ElementCollection is used instead
    //fetch = FetchType.LAZY means roles are not loaded from DB until you actually access the role field
    @Enumerated(EnumType.STRING)// haven't used ordinal here
    //becuase then there are 2 roles, which would have been assigned value 1 and 2
    //better to have String
    private Set<Role> role;
}
