package com.example.giftcards.giftcards.controller;

import com.example.giftcards.giftcards.model.GiftCardFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // ✅ IMPORT CORRECTO

//@SpringBootTest
//@AutoConfigureMockMvc
@WebMvcTest(GiftcardsController.class)
class GiftcardsControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean
    private GiftCardFacade giftcardsSystemFacade;

    private final String user = "Johnny";
    private final String password = "jojo";

    @Test
    void test01LoginCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);

        // opcional: comprobar que el token es un UUID válido
        assertDoesNotThrow(() -> UUID.fromString(token));
    }

//        @Test
//    void test02LoginIncorrectPass_Approach1() throws Exception {
//            when(giftcardsSystemFacade.login(user, "incorrectPass"))
//                    .thenThrow(new RuntimeException("InvalidUser")); // usa tu excepción de dominio
//
//            mockMvc.perform(
//                        post("/login")
//                                .param("user", user)
//                                .param("pass", "incorrectPass")
//                )
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.error").value(GiftCardFacade.InvalidUser));
//    }

    @Test
    void test03RedeemCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);

        mockMvc.perform(
                        post("/GC1/redeem")
                                .header("Authorization", "Bearer " + token)
                ).andExpect(status().isOk());
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


//    @Test
//    void test02LoginIncorrectPass() throws Exception {
//        assertThrowsLike(() -> mockMvc.perform(
//                post("/login")
//                .param("user", user)
//                .param("pass", "incorrectPass")
//                )
//                .andExpect(status().isUnauthorized()).andReturn(),
//                GifCardFacade.InvalidUser);
//    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }
}



