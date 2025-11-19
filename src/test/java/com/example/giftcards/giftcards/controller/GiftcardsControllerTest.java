package com.example.giftcards.giftcards.controller;

import com.example.giftcards.giftcards.model.*;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(GiftcardsController.class)
class GiftcardsControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private GiftCardService giftCardService;
    @Autowired private UserService userService;
    @Autowired private MerchantService merchantService;


    private final String user = "Johnny";
    private final String password = "jojo";

    @BeforeEach
    public void setUp() {
        cleanAndLoadData();
    }

    private void cleanAndLoadData() {
        giftCardService.findAll().forEach(giftCardService::delete);
        userService.findAll().forEach(userService::delete);

        userService.save(new UserVault(user, password));
        merchantService.save(new Merchant("M1", "Starbucks"));
        giftCardService.save(new GiftCard("GC1", 10));
    }

    @Test
    void test01LoginCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);
        assertDoesNotThrow(() -> UUID.fromString(token));
    }

        @Test
    void test02LoginIncorrectPass() throws Exception {
        mockMvc.perform(
                    post("/login")
                            .param("user", user)
                            .param("pass", "incorrectPass")
            )
            .andExpect(status().is(500));
    }

    @Test
    void test03RedeemCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);
        redeemGC1WithToken(token);
    }

    @Test
    void test04RedeemWithInvalidToken() throws Exception {
        mockMvc.perform(
                    post("/GC1/redeem")
                        .header("Authorization", "Bearer " + "tokenInvalid")
                )
                .andExpect(status().is(500));
    }

    @Test
    void test05RedeemWithoutHeader() throws Exception {
        mockMvc.perform(
                    post("/GC1/redeem")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void test06RedeemFailCardUnknow() throws Exception {
        String token = loginAndGetToken(user, password);

        mockMvc.perform(
                        post("/GC1234/redeem")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().is(500));
    }

    @Test
    void test07GetBalanceCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);
        redeemGC1WithToken(token);

        mockMvc.perform(
                    get("/GC1/balance")
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10));
    }

    @Test
    void test08GetBalanceFailWithNotRedeemedCart() throws Exception {
        String token = loginAndGetToken(user, password);

        mockMvc.perform(
                        get("/GC1/balance")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().is(500));
    }

    @Test
    void test09GetBalanceFailInvalidToken() throws Exception {
        mockMvc.perform(
                        get("/GC1/balance")
                                .header("Authorization", "Bearer " + "tokenInvalid")
                )
                .andExpect(status().is(500));
    }

    @Test
    void test10GetDetailsCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);
        redeemGC1WithToken(token);
        mockMvc.perform(
                get("/GC1/details")
                            .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk());
    }

    //11 token, 12 tarjeta no conocida, 13 tarjeta no redemida o redimida por otro.

    @Test
    void test14ChargeCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);
        redeemGC1WithToken(token);
        mockMvc.perform(
                post("/GC1/charge")
                        .param("merchant", "M1")
                        .param("amount", "5")
                        .param("description", "Café")
                        .param("cardId", "GC1")
        ).andExpect(status().is(200));
    }

    @Test
    void test15ChargeWithInvalidMerchant() throws Exception {
        String token = loginAndGetToken(user, password);
        redeemGC1WithToken(token);

        mockMvc.perform(
                post("/GC1/charge")
                        .param("merchant", "MX")
                        .param("amount", "5")
                        .param("description", "Café")
        ).andExpect(status().is(500));
    }

    @Test
    void test16ChargeOverBalance() throws Exception {
        String token = loginAndGetToken(user, password);
        redeemGC1WithToken(token);

        mockMvc.perform(
                post("/GC1/charge")
                        .param("merchant", "M1")
                        .param("amount", "100")  // ← Más del balance
                        .param("description", "Café")
        ).andExpect(status().is(500));
    }




    private String loginAndGetToken(String user, String pass) throws Exception {
        var res = mockMvc.perform(post("/login")
                        .param("user", user)
                        .param("pass", pass))
                .andExpect(status().isOk())
                .andReturn();

        return new ObjectMapper().readTree(res.getResponse().getContentAsString())
                .get("token").asText();
    }

    private void redeemGC1WithToken(String token) throws Exception {
        mockMvc.perform(
                        post("/GC1/redeem")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }
}



