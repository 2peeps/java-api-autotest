package qa.dmitriy.auth;

import io.restassured.response.Response;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class ExternalSystemTokenProvider {

    private static final String TOKEN_ENDPOINT =
            "/api/auth/external-system/token";

    public String getAccessToken() {
        Response response = given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .contentType("application/json")
                .body("""
                        {
                          "client_id": "%s",
                          "client_secret": "%s"
                        }
                        """.formatted(
                        TestConfig.externalSystemClientId(),
                        TestConfig.externalSystemClientSecret()
                ))
                .when()
                .post(TOKEN_ENDPOINT);

        response.then()
                .statusCode(200);

        String accessToken = response.jsonPath()
                .getString("access_token");

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException(
                    "External system token was not returned"
            );
        }

        return accessToken;
    }
}