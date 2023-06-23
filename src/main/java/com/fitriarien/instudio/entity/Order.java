package com.fitriarien.instudio.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "order_date")
    private String orderDate;

    @Column(name = "visit_schedule")
    private String visitSchedule;

    @Column(name = "visit_address")
    private String visitAddress;

    @Column(name = "order_amount")
    private Double orderAmount;

    @Column(name = "order_status")
    private Long orderStatus;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetailList;

    @OneToMany(mappedBy = "order")
    private List<Payment> paymentList;
}
