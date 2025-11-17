package com.example.giftcards.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(GiftCardService.class)
public class GiftCardServiceTest {
    @Autowired
    protected GiftCardService service;

    protected GiftCard newGiftCard() {
        return new GiftCard("GC1",100);
    }

    protected GiftCard savedGiftCard() {
        return service.save(newGiftCard());
    }

    @Test
    public void testEntitySave() {
        GiftCard model = newGiftCard();
        GiftCard retrieved = service.save( model );
        assertNotNull( retrieved.getId() );
        assertNotNull( model.getId() );
        assertEquals( retrieved, model );
    }

    @Test public void testDeletionByObject() {
        GiftCard model = savedGiftCard();

        service.delete( model );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );

    }

    @Test public void testDeletionById() {
        GiftCard model = savedGiftCard();

        service.deleteById( model.getId() );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );

    }

    @Test public void testDeletionByProxy() throws Exception {
        GiftCard model = savedGiftCard();
        GiftCard proxy = model.getClass().getConstructor().newInstance();
        proxy.setId( model.getId() );

        service.delete( proxy );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );
    }

    @Test public void testFindAll() {
        GiftCard model = savedGiftCard();
        List list = service.findAll();
        assertFalse( list.isEmpty() );
        assertTrue( list.contains( model ) );
    }

}
