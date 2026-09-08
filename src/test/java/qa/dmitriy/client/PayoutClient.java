package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.TokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class PayoutClient {

    private static final String PAYOUT_EXPORT_ENDPOINT =
            "/api/wallet/payout/export";

    private final TokenProvider tokenProvider = new TokenProvider();

    public Response exportPayouts() {
        return payoutRequest()
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByStatus(String status) {
        return payoutRequest()
                .queryParam("statuses", status)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByIin(String iin) {
        return payoutRequest()
                .queryParam("iin", iin)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByPhone(String phone) {
        return payoutRequest()
                .queryParam("phone", phone)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByDateRange(
            String dateFrom,
            String dateTo) {

        return payoutRequest()
                .queryParam("createdDateFrom", dateFrom)
                .queryParam("createdDateTo", dateTo)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByStatuses(
            String firstStatus,
            String secondStatus) {

        return payoutRequest()
                .queryParam(
                        "statuses",
                        firstStatus + "," + secondStatus
                )
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByIinAndDateRange(
            String iin,
            String dateFrom,
            String dateTo) {

        return payoutRequest()
                .queryParam("iin", iin)
                .queryParam("createdDateFrom", dateFrom)
                .queryParam("createdDateTo", dateTo)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByIinAndPhone(
            String iin,
            String phone) {

        return payoutRequest()
                .queryParam("iin", iin)
                .queryParam("phone", phone)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByIinPhoneAndDateRange(
            String iin,
            String phone,
            String dateFrom,
            String dateTo) {

        return payoutRequest()
                .queryParam("iin", iin)
                .queryParam("phone", phone)
                .queryParam("createdDateFrom", dateFrom)
                .queryParam("createdDateTo", dateTo)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByIinPhoneAndStatus(
            String iin,
            String phone,
            String status) {

        return payoutRequest()
                .queryParam("iin", iin)
                .queryParam("phone", phone)
                .queryParam("statuses", status)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    public Response exportPayoutsByAllFilters(
            String iin,
            String phone,
            String dateFrom,
            String dateTo,
            String status) {

        return payoutRequest()
                .queryParam("iin", iin)
                .queryParam("phone", phone)
                .queryParam("createdDateFrom", dateFrom)
                .queryParam("createdDateTo", dateTo)
                .queryParam("statuses", status)
                .when()
                .get(PAYOUT_EXPORT_ENDPOINT);
    }

    private RequestSpecification payoutRequest() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                );
    }
}