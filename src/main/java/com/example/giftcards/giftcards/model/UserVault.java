package com.example.giftcards.giftcards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;


@Entity
@Table (name= "users")
@Getter @Setter
public class UserVault extends ModelEntity<Long>{
    @Column(unique = true, nullable = false) private String username;
    @Column(nullable = false) private String password;

    protected UserVault(){}
    public UserVault(String username, String password) {
        this.id = new Random().nextLong();
        this.username = username;
        this.password = password;
    }
}

