package com.codingShuttle.projects.airBnbApp.dto;

import com.codingShuttle.projects.airBnbApp.entity.User;
import com.codingShuttle.projects.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDto {

    private Long id;
    private User user;
    private Integer age;
    private Gender gender;
}
