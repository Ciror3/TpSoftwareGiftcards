package com.example.giftcards.giftcards.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserVault, Long> {

    Optional<UserVault> findByUsername(String username );

}
