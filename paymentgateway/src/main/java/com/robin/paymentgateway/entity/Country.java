package com.robin.paymentgateway.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Country {
    @Id
    private String code;

    @Column
    private String name;

    @Column
    private String region;

    @Column
    private String continent;


}
