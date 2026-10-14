package com.codingShuttle.projects.airBnbApp.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import com.codingShuttle.projects.airBnbApp.entity.HotelContactInfo;


@Data
@Getter
@Setter
public class HotelDto {

    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;

}
