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
@Table(name = "images")
public class Image {

    @Id
    @Column(name = "image_id")
    private String imageId;

    @Column(name = "image_alt")
    private String imageAlt;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "image_status")
    private Long imageStatus;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
