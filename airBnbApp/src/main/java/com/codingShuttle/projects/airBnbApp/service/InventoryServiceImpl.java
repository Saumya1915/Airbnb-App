package com.codingShuttle.projects.airBnbApp.service;

import com.codingShuttle.projects.airBnbApp.dto.HotelDto;
import com.codingShuttle.projects.airBnbApp.dto.HotelSearchRequest;
import com.codingShuttle.projects.airBnbApp.entity.Hotel;
import com.codingShuttle.projects.airBnbApp.entity.Inventory;
import com.codingShuttle.projects.airBnbApp.entity.Room;
import com.codingShuttle.projects.airBnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;


    @Override
    public void intializeRoomForOneYear(Room room) {

        LocalDate today=LocalDate.now();
        LocalDate endDate=today.plusYears(1);
        for(; !today.isAfter(endDate); today=today.plusDays(1)){
            Inventory inventory=Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false).build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteAllInventories(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {

        Pageable pageable= PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount= ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate())+1;

        //ChronoUnit is an enum in Java's java.time.temporal package that represents a unit of time:
        // DAYS, HOURS, MINUTES, MONTHS, YEARS, and so on.
        // Its most common use is measuring how much time lies between two
        // dates or times.
        Page<Hotel> hotelPage=inventoryRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(),
                             hotelSearchRequest.getEndDate(), hotelSearchRequest.getRoomsCount(),
                (int) dateCount, pageable);
        return hotelPage.map((element)->modelMapper.map(element,HotelDto.class));
    }
}
