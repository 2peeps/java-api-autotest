package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.UserTokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class ReceiptClient {

    private static final String RECEIPTS_ENDPOINT =
            "/api/self-employed/receipts";

    private static final String RECEIPTS_BY_ID_ENDPOINT =
            "/api/self-employed/receipts/{id}";

    private final UserTokenProvider userTokenProvider =
            new UserTokenProvider();

    public Response getReceiptById(String id) {
        return receiptRequest()
                .pathParam("id", id)
                .when()
                .get(RECEIPTS_BY_ID_ENDPOINT);
    }

    public Response getReceiptByIdWithoutAuthentication(String id) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .pathParam("id", id)
                .when()
                .get(RECEIPTS_BY_ID_ENDPOINT);
    }

    public Response getReceipts(
            int page,
            int size,
            String sort
    ) {
        return receiptRequest()
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(RECEIPTS_ENDPOINT);
    }

    public Response getReceiptsByStatus(
            String status,
            int page,
            int size,
            String sort
    ) {
        return receiptRequest()
                .queryParam("statuses", status)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(RECEIPTS_ENDPOINT);
    }

    public Response getReceiptsByStatuses(
            String firstStatus,
            String secondStatus,
            int page,
            int size,
            String sort
    ) {
        return receiptRequest()
                .queryParam(
                        "statuses",
                        firstStatus + "," + secondStatus
                )
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(RECEIPTS_ENDPOINT);
    }

    public Response getReceiptsByDateRange(
            String createdFrom,
            String createdTo,
            int page,
            int size,
            String sort
    ) {
        return receiptRequest()
                .queryParam("created_from", createdFrom)
                .queryParam("created_to", createdTo)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(RECEIPTS_ENDPOINT);
    }

    public Response getReceiptsWithFilters(
            String status,
            String createdFrom,
            String createdTo,
            int page,
            int size,
            String sort
    ) {
        RequestSpecification request =
                receiptRequest()
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sort", sort);

        if (status != null && !status.isBlank()) {
            request.queryParam("statuses", status);
        }

        if (createdFrom != null && !createdFrom.isBlank()) {
            request.queryParam("created_from", createdFrom);
        }

        if (createdTo != null && !createdTo.isBlank()) {
            request.queryParam("created_to", createdTo);
        }

        return request
                .when()
                .get(RECEIPTS_ENDPOINT);
    }

    public Response getReceiptsWithoutAuthentication(
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
                .get(RECEIPTS_ENDPOINT);
    }

    private RequestSpecification receiptRequest() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + userTokenProvider.getAccessToken()
                );
    }
}