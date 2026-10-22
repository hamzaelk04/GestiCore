package org.example.controller;

import org.example.config.DataInitializer;
import org.example.config.RootConfig;
import org.example.config.WebConfig;
import org.example.model.User;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RootConfig.class, WebConfig.class})
@Transactional
class UserControllerIT {

    private static final String ADMIN_EMAIL = DataInitializer.ADMIN_EMAIL;
    private static final String ADMIN_PASSWORD = DataInitializer.ADMIN_PASSWORD;
    private static final String USER_EMAIL = "user@it.com";
    private static final String USER_PASSWORD = "user12345";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private User standardUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        User user = new User();
        user.setName("Standard User");
        user.setEmail(USER_EMAIL);
        user.setPassword(passwordEncoder.encode(USER_PASSWORD));
        user.setRole(Role.USER);
        user.setActive(true);
        standardUser = userRepository.saveAndFlush(user);
    }

    @Test
    void request_withoutCredentials_returns401WithErrorStructure() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/users"));
    }

    @Test
    void request_withWrongPassword_returns401() throws Exception {
        mockMvc.perform(get("/api/users/me").header(HttpHeaders.AUTHORIZATION, basic(USER_EMAIL, "badpassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void request_withMalformedAuthorizationHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/users/me").header(HttpHeaders.AUTHORIZATION, "Basic ###"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void admin_createsUser_returns201WithoutPassword() throws Exception {
        String body = """
                {"name":"New Person","email":"new@it.com","password":"password123","role":"USER"}
                """;

        mockMvc.perform(asAdmin(post("/api/users")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("new@it.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void admin_createsUserWithoutRole_defaultsToUser() throws Exception {
        String body = """
                {"name":"No Role","email":"norole@it.com","password":"password123"}
                """;

        mockMvc.perform(asAdmin(post("/api/users")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void admin_createsUserWithInvalidData_returns400WithFieldErrors() throws Exception {
        String body = """
                {"name":"","email":"not-an-email","password":"short"}
                """;

        mockMvc.perform(asAdmin(post("/api/users")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void admin_createsUserWithInvalidRole_returns400() throws Exception {
        String body = """
                {"name":"Bad Role","email":"badrole@it.com","password":"password123","role":"SUPERUSER"}
                """;

        mockMvc.perform(asAdmin(post("/api/users")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void admin_createsUserWithMalformedJson_returns400() throws Exception {
        mockMvc.perform(asAdmin(post("/api/users")).contentType(MediaType.APPLICATION_JSON).content("{bad json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void admin_createsUserWithExistingEmail_returns409() throws Exception {
        String body = """
                {"name":"Duplicate","email":"user@it.com","password":"password123","role":"USER"}
                """;

        mockMvc.perform(asAdmin(post("/api/users")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void admin_listsUsers_returns200() throws Exception {
        mockMvc.perform(asAdmin(get("/api/users")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem(USER_EMAIL)))
                .andExpect(jsonPath("$[*].email", hasItem(ADMIN_EMAIL)))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void admin_getsUserById_returns200() throws Exception {
        mockMvc.perform(asAdmin(get("/api/users/" + standardUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(USER_EMAIL));
    }

    @Test
    void admin_getsUnknownUser_returns404() throws Exception {
        mockMvc.perform(asAdmin(get("/api/users/00000000-0000-0000-0000-000000000000")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void admin_getsUserWithInvalidUuid_returns400() throws Exception {
        mockMvc.perform(asAdmin(get("/api/users/not-a-uuid")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void admin_updatesUser_returns200() throws Exception {
        String body = """
                {"name":"Updated Name","email":"updated@it.com","role":"ADMIN"}
                """;

        mockMvc.perform(asAdmin(put("/api/users/" + standardUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@it.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void admin_updatesUserWithEmailOfAnotherUser_returns409() throws Exception {
        String body = """
                {"name":"Updated Name","email":"admin@example.com"}
                """;

        mockMvc.perform(asAdmin(put("/api/users/" + standardUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void admin_deactivatesUser_returns204AndUserDisappears() throws Exception {
        mockMvc.perform(asAdmin(delete("/api/users/" + standardUser.getId())))
                .andExpect(status().isNoContent());

        mockMvc.perform(asAdmin(get("/api/users/" + standardUser.getId())))
                .andExpect(status().isNotFound());

        mockMvc.perform(asAdmin(get("/api/users")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", not(hasItem(USER_EMAIL))));
    }

    @Test
    void deactivatedUser_cannotAuthenticate() throws Exception {
        mockMvc.perform(asAdmin(delete("/api/users/" + standardUser.getId())))
                .andExpect(status().isNoContent());

        mockMvc.perform(asUser(get("/api/users/me")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void user_listingUsers_returns403() throws Exception {
        mockMvc.perform(asUser(get("/api/users")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void user_readingAnotherAccount_returns403() throws Exception {
        mockMvc.perform(asUser(get("/api/users/" + standardUser.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void user_creatingUser_returns403() throws Exception {
        String body = """
                {"name":"X","email":"x@it.com","password":"password123"}
                """;

        mockMvc.perform(asUser(post("/api/users")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void user_updatingAnotherAccount_returns403() throws Exception {
        String body = """
                {"name":"Hacked","email":"hacked@it.com"}
                """;

        mockMvc.perform(asUser(put("/api/users/" + standardUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void user_deactivatingAccount_returns403() throws Exception {
        mockMvc.perform(asUser(delete("/api/users/" + standardUser.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void user_readsOwnProfile_returns200() throws Exception {
        mockMvc.perform(asUser(get("/api/users/me")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void user_updatesOwnProfile_returns200() throws Exception {
        String body = """
                {"name":"Renamed User","email":"renamed@it.com"}
                """;

        mockMvc.perform(asUser(put("/api/users/me")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed User"))
                .andExpect(jsonPath("$.email").value("renamed@it.com"));
    }

    @Test
    void user_updatesOwnProfileWithInvalidData_returns400() throws Exception {
        String body = """
                {"name":"","email":"bad"}
                """;

        mockMvc.perform(asUser(put("/api/users/me")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void user_promotingHimselfToAdmin_returns403() throws Exception {
        String body = """
                {"name":"Standard User","email":"user@it.com","role":"ADMIN"}
                """;

        mockMvc.perform(asUser(put("/api/users/me")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void user_changesPassword_returns200AndNewPasswordWorks() throws Exception {
        String body = """
                {"currentPassword":"user12345","newPassword":"newPassword99"}
                """;

        mockMvc.perform(asUser(patch("/api/users/me/password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/me").header(HttpHeaders.AUTHORIZATION, basic(USER_EMAIL, "newPassword99")))
                .andExpect(status().isOk());

        mockMvc.perform(asUser(get("/api/users/me")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void user_changesPasswordWithWrongCurrentPassword_returns400() throws Exception {
        String body = """
                {"currentPassword":"wrong-password","newPassword":"newPassword99"}
                """;

        mockMvc.perform(asUser(patch("/api/users/me/password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_changesPasswordWithTooShortNewPassword_returns400() throws Exception {
        String body = """
                {"currentPassword":"user12345","newPassword":"short"}
                """;

        mockMvc.perform(asUser(patch("/api/users/me/password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.newPassword").exists());
    }

    private MockHttpServletRequestBuilder asAdmin(MockHttpServletRequestBuilder builder) {
        return builder.header(HttpHeaders.AUTHORIZATION, basic(ADMIN_EMAIL, ADMIN_PASSWORD));
    }

    private MockHttpServletRequestBuilder asUser(MockHttpServletRequestBuilder builder) {
        return builder.header(HttpHeaders.AUTHORIZATION, basic(USER_EMAIL, USER_PASSWORD));
    }

    private String basic(String email, String password) {
        String credentials = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
