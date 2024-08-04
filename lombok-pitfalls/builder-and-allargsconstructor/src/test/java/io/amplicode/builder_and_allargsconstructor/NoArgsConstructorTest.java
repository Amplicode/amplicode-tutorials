package io.amplicode.builder_and_allargsconstructor;

import io.amplicode.builder_and_allargsconstructor.entities.User;
import io.amplicode.builder_and_allargsconstructor.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NoArgsConstructorTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void builderTest() {
//        User user = User.builder()
//                .id(1L)
//                .build();
//        userRepository.save(user);
    }

}