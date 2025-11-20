package com.example.giftcards.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends ModelService< UserVault, Long, UserRepository > {

    @Transactional( readOnly = true )
    public UserVault findByName( String username ) {
        return repository.findByUsername(username).orElseThrow(()-> new RuntimeException(GiftCardFacade.InvalidUser));
    }
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return repository.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }
}

