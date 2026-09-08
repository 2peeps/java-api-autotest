package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.TokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class WalletClient {

    private static final String WALLET_EXPORT_ENDPOINT =
            "/api/wallet/export";

    private final TokenProvider tokenProvider = new TokenProvider();

    public Response exportWallets() {
        return walletRequest()
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    public Response exportWallets(String dateFrom, String dateTo) {
        return walletRequest()
                .queryParam("createdDateFrom", dateFrom)
                .queryParam("createdDateTo", dateTo)
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    public Response exportWalletsByIin(String iin) {
        return walletRequest()
                .queryParam("iin", iin)
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    public Response exportWalletsByPhone(String phone) {
        return walletRequest()
                .queryParam("phone", phone)
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    public Response exportWalletsByWalletStatus(String walletStatus) {
        return walletRequest()
                .queryParam("walletStatuses", walletStatus)
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    public Response exportWalletsByIdentificationStatus(
            String identificationStatus) {

        return walletRequest()
                .queryParam(
                        "identificationStatuses",
                        identificationStatus
                )
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    public Response exportWalletsByWalletStatuses(
            String firstStatus,
            String secondStatus) {

        return walletRequest()
                .queryParam(
                        "walletStatuses",
                        firstStatus + "," + secondStatus
                )
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    public Response exportWalletsByIdentificationStatuses(
            String firstStatus,
            String secondStatus) {

        return walletRequest()
                .queryParam(
                        "identificationStatuses",
                        firstStatus + "," + secondStatus
                )
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    public Response exportWalletsByFilters(
            String iin,
            String dateFrom,
            String dateTo,
            String walletStatus,
            String identificationStatus) {

        return walletRequest()
                .queryParam("iin", iin)
                .queryParam("createdDateFrom", dateFrom)
                .queryParam("createdDateTo", dateTo)
                .queryParam("walletStatuses", walletStatus)
                .queryParam(
                        "identificationStatuses",
                        identificationStatus
                )
                .when()
                .get(WALLET_EXPORT_ENDPOINT);
    }

    private RequestSpecification walletRequest() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                );
    }
}