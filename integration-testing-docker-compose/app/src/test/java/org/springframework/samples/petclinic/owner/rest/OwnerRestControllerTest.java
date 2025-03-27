package org.springframework.samples.petclinic.owner.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link OwnerRestController}
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.docker.compose.enabled=true",
        "spring.docker.compose.skip.in-tests=false",
        "spring.docker.compose.stop.command=down",
        "spring.docker.compose.file=docker-compose-tests.yaml"
})
@AutoConfigureMockMvc
public class OwnerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Test
    public void create() throws Exception {
        String ownerDto = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "address": "123 Main St",
                    "city": "New York",
                    "telephone": "5551234567"
                }""";

        MvcResult mvcResult = mockMvc.perform(post("/rest/owners")
                        .content(ownerDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        String jsonResponse = mvcResult.getResponse()
                .getContentAsString();
        Integer id = JsonPath.parse(jsonResponse)
                .read("$.id");

        ownerRepository.findById(id).orElseThrow();

        ownerRepository.deleteById(id);
    }

    @Test
    public void createIdNotNull() throws Exception {
        String ownerDto = """
                {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Doe",
                    "address": "123 Main St",
                    "city": "New York",
                    "telephone": "5551234567"
                }""";

        mockMvc.perform(post("/rest/owners")
                        .content(ownerDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isUnprocessableEntity());
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getOne() throws Exception {
        mockMvc.perform(get("/rest/owners/{0}", "1"))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void getOneNotFound() throws Exception {
        mockMvc.perform(get("/rest/owners/{id}", 0))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/owners/{0}", "1"))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        assertThat(ownerRepository.findById(1).isPresent()).isEqualTo(false);
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void patch() throws Exception {
        String patchNode = """
                {
                    "id": 1,
                    "firstName": "Johny",
                    "address": null,
                    "city": "Spring ville"
                }""";

        mockMvc.perform(MockMvcRequestBuilders.patch("/rest/owners/{0}", "1")
                        .content(patchNode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.address").value(nullValue()))
                .andExpect(jsonPath("$.firstName").value("Johny"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.city").value("Spring ville"))
                .andExpect(jsonPath("$.telephone").value("555-123-4567"));

        Owner owner = ownerRepository.findById(1).orElseThrow();

        assertThat(owner.getAddress()).isNull();
        assertThat(owner.getFirstName()).isEqualTo("Johny");
        assertThat(owner.getCity()).isEqualTo("Spring ville");
        assertThat(owner.getTelephone()).isEqualTo("555-123-4567");
        assertThat(owner.getLastName()).isEqualTo("Doe");
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAll() throws Exception {
        mockMvc.perform(get("/rest/owners")
                        .param("firstNameContains", "J"))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.content[*].firstName").value(everyItem(startsWith("J"))))
                .andExpect(jsonPath("$.content.length()").value(2));
    }
}
