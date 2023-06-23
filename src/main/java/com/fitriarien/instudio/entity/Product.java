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
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_model")
    private String productModel;

    @Column(name = "cost_estimation")
    private Double costEstimation;

    @Column(name = "product_status")
    private Long productStatus;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "product")
    private List<Image> images;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetailList;
}
