package com.repomgr.repomanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.infrastructure.VersionService;
import com.repomgr.repomanager.rest.model.PasswordDto;
import com.repomgr.repomanager.rest.model.TokenDto;
import com.repomgr.repomanager.rest.model.UserDto;
import com.repomgr.repomanager.rest.model.VersionInformationDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RepoManagerApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGenerateToken() throws Exception {
        Assert.assertNotNull(createToken());
    }

    @Test
    public void testStoreUser() throws Exception {
        ResultActions resultActions = createUser();

        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"valid\":true")));
    }

    @Test
    public void testUpdateUserPassword() throws Exception {
        // create new user and read userid
        ResultActions createResultAction = createUser();
        String contentAsString = createResultAction.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto = objectMapper.readValue(contentAsString, UserDto.class);

        // create new userdto for updating password
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setPassword("newPassword");
        String body = objectMapper.writeValueAsString(passwordDto);

        // update password
        ResultActions resultActions = mockMvc.perform(
                put("/users/" + userDto.getUserId() + "/password")
                        .content(body)
                        .headers(createAuthHeader())
        );

        resultActions
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser() throws Exception {
        // create new user for deletion
        ResultActions createResultAction = createUser();

        // read response
        String contentAsString = createResultAction.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto = objectMapper.readValue(contentAsString, UserDto.class);

        // delete the user
        ResultActions resultActions = mockMvc.perform(
                delete("/users/" + userDto.getUserId())
                        .headers(createAuthHeader())
        );

        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    public void testStoreNewVersion() throws Exception {
        // create new version object
        VersionInformationDto versionInformationDto = new VersionInformationDto();
        versionInformationDto.setGroupId("com.repomgr");
        versionInformationDto.setArtifactId("RepoAdmin");
        versionInformationDto.setVersion("1.0.0");
        versionInformationDto.setBranch("master");
        versionInformationDto.setProjectName("MyProject");
        versionInformationDto.setRepositoryUrl("http://domain.com/repo");
        versionInformationDto.setCreationDate(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(versionInformationDto);

        // perform request
        ResultActions resultActions = mockMvc.perform(
                post("/repositories")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .headers(createAuthHeader())
        );

        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"status\":true")));
    }

    /**
     * Helper method for token creation
     *
     * @return              Token for admin user
     * @throws Exception
     */
    private String createToken() throws Exception {
        String token = null;

        UserDto userDto = new UserDto();
        userDto.setUsername("admin");
        userDto.setPassword("password");
        userDto.setValid(null);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(userDto);

        ResultActions resultActions = mockMvc.perform(
                post("/authentication/generate-token")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        TokenDto tokenDto = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), TokenDto.class);
        if (tokenDto != null) {
            token = tokenDto.getToken();
        }

        return token;
    }

    /**
     * Helper method for creating Authorization header
     *
     * @return Header with Authorization token
     * @throws Exception
     */
    private HttpHeaders createAuthHeader() throws Exception {
        String token = createToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        return headers;
    }

    /**
     * Helper method for creating new user
     *
     * @return          ResultActions
     * @throws Exception
     */
    private ResultActions createUser() throws Exception {
        // create user
        UserDto userDto = new UserDto();
        userDto.setUsername("Test" + ThreadLocalRandom.current().nextInt());
        userDto.setPassword("test");
        userDto.setRole(Constants.ROLE_USER);
        userDto.setValid(null);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(userDto);

        // execute request
        return mockMvc.perform(
                post("/users")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .headers(createAuthHeader())
        );
    }
}
