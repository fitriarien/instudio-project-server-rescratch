package com.fitriarien.instudio.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "payment_date")
    private String paymentDate;

    @Column(name = "payment_amount")
    private Double paymentAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_detail")
    private String paymentDetail;

    @Column(name = "account_number")
    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;
}
