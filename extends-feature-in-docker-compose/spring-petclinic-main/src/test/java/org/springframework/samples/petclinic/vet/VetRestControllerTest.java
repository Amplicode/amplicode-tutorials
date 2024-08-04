package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link VetRestController}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class VetRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {

	}

	@Test
	@DisplayName("Test find all")
	public void findAllTest() throws Exception {
		mockMvc.perform(get("/rest/vets")
				.with(SecurityMockMvcRequestPostProcessors.user("user")))
			.andExpect(status()
				.isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("Test find by specialties in")
	public void findBySpecialtiesInTest() throws Exception {
		String specialtyIds = "1,2";

		mockMvc.perform(get("/rest/vets/by-specialty")
				.param("specialtyIds", specialtyIds)
				.with(SecurityMockMvcRequestPostProcessors.user("user")))
			.andExpect(status()
				.isOk())
			.andDo(print());
	}
}
