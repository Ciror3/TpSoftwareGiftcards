package com.example.giftcards.giftcards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table (name= "merchants")
@Getter @Setter
public class Merchant extends ModelEntity<String> {
    @Column private String name;

    protected Merchant() {}

    public Merchant(String code, String name) {
        this.id = code;
        this.name = name;
    }
}
