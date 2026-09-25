package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.UserTokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class RewardPaymentClient {

    private static final String PAYMENTS_ENDPOINT =
            "/api/self-employed/payments";

    private static final String PAYMENT_BY_ID_ENDPOINT =
            "/api/self-employed/payments/{id}";

    private final UserTokenProvider userTokenProvider =
            new UserTokenProvider();

    public Response getRewardPayments(
            int page,
            int size,
            String sort
    ) {
        return rewardPaymentRequest()
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(PAYMENTS_ENDPOINT);
    }

    public Response getRewardPaymentsWithFilters(
            String id,
            String documentNumber,
            String status,
            String createdAtFrom,
            String createdAtTo,
            String validateFrom,
            String validateTo,
            int page,
            int size,
            String sort
    ) {
        RequestSpecification request =
                rewardPaymentRequest()
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sort", sort);

        if (id != null && !id.isBlank()) {
            request.queryParam("id", id);
        }

        if (documentNumber != null && !documentNumber.isBlank()) {
            request.queryParam("documentNumber", documentNumber);
        }

        if (status != null && !status.isBlank()) {
            request.queryParam("statuses", status);
        }

        if (createdAtFrom != null && !createdAtFrom.isBlank()) {
            request.queryParam("createdAtFrom", createdAtFrom);
        }

        if (createdAtTo != null && !createdAtTo.isBlank()) {
            request.queryParam("createdAtTo", createdAtTo);
        }

        if (validateFrom != null && !validateFrom.isBlank()) {
            request.queryParam("validateFrom", validateFrom);
        }

        if (validateTo != null && !validateTo.isBlank()) {
            request.queryParam("validateTo", validateTo);
        }

        return request
                .when()
                .get(PAYMENTS_ENDPOINT);
    }

    public Response getRewardPaymentsWithoutAuthentication(
            int page,
            int size,
            String sort
    ) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(PAYMENTS_ENDPOINT);
    }

    public Response getRewardPaymentById(String id) {
        return rewardPaymentRequest()
                .pathParam("id", id)
                .when()
                .get(PAYMENT_BY_ID_ENDPOINT);
    }

    public Response getRewardPaymentByIdWithoutAuthentication(
            String id
    ) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .pathParam("id", id)
                .when()
                .get(PAYMENT_BY_ID_ENDPOINT);
    }

    private RequestSpecification rewardPaymentRequest() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .auth()
                .oauth2(
                        userTokenProvider.getAccessToken(
                                TestConfig.rewardPaymentTestIin(),
                                TestConfig.rewardPaymentTestPhone()
                        )
                );
    }
}