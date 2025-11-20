package com.example.giftcards.giftcards.model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface GiftCardRepository extends JpaRepository<GiftCard, String> {
    Optional<GiftCard> findByOwner(UserVault owner);

}
