package com.codingShuttle.projects.airBnbApp.entity;

import com.codingShuttle.projects.airBnbApp.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String transcationId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false, precision = 10,scale=2)
    private BigDecimal amount;

}
