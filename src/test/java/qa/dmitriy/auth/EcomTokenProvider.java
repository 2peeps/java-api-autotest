package qa.dmitriy.auth;

import io.restassured.response.Response;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class EcomTokenProvider {

    private String accessToken;

    public synchronized String getAccessToken() {
        if (accessToken == null || accessToken.isBlank()) {
            refreshToken();
        }

        return accessToken;
    }

    private void refreshToken() {
        Response response = given()
                .baseUri(TestConfig.paymentStatementsBaseUrl())
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
                .post("/api/auth/external-system/token");

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to obtain ECOM access token. HTTP status: "
                            + response.statusCode()
                            + ", body: "
                            + response.asString()
            );
        }

        accessToken = response.jsonPath()
                .getString("access_token");

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException(
                    "ECOM access token is missing in response"
            );
        }
    }
}