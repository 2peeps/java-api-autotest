package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class AuthClient {

    public Response getToken() {
        return getToken(
                TestConfig.adminEmail(),
                TestConfig.adminPassword()
        );
    }

    public Response getToken(String username, String password) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("client_id", "auth-service")
                .formParam(
                        "client_secret",
                        System.getenv("KEYCLOAK_CLIENT_SECRET")
                )
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post(TestConfig.authUrl());
    }
}