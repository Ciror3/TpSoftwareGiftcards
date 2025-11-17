package com.example.giftcards.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(MerchantService.class)
public class MerchantServiceTest {
    @Autowired
    protected MerchantService service;

    protected Merchant newMerchant() {
        return new Merchant("M12","MerchantApi");
    }

    protected Merchant savedMerchant() {
        return service.save(newMerchant());
    }

    @Test
    public void testEntitySave() {
        Merchant model = newMerchant();
        Merchant retrieved = service.save( model );
        assertNotNull( retrieved.getCode() );
        assertNotNull( model.getCode() );
        assertEquals( retrieved, model );
    }

    @Test
    public void testExistence() {
        savedMerchant();
        assertTrue(service.exists("M12"));
    }

    @Test public void testFindAll() {
        Merchant model = savedMerchant();
        List list = service.findAll();
        assertFalse( list.isEmpty() );
        assertTrue( list.contains( model ) );
    }
}
