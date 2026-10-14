package com.codingShuttle.projects.airBnbApp.service;

import com.codingShuttle.projects.airBnbApp.dto.BookingDto;
import com.codingShuttle.projects.airBnbApp.dto.BookingRequestDto;
import com.codingShuttle.projects.airBnbApp.dto.GuestDto;
import com.codingShuttle.projects.airBnbApp.entity.*;
import com.codingShuttle.projects.airBnbApp.entity.enums.BookingStatus;
import com.codingShuttle.projects.airBnbApp.exception.ResourceNotFound;
import com.codingShuttle.projects.airBnbApp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;


    @Override
    @Transactional
    public BookingDto intialiseBooking(BookingRequestDto bookingRequestDto) {
        log.info("Intialising booking for hotel : {}, room : {}, date {}-{}", bookingRequestDto.getHotelId(),
        bookingRequestDto.getRoomId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());

        Hotel hotel=hotelRepository
                .findById(bookingRequestDto.getHotelId())
                .orElseThrow(
                        ()->new ResourceNotFound("Hotel id not found"));

        Room room=roomRepository
                .findById(bookingRequestDto.getRoomId())
                .orElseThrow(
                        ()->new ResourceNotFound("Room id not found"));

        List<Inventory> inventoryList=inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequestDto.getCheckInDate(),bookingRequestDto.getCheckOutDate(),
                bookingRequestDto.getRoomsCount());

        Long daysCount= ChronoUnit.DAYS.between(bookingRequestDto.getCheckInDate(),bookingRequestDto.getCheckOutDate())+1;

        if(inventoryList.size()!=daysCount){
            throw new IllegalStateException("Room is not available");
        }

        //Reserve the room/ update the booked count of inventories

        for(Inventory inventory:inventoryList){
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequestDto.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        //create the booking



        Booking booking=Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .roomCount(bookingRequestDto.getRoomsCount())
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .amount(BigDecimal.TEN)
                .user(getCurrentUser())
                .build();

        booking=bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);



    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guests for booking with Id: ", bookingId);

        Booking booking=bookingRepository
                .findById(bookingId)
                .orElseThrow(
                        ()->new ResourceNotFound("Hotel id not found"));

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Sorry! Booking has expired already.");
        }

        if(booking.getBookingStatus()!=BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state, so you cannot add guest");
        }

        for(GuestDto guestdto:guestDtoList ){
            Guest guest=modelMapper.map(guestdto, Guest.class);
            guest.setUser(getCurrentUser());
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);

        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking=bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        User user=new User();
        user.setId(1L);
        return user;
    }
}
