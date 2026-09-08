package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class AuthClient {

    public Response getToken() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.authUrl())
                .contentType("application/x-www-form-urlencoded")
                .formParam("client_id", TestConfig.authClientId())
                .formParam("username", TestConfig.authUsername())
                .formParam("password", TestConfig.authPassword())
                .formParam("grant_type", "password")
                .formParam("client_secret", TestConfig.authClientSecret())
                .when()
                .post();
    }
}