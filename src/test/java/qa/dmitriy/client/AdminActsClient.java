package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class AdminActsClient {

    private static final String ENDPOINT = "/api/acts";

    public Response getActs(String accessToken, Integer id) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("page", 0)
                .queryParam("size", 20)
                .queryParam("id", id)
                .when()
                .get(ENDPOINT);
    }

    public Response getActsWithoutAuthentication(Integer id) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .queryParam("page", 0)
                .queryParam("size", 20)
                .queryParam("id", id)
                .when()
                .get(ENDPOINT);
    }

    public Response getActById(String accessToken, int actId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(ENDPOINT + "/" + actId);
        }
}