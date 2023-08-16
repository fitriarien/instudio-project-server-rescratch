package com.fitriarien.instudio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @Column(name = "order_det_id")
    private String orderDetId;

    @Column(name = "time_estimation")
    private Long timeEstimation;

    private Double subtotal;

    @Column(name = "product_size")
    private Double productSize;

    @Column(name = "product_theme")
    private String productTheme;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @JsonIgnore
    private Product product;
}
