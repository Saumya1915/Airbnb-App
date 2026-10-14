package com.codingShuttle.projects.airBnbApp.service;

import com.codingShuttle.projects.airBnbApp.dto.RoomDto;
import com.codingShuttle.projects.airBnbApp.entity.Hotel;
import com.codingShuttle.projects.airBnbApp.entity.Room;
import com.codingShuttle.projects.airBnbApp.exception.ResourceNotFound;
import com.codingShuttle.projects.airBnbApp.repository.HotelRepository;
import com.codingShuttle.projects.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

   private final RoomRepository roomRepository;
   private final HotelRepository hotelRepository;
   private final ModelMapper modelMapper;
   private final InventoryService inventoryService;


    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {

        log.info("Creating a room with name"+ roomDto.getId());
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(
                        ()->new ResourceNotFound("Hotel id not found"));
        Room room= modelMapper.map(roomDto, Room.class);
        //hotel.setActive(false);//onboarded on our platform, no yet active on inventory and search
        room.setHotel(hotel);
        room=roomRepository.save(room);
        log.info("Created a room with Id"+ roomDto.getId());

        if(hotel.getActive()){
            inventoryService.intializeRoomForOneYear(room);
        }
        return modelMapper.map(room, RoomDto.class);
    }


    @Override
    public List<RoomDto> getAllRoomInHotel(Long hotelId) {
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(
                        ()->new ResourceNotFound("Hotel id not found"));

        return hotel.getRooms()
                .stream()
                .map((element)->modelMapper. map(element, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {

        Room room=roomRepository
                .findById(roomId)
                .orElseThrow(
                        ()->new ResourceNotFound("Room id not found"));

        return modelMapper.map(room, RoomDto.class);

    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        Room room=roomRepository
                .findById(roomId)
                .orElseThrow(
                        ()->new ResourceNotFound("Room id not found"));
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);

    }



}
