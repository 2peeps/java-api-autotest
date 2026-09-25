package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.TokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class WalletClient {

    private static final String PAYOUT_REGISTRY_EXPORT_ENDPOINT =
            "/api/wallet/payout/registry/{id}/export";

    private static final String PAYOUT_REGISTRY_DETAILS_ENDPOINT  =
            "/api/wallet/payout/registry/{id}";

    private static final String PAYOUT_REGISTRIES_ENDPOINT =
            "/api/wallet/payout/registries";

    private static final String WALLET_EXPORT_ENDPOINT =
            "/api/wallet/export";

    private static final String WALLET_REGISTRY_EXPORT_ENDPOINT =
            "/api/wallet/registry/%d/export";

    private static final String WALLET_SEARCH_ENDPOINT =
            "/api/wallet/search";

    private static final String WALLET_REGISTRIES_ENDPOINT =
            "api/wallet/registries";

    private static final String WALLET_REGISTRY_DETAILS_ENDPOINT =
            "/api/wallet/registry/%d";

    public Response exportPayoutRegistry(long id) {
        return walletRequest()
                .pathParam("id", id)
                .when()
                .get(PAYOUT_REGISTRY_EXPORT_ENDPOINT);
    }

    public Response exportPayoutRegistryWithoutAuthentication(long id) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .pathParam("id", id)
                .when()
                .get(PAYOUT_REGISTRY_EXPORT_ENDPOINT);
    }

    public Response exportWalletRegistry(long registryId) {
    return walletRequest()
            .when()
            .get(
                    String.format(
                            WALLET_REGISTRY_EXPORT_ENDPOINT,
                            registryId
                    )
            );
    }

    public Response exportWalletRegistryWithoutAuthentication(
        long registryId
    ) {
    return given()
            .spec(RestAssuredConfig.defaultSpecification())
            .baseUri(TestConfig.walletBaseUrl())
            .when()
            .get(
                    String.format(
                            WALLET_REGISTRY_EXPORT_ENDPOINT,
                            registryId
                    )
            );
    }

    public Response getPayoutRegistries(
        int page,
        int size,
        String sort
    ) {
    return walletRequest()
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sort", sort)
            .when()
            .get(PAYOUT_REGISTRIES_ENDPOINT);
    }

    public Response getPayoutRegistries(
        String from,
        String to,
        String status,
        String filename,
        int page,
        int size,
        String sort
    ) {
    RequestSpecification request = walletRequest()
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sort", sort);

    if (from != null) {
        request.queryParam("from", from);
    }

    if (to != null) {
        request.queryParam("to", to);
    }

    if (status != null) {
        request.queryParam("status", status);
    }

    if (filename != null) {
        request.queryParam("filename", filename);
    }

    return request
            .when()
            .get(PAYOUT_REGISTRIES_ENDPOINT);
    }

    public Response getPayoutRegistriesWithoutAuthentication(
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
            .get(PAYOUT_REGISTRIES_ENDPOINT);
    }

    public Response getPayoutRegistryDetails(long id) {
        return walletRequest()
                .pathParam("id", id)
                .when()
                .get(PAYOUT_REGISTRY_DETAILS_ENDPOINT);
    }

    public Response getPayoutRegistryDetailsWithoutAuthentication(long id) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .pathParam("id", id)
                .when()
                .get(PAYOUT_REGISTRY_DETAILS_ENDPOINT);
    }

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

    public Response getWalletRegistryDetails(long registryId) {
    return walletRequest()
            .when()
            .get(
                    String.format(
                            WALLET_REGISTRY_DETAILS_ENDPOINT,
                            registryId
                    )
            );
    }

    public Response getWalletRegistryDetailsWithoutAuthentication(
        long registryId
    ) {
    return given()
            .spec(RestAssuredConfig.defaultSpecification())
            .baseUri(TestConfig.walletBaseUrl())
            .when()
            .get(
                    String.format(
                            WALLET_REGISTRY_DETAILS_ENDPOINT,
                            registryId
                    )
            );
    }

    public Response getWalletRegistries(
            int page,
            int size,
            String sort
    ) {
        return walletRequest()
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(WALLET_REGISTRIES_ENDPOINT);
    }
    public Response getWalletRegistries(
        String from,
        String to,
        String status,
        String filename,
        int page,
        int size,
        String sort
    ) {
    RequestSpecification request = walletRequest()
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sort", sort);

    if (from != null) {
        request.queryParam("from", from);
    }

    if (to != null) {
        request.queryParam("to", to);
    }

    if (status != null) {
        request.queryParam("status", status);
    }

    if (filename != null) {
        request.queryParam("filename", filename);
    }

    return request
            .when()
            .get(WALLET_REGISTRIES_ENDPOINT);
    }

    public Response getWalletRegistriesWithoutAuthentication(
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
            .get(WALLET_REGISTRIES_ENDPOINT);
    }

    public Response searchWallets(
        int page,
        int size,
        String sort
    ) {
    return walletRequest()
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sort", sort)
            .when()
            .get(WALLET_SEARCH_ENDPOINT);
    }

    public Response searchWalletsByIin(
        String iin,
        int page,
        int size,
        String sort
    ) {
    return walletRequest()
            .queryParam("iin", iin)
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sort", sort)
            .when()
            .get(WALLET_SEARCH_ENDPOINT);
    }

    public Response searchWalletsByPhone(
        String phone,
        int page,
        int size,
        String sort
    ) {
    return walletRequest()
            .queryParam("phone", phone)
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sort", sort)
            .when()
            .get(WALLET_SEARCH_ENDPOINT);
    }

    public Response searchWalletsByWalletStatus(
        String status,
        int page,
        int size,
        String sort
    ) {
    return walletRequest()
            .queryParam("walletStatuses", status)
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sort", sort)
            .when()
            .get(WALLET_SEARCH_ENDPOINT);
    }

    public Response searchWalletsWithoutAuthentication(
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
            .get(WALLET_SEARCH_ENDPOINT);
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