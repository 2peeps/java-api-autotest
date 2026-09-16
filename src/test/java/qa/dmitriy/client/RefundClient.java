package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.TokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class RefundClient {

    private static final String REFUND_ENDPOINT =
            "/api/wallet/refund";

    private static final String REFUND_DETAILS_ENDPOINT =
            "/api/wallet/refund/{id}";

    private static final String REFUND_SEARCH_ENDPOINT =
            "/api/wallet/refund/search";

    private final TokenProvider tokenProvider =
            new TokenProvider();

    public Response createRefund(
            String iin,
            String phone,
            double amount) {

        String requestBody = """
                {
                  "iin": "%s",
                  "phone": "%s",
                  "amount": %s
                }
                """.formatted(
                iin,
                phone,
                amount
        );

        return refundRequest()
                .body(requestBody)
                .when()
                .post(REFUND_ENDPOINT);
    }

    public Response createRefundWithBody(String requestBody) {
        return refundRequest()
                .body(requestBody)
                .when()
                .post(REFUND_ENDPOINT);
    }

    public Response getRefund(long refundId) {
        return refundRequest()
                .pathParam("id", refundId)
                .when()
                .get(REFUND_DETAILS_ENDPOINT);
    }

    public Response searchRefunds() {
        return refundRequest()
                .when()
                .get(REFUND_SEARCH_ENDPOINT);
    }

    public Response searchRefunds(
            String iin,
            String phone,
            String status,
            int page,
            int size) {

        RequestSpecification request =
                refundRequest()
                        .queryParam("page", page)
                        .queryParam("size", size);

        if (iin != null && !iin.isBlank()) {
            request.queryParam("iin", iin);
        }

        if (phone != null && !phone.isBlank()) {
            request.queryParam("phone", phone);
        }

        if (status != null && !status.isBlank()) {
            request.queryParam("statuses", status);
        }

        return request
                .when()
                .get(REFUND_SEARCH_ENDPOINT);
    }

    private RequestSpecification refundRequest() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                )
                .contentType("application/json");
    }
}