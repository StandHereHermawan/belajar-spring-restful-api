package ariefbelajarteknologi.restful.controller;

import ariefbelajarteknologi.restful.entity.Contact;
import ariefbelajarteknologi.restful.entity.User;
import ariefbelajarteknologi.restful.model.ContactResponse;
import ariefbelajarteknologi.restful.model.CreateContactRequest;
import ariefbelajarteknologi.restful.model.UpdateContactRequest;
import ariefbelajarteknologi.restful.model.WebResponse;
import ariefbelajarteknologi.restful.repository.ContactRepository;
import ariefbelajarteknologi.restful.repository.UserRepository;
import ariefbelajarteknologi.restful.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 30L));
        userRepository.save(user);
    }

    @Test
    void createContactBadRequest() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setEmail("salah");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createContactSuccess() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Arief");
        request.setLastName("Hermawan");
        request.setEmail("arief@student.com");
        request.setPhone("081222204535");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->
        {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );
            assertNull(response.getErrors());
            assertEquals("Arief", response.getData().getFirstName());
            assertEquals("Hermawan", response.getData().getLastName());
            assertEquals("arief@student.com", response.getData().getEmail());
            assertEquals("081222204535", response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void getContactNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts/tidakada")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getContactSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Arief");
        contact.setLastName("Hermawan");
        contact.setEmail("Arief@example.com");
        contact.setPhone("081222204535");
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });
            assertNull(response.getErrors());

            assertEquals(contact.getId(), response.getData().getId());
            assertEquals(contact.getFirstName(), response.getData().getFirstName());
            assertEquals(contact.getLastName(), response.getData().getLastName());
            assertEquals(contact.getEmail(), response.getData().getEmail());
            assertEquals(contact.getPhone(), response.getData().getPhone());
        });
    }

    @Test
    void updateContactBadRequest() throws Exception {
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("");
        request.setEmail("salah");

        mockMvc.perform(
                put("/api/contacts/12345")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateContactSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Arief");
        contact.setLastName("Hermawan");
        contact.setEmail("Arief@example.com");
        contact.setPhone("081222204535");
        contactRepository.save(contact);

        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Muhammad");
        request.setLastName("Rozan Aqila");
        request.setEmail("rozan@student.com");
        request.setPhone("081233304535");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->
        {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );
            assertNull(response.getErrors());
            assertEquals(request.getFirstName(), response.getData().getFirstName());
            assertEquals(request.getLastName(), response.getData().getLastName());
            assertEquals(request.getEmail(), response.getData().getEmail());
            assertEquals(request.getPhone(), response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }
    @Test
    void deleteContactNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/contacts/tidakada")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteContactSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Arief");
        contact.setLastName("Hermawan");
        contact.setEmail("Arief@example.com");
        contact.setPhone("081222204535");
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });
            assertNull(response.getErrors());
            assertEquals("OK",response.getData());
        });
    }
}