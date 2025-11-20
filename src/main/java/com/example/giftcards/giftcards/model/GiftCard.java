package com.example.giftcards.giftcards.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "gift_cards") // Es buena práctica nombrar la tabla explícitamente en plural
@Getter @Setter
public class GiftCard extends ModelEntity<String> {

    public static final String CargoImposible = "CargoImposible";
    public static final String InvalidCard = "InvalidCard";

    @Column
    private int balance;

    // Lazy: No traemos los datos del usuario cada vez que cargamos la tarjeta, solo si se piden.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserVault owner;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "giftcard_charges", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "charges_description")
    private List<String> charges = new ArrayList<>();

    protected GiftCard() {}

    public GiftCard(String id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public GiftCard charge(int anAmount, String description) {
        if (!owned() || (balance - anAmount < 0)) {
            throw new RuntimeException(CargoImposible);
        }

        balance = balance - anAmount;
        charges.add(description);

        return this;
    }

    public GiftCard redeem(UserVault newOwner) {
        if (owned()) {
            throw new RuntimeException(InvalidCard);
        }

        this.owner = newOwner;
        return this;
    }

    public boolean owned() {
        return owner != null;
    }

    public boolean isOwnedBy(UserVault aPossibleOwner) {
        return owner != null && owner.equals(aPossibleOwner);
    }

    public String id() {
        return id;
    }

    public int balance() {
        return balance;
    }

    public List<String> charges() {
        return charges;
    }

    // Nota: Como tienes @Getter de Lombok, también tendrás getOwner(), getBalance(), etc. disponibles.
}