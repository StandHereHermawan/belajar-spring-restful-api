package ariefbelajarteknologi.restful.controller;

import ariefbelajarteknologi.restful.entity.User;
import ariefbelajarteknologi.restful.model.LoginUserRequest;
import ariefbelajarteknologi.restful.model.TokenResponse;
import ariefbelajarteknologi.restful.model.WebResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void loginFailedUserNotFound() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginFailedWrongPassword() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test",BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("salah");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginSuccess() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test",BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("test");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->
        {
            WebResponse<TokenResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            User userDataDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDataDb);
            assertEquals(userDataDb.getToken(), response.getData().getToken());
            assertEquals(userDataDb.getTokenExpiredAt(), response.getData().getExpiredAt());
        });
    }

    @Test
    void testLogOutFailed() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 30L));
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN","test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->
        {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });

            assertNull(response.getErrors());
            assertEquals("OK",response.getData());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertNull(userDb.getTokenExpiredAt());
            assertNull(userDb.getToken());
        });
    }
}