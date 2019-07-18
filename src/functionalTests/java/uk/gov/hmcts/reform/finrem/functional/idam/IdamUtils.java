package uk.gov.hmcts.reform.finrem.functional.idam;

import com.google.common.collect.ImmutableList;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.finrem.functional.ResourceLoader;
import uk.gov.hmcts.reform.finrem.functional.model.CreateUserRequest;
import uk.gov.hmcts.reform.finrem.functional.model.UserCode;


import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class IdamUtils {

    @Value("${user.id.url}")
    private String userId;

    @Value("${idam.api.url}")
    private String idamUserBaseUrl;

    @Value("${idam.whitelist.url}")
    private String idamRedirectUri;

    @Value("${idam.api.secret}")
    private String idamSecret;

    @Value("${idam.username}")
    private String idamUserName1;
    @Value("${idam.userpassword}")
    private String idamUserPassword1;

    private String idamUsername;
    private String idamPassword;
    private String testUserJwtToken;

    public List<Integer> responseCodes = ImmutableList.of(200, 204);


    @Autowired
    private ServiceAuthTokenGenerator tokenGenerator;

    public Headers getHeadersWithUserId() {

        return Headers.headers(
            new Header("ServiceAuthorization",   tokenGenerator.generate()),
            new Header("user-roles", "caseworker-divorce"),
            new Header("user-id", userId));
    }

    public Headers getHeaders() {
        return Headers.headers(

            new Header("Authorization", "Bearer "
                + generateUserTokenWithNoRoles(idamUserName1, idamUserPassword1)),
            //new Header("Authorization", "Bearer " + idamUtils.getClientAuthToken()),
            new Header("Content-Type", ContentType.JSON.toString()));
    }

    public String getIdamTestUserToken() {
        if (StringUtils.isBlank(testUserJwtToken)) {
            createUserAndToken();
        }
        return testUserJwtToken;
    }

    public void deleteIdamTestUser() {
        if (!StringUtils.isBlank(testUserJwtToken)) {
            deleteUser();
        }
    }

    private void deleteUser() {
        Response response = RestAssured.given()
            .delete(idamCreateUrl() + "/" + idamUsername);
        if (responseCodes.contains(response.getStatusCode())) {
            testUserJwtToken = null;
        }
    }

    private void createUserAndToken() {
        createUserInIdam();
        testUserJwtToken = generateUserTokenWithNoRoles(idamUsername, idamPassword);
    }

    private void createUserInIdam() {
        idamUsername = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        idamPassword = "genericPassword123";

        createUser(idamUsername, idamPassword);
    }

    private void createUser(String username, String password) {
        CreateUserRequest userRequest = CreateUserRequest.builder()
            .email(username)
            .password(password)
            .forename("Test")
            .surname("User")
            .roles(new UserCode[] { UserCode.builder().code("citizen").build() })
            .userGroup(UserCode.builder().code("citizens").build())
            .build();

        RestAssured.given()
            .header("Content-Type", "application/json")
            .body(ResourceLoader.objectToJson(userRequest))
            .post(idamCreateUrl());
    }

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    public String generateUserTokenWithNoRoles(String username, String password) {
        String userLoginDetails = String.join(":", username, password);
        final String authHeader = "Basic " + new String(Base64.getEncoder().encode((userLoginDetails).getBytes()));


        Response response = RestAssured.given()
            .header("Authorization", authHeader)
            .relaxedHTTPSValidation()
            .post(idamCodeUrl());

        if (response.getStatusCode() >= 300) {
            throw new IllegalStateException("Token generation failed with code: " + response.getStatusCode()
                + " body: " + response.getBody().prettyPrint());
        }

        response = RestAssured.given()
            .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .relaxedHTTPSValidation()
            .post(idamTokenUrl(response.getBody().path("code")));

        String token = response.getBody().path("access_token");


        return token;
    }

    private String idamCodeUrl() {
        String myUrl = idamUserBaseUrl + "/oauth2/authorize"
            + "?response_type=code"
            + "&client_id=finrem"
            + "&redirect_uri=" + idamRedirectUri;
        return myUrl;
    }

    private String idamTokenUrl(String code) {
        String myUrl = idamUserBaseUrl + "/oauth2/token"
            + "?code=" + code
            + "&client_id=finrem"
            + "&client_secret=" + idamSecret
            + "&redirect_uri=" + idamRedirectUri
            + "&grant_type=authorization_code";

        return myUrl;
    }
}
