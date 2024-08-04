package io.amplicode.to_string;

import io.amplicode.to_string.entities.User;
import io.amplicode.to_string.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class LazyAssociationsTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	@Transactional
	@Sql(statements = """
            INSERT INTO address (id, country, city) VALUES (1, 'Канада', 'Лондон');
            INSERT INTO user_ (id, username, address_id) VALUES (1, 'Райан Гослинг', 1);
            INSERT INTO post (id, user_id, text) VALUES (1, 1, 'Каскадёры уже в кино!');
            """)
	public void toStringLazyTest() {
		User user = userRepository.findById(1L).orElseThrow();

		System.out.println("\n Получили сущность. Теперь обратимся к методу toString().\n");
		System.out.println(user.toString());
	}

}
