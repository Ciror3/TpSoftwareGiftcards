package com.example.giftcards.giftcards.model;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class GiftCardService extends ModelService< GiftCard, String, GiftCardRepository >{
    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
