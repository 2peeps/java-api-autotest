package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.UserTokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class ActClient {

    private static final String ACTS_ENDPOINT =
            "/api/self-employed/acts/";

    private static final String ACT_BY_ID_ENDPOINT =
            "/api/self-employed/acts/{id}";

    private final UserTokenProvider userTokenProvider =
            new UserTokenProvider();

    public Response getActs(
            int page,
            int size,
            String sort
    ) {
        RequestSpecification request =
                actRequest()
                        .queryParam("page", page)
                        .queryParam("size", size);

        if (sort != null && !sort.isBlank()) {
            request.queryParam("sort", sort);
        }

        return request
                .when()
                .get(ACTS_ENDPOINT);
    }

    public Response getActsWithFilters(
            String id,
            String dealNumber,
            String status,
            String createdFrom,
            String createdTo,
            Integer monthCreated,
            int page,
            int size,
            String sort
    ) {
        RequestSpecification request =
                actRequest()
                        .queryParam("page", page)
                        .queryParam("size", size);

        if (id != null && !id.isBlank()) {
            request.queryParam("id", id);
        }

        if (dealNumber != null && !dealNumber.isBlank()) {
            request.queryParam("dealNumber", dealNumber);
        }

        if (status != null && !status.isBlank()) {
            request.queryParam("statuses", status);
        }

        if (createdFrom != null && !createdFrom.isBlank()) {
            request.queryParam("createdFrom", createdFrom);
        }

        if (createdTo != null && !createdTo.isBlank()) {
            request.queryParam("createdTo", createdTo);
        }

        if (monthCreated != null) {
            request.queryParam("monthCreated", monthCreated);
        }

        if (sort != null && !sort.isBlank()) {
            request.queryParam("sort", sort);
        }

        return request
                .when()
                .get(ACTS_ENDPOINT);
    }

    public Response getActsWithoutAuthentication(
            int page,
            int size,
            String sort
    ) {
        RequestSpecification request =
                given()
                        .spec(RestAssuredConfig.defaultSpecification())
                        .baseUri(TestConfig.walletBaseUrl())
                        .queryParam("page", page)
                        .queryParam("size", size);

        if (sort != null && !sort.isBlank()) {
            request.queryParam("sort", sort);
        }

        return request
                .when()
                .get(ACTS_ENDPOINT);
    }

    public Response getActById(String id) {
        return actRequest()
                .pathParam("id", id)
                .when()
                .get(ACT_BY_ID_ENDPOINT);
    }

    public Response getActByIdWithoutAuthentication(String id) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .pathParam("id", id)
                .when()
                .get(ACT_BY_ID_ENDPOINT);
    }

    private RequestSpecification actRequest() {
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