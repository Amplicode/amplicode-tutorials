package org.springframework.samples.petclinic.owner.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class OwnerRestControllerTest {
    @ServiceConnection
    @Container
    static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                    .waitingFor(Wait.defaultWaitStrategy());

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
                  "city": "Anytown",
                  "telephone": "1234567890"
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

        assertThat(id).isNotNull();

        ownerRepository.findById(id)
                .orElseThrow();

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
                  "city": "Anytown",
                  "telephone": "1234567890"
                }""";

        mockMvc.perform(post("/rest/owners")
                        .content(ownerDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        int size = ownerRepository.findAll()
                .size();

        assertThat(size).isEqualTo(0);
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getOne() throws Exception {
        mockMvc.perform(get("/rest/owners/{0}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void getOneNotFound() throws Exception {
        mockMvc.perform(get("/rest/owners/{0}", 0))
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/owners/{0}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andReturn();
        int size = ownerRepository.findAll()
                .size();

        assertThat(size).isEqualTo(4);
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void update() throws Exception {
        String dto = """
                {
                    "id": 1,
                    "firstName": "Johny",
                    "lastName": "Dow-vie",
                    "address": "Spring Avenue",
                    "city": "Spring ville",
                    "telephone": "9999999999"
                }""";

        mockMvc.perform(put("/rest/owners/{0}", 1)
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.firstName").value("Johny"))
                .andExpect(jsonPath("$.lastName").value("Dow-vie"))
                .andExpect(jsonPath("$.city").value("Spring ville"));

        Owner owner = ownerRepository.findById(1)
                .orElseThrow();

        assertThat(owner.getFirstName()).isEqualTo("Johny");
        assertThat(owner.getTelephone()).isEqualTo("9999999999");
        assertThat(owner.getAddress()).isEqualTo("Spring Avenue");
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void patch() throws Exception {
        String patchNode = """
                {
                  "firstName": "Johnny",
                  "address": null,
                  "city": "Spring ville"
                }""";

        mockMvc.perform(MockMvcRequestBuilders.patch("/rest/owners/{0}", 1)
                        .content(patchNode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.address").value(nullValue()))
                .andExpect(jsonPath("$.city").value("Spring ville"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.telephone").value("555-123-4567"));

        Owner owner = ownerRepository.findById(1)
                .orElseThrow();

        assertThat(owner.getAddress()).isNull();
        assertThat(owner.getFirstName()).isEqualTo("Johnny");
        assertThat(owner.getCity()).isEqualTo("Spring ville");
        assertThat(owner.getTelephone()).isEqualTo("555-123-4567");
        assertThat(owner.getLastName()).isEqualTo("Doe");
    }

    @Test
    @Sql(scripts = "classpath:insert-owners.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:delete-owners.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAll() throws Exception {
        mockMvc.perform(get("/rest/owners")
                        .param("lastNameStarts", "Do"))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.content[*].lastName").value(everyItem(is("Doe"))))
                .andExpect(jsonPath("$.content.length()").value(2));
    }
}
