package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class MockPaymentStatementsClient {

    public Response createStatement(String requestBody) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.paymentStatementsBaseUrl())
                .body(requestBody)
                .when()
                .post("/api/mock/payment-statements");
    }

    public Response getItemStatuses(String statementId, String requestBody) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.paymentStatementsBaseUrl())
                .pathParam("statementId", statementId)
                .body(requestBody)
                .when()
                .post("/api/mock/payment-statements/{statementId}/items/statuses");
    }

    public Response cancelStatement(String statementId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.paymentStatementsBaseUrl())
                .pathParam("statementId", statementId)
                .when()
                .post("/api/mock/payment-statements/{statementId}/cancel");
    }
}