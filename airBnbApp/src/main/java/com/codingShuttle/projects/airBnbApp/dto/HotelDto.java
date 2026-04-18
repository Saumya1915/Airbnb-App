package com.codingShuttle.projects.airBnbApp.dto;

import com.codingShuttle.projects.airBnbApp.entity.HotelContactInfo;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class HotelDto {

    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;

}
