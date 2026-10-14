package com.codingShuttle.projects.airBnbApp.service;

import com.codingShuttle.projects.airBnbApp.dto.HotelDto;
import com.codingShuttle.projects.airBnbApp.dto.HotelInfoDto;
import com.codingShuttle.projects.airBnbApp.entity.Hotel;
import org.jspecify.annotations.Nullable;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hoteldto);

    HotelDto activatedHotel(Long hotelId);

    void deleteHotelById(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);
}
