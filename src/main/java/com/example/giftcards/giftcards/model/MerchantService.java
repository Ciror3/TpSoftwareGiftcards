package com.example.giftcards.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class MerchantService extends ModelService< Merchant, String, MerchantRepository > {
    @Transactional(readOnly = true)
    public boolean exists(String code) {
        return repository.existsById(code);
    }
}
