// src/test/java/com/book/DatabaseCleaner.java
package com.book;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {
    @PersistenceContext private EntityManager em;

    @Transactional
    public void clear() {
        em.createNativeQuery("TRUNCATE TABLE book RESTART IDENTITY CASCADE").executeUpdate();
    }
}
