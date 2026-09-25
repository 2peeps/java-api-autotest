package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class AdminMeClient {

    private static final String ENDPOINT = "/api/auth/admin/me";

    public Response getCurrentAdminInfo(String accessToken) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(ENDPOINT);
    }

    public Response getCurrentAdminInfoWithoutAuthentication() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .when()
                .get(ENDPOINT);
    }
}