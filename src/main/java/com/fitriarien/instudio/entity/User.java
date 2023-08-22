package com.fitriarien.instudio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    private String role;

    private String name;

    private String email;

    private String phone;

    private String address;

    private Long status;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Product> products;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Image> images;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orderList;
}
