package io.amplicode.equals_and_hash_code;

import io.amplicode.equals_and_hash_code.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SameRecordTests {
    @PersistenceContext
    private EntityManager em;

    @Test
    void userDetachTest() {
        User user = new User();
        em.persist(user);

        User firstFetched = em.find(User.class, user.getId());
        em.detach(firstFetched);

        User secondFetched = em.find(User.class, user.getId());

        Assertions.assertEquals(firstFetched, secondFetched);
    }

}
