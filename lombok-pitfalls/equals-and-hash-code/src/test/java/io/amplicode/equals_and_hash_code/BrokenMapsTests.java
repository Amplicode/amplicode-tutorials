package io.amplicode.equals_and_hash_code;

import io.amplicode.equals_and_hash_code.entities.User;
import io.amplicode.equals_and_hash_code.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class BrokenMapsTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void userSetTest() {
        User user = new User();
        Set<User> set = new HashSet<>();

        set.add(user);
        userRepository.save(user);

        Assertions.assertTrue(set.contains(user));
    }

}
