package com.codingShuttle.projects.airBnbApp.service;

import com.codingShuttle.projects.airBnbApp.dto.HotelDto;
import com.codingShuttle.projects.airBnbApp.dto.HotelInfoDto;
import com.codingShuttle.projects.airBnbApp.dto.RoomDto;
import com.codingShuttle.projects.airBnbApp.entity.Hotel;
import com.codingShuttle.projects.airBnbApp.entity.Room;
import com.codingShuttle.projects.airBnbApp.exception.ResourceNotFound;
import com.codingShuttle.projects.airBnbApp.repository.HotelRepository;
import com.codingShuttle.projects.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;



    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a hotel with name"+ hotelDto.getName());
        Hotel hotel=modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);//onboarded on our platform, no yet active on inventory and search
        hotel=hotelRepository.save(hotel);
        log.info("Created a hotel with Id"+ hotelDto.getId());
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with id");
        Hotel hotel=hotelRepository.findById(id).orElseThrow(()->new ResourceNotFound("Hotel id not found"));
        return modelMapper.map(hotel,HotelDto.class);

    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hoteldto) {

        Hotel hotel=hotelRepository
                .findById(id)
                .orElseThrow(
                        ()->new ResourceNotFound("Hotel id not found"));
        modelMapper.map(hoteldto,hotel);
        hotel.setId(id);
        hotel= hotelRepository.save(hotel);
        return modelMapper.map(hotel,HotelDto.class);


    }

    @Override
    @Transactional
    public HotelDto activatedHotel(Long hotelId) {

        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(
                        ()->new ResourceNotFound("Hotel id not found"));
        hotel.setActive(true);

        for(Room room: hotel.getRooms())
        {
            inventoryService.intializeRoomForOneYear(room);
        }

        return modelMapper.map(hotel, HotelDto.class);

    }

    @Override
    @Transactional
    public void deleteHotelById(Long hotelId) {
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(
                        ()->new ResourceNotFound("Hotel id not found"));

        for(Room room: hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(hotelId);


    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(
                        ()->new ResourceNotFound("Hotel id not found"));

        List<RoomDto> rooms= hotel.getRooms()
                .stream()
                .map((element)->modelMapper.map(element,RoomDto.class))
                .toList();

        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);


    }


}
