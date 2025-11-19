package com.example.giftcards.giftcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class GifCardFacadeTest {
    // Se espera que el usuario pueda inciar sesion con usuario y password y obtener un token
    //    debe poder usar el token para gestionar la tarjeta.
    //    el token se vence a los 5'

    // las giftcards ya estan definidas en el sistema.
    //    el usuario las reclama, pueden ser varias
    //    puede consultar el saldo y el detalle de gastos de sus tarjetas

    // los merchants pueden hacer cargos en las tarjetas que hayan sido reclamadas.
    //    los cargos se actualizan en el balance de las tarjetas

    @Autowired private GiftCardFacade facade;
    @Autowired private GiftCardService giftCardService;
    @Autowired private UserService userService;
    @Autowired private MerchantService merchantService;

    @MockBean private Clock clock;

    private void cleanAndLoadData() {
        giftCardService.findAll().forEach(giftCardService::delete);
        userService.findAll().forEach(userService::delete);

        userService.save(new UserVault("Bob", "BobPass"));
        userService.save(new UserVault("Kevin", "KevPass"));
        merchantService.save(new Merchant("M1", "Starbucks"));
        giftCardService.save(new GiftCard("GC1", 10));
        giftCardService.save(new GiftCard("GC2", 5));
    }

    @BeforeEach
    public void setUp()
    {
        when(clock.now()).thenReturn(LocalDateTime.now());
        cleanAndLoadData();
    }

    @Test public void userCanOpenASession() {
        assertNotNull( facade.login( "Bob", "BobPass" ) );
    }

    @Test public void unkownUserCannorOpenASession() {
        assertThrows( RuntimeException.class, () -> facade.login( "Stuart", "StuPass" ) );
    }

    @Test public void userCannotUseAnInvalidtoken() {
        assertThrows( RuntimeException.class, () -> facade.redeem( UUID.randomUUID(), "GC1" ) );
        assertThrows( RuntimeException.class, () -> facade.balance( UUID.randomUUID(), "GC1" ) );
        assertThrows( RuntimeException.class, () -> facade.details( UUID.randomUUID(), "GC1" ) );
    }

    @Test public void userCannotCheckOnAlienCard() {
        UUID token = facade.login( "Bob", "BobPass" );
        assertThrows( RuntimeException.class, () -> facade.balance( token, "GC1" ) );
    }

    @Test public void userCanRedeeemACard() {
        UUID token = facade.login( "Bob", "BobPass" );
        facade.redeem( token, "GC1" );
        assertEquals( 10, facade.balance( token, "GC1" ) );
    }

    @Test public void userCanRedeeemASecondCard() {
        UUID token = facade.login( "Bob", "BobPass" );
        facade.redeem( token, "GC1" );
        facade.redeem( token, "GC2" );

        assertEquals( 10, facade.balance( token, "GC1" ) );
        assertEquals( 5, facade.balance( token, "GC2" ) );
    }

    @Test public void multipleUsersCanRedeeemACard() {
        UUID bobsToken = facade.login( "Bob", "BobPass" );
        UUID kevinsToken = facade.login( "Kevin", "KevPass" );

        facade.redeem( bobsToken, "GC1" );
        facade.redeem( kevinsToken, "GC2" );

        assertEquals( 10, facade.balance( bobsToken, "GC1" ) );
        assertEquals( 5, facade.balance( kevinsToken, "GC2" ) );
    }

    @Test public void unknownMerchantCantCharge() {
        assertThrows( RuntimeException.class, () -> facade.charge( "Mx", "GC1", 2, "UnCargo" ) );

    }

    @Test public void merchantCantChargeUnredeemedCard() {
        assertThrows( RuntimeException.class, () -> facade.charge( "M1", "GC1", 2, "UnCargo" ) );
    }

    @Test public void merchantCanChargeARedeemedCard() {
        UUID token = facade.login( "Bob", "BobPass" );
        facade.redeem( token, "GC1" );
        facade.charge( "M1", "GC1", 2, "UnCargo" );

        assertEquals( 8, facade.balance( token, "GC1" ) );
    }

    @Test public void merchantCannotOverchargeACard() {
        UUID token = facade.login( "Bob", "BobPass" );
        facade.redeem( token, "GC1" );

        assertThrows( RuntimeException.class, () -> facade.charge( "M1", "GC1", 11, "UnCargo" ) );
    }

    @Test public void userCanCheckHisEmptyCharges() {
        UUID token = facade.login( "Bob", "BobPass" );
        facade.redeem( token, "GC1" );

        assertTrue( facade.details( token, "GC1" ).isEmpty() );
    }

    @Test public void userCanCheckHisCharges() {
        UUID token = facade.login( "Bob", "BobPass" );
        facade.redeem( token, "GC1" );
        facade.charge( "M1", "GC1", 2, "UnCargo" );

        assertEquals( "UnCargo", facade.details( token, "GC1" ).getLast() );
    }

    @Test public void userCannotCheckOthersCharges() {
        facade.redeem( facade.login( "Bob", "BobPass" ), "GC1" );
        UUID kevinToken = facade.login( "Kevin", "KevPass" );

        assertThrows( RuntimeException.class, () -> facade.details( kevinToken, "GC1" ) );
    }

    @Test public void tokenExpires() {
        when(clock.now()).thenReturn(LocalDateTime.now());
        UUID token = facade.login( "Kevin", "KevPass" );

        when(clock.now()).thenReturn(LocalDateTime.now().plusMinutes(16));
        assertThrows( RuntimeException.class, () -> facade.redeem( token, "GC1" ) );
    }

}
