package com.codingShuttle.projects.airBnbApp.dto;

import com.codingShuttle.projects.airBnbApp.entity.Hotel;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RoomDto {

    private Long id;
    private String type;

    private BigDecimal basePrice;

    private Integer totalCount;

    private Integer capacity;

    private String[] photos;

    private String[] amenities;


}
