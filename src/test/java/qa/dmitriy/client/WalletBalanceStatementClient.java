package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.auth.TokenProvider;
import qa.dmitriy.auth.UserTokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class WalletBalanceStatementClient {

    private static final String USER_BALANCE_ENDPOINT =
            "api/wallet/user/balance";

    private static final String BALANCE_BY_PHONE_ENDPOINT =
            "/api/wallet/balance/by-phone";

    private static final String STATEMENT_ENDPOINT =
            "/api/wallet/user/statement";

    private static final String USER_WALLETS_ENDPOINT =
            "/api/wallet/user/me";


    private final TokenProvider tokenProvider;
    private final UserTokenProvider userTokenProvider;

    public Response getMyBalance() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .auth()
                .oauth2(userTokenProvider.getAccessToken())
                .when()
                .get(USER_BALANCE_ENDPOINT);
    }

    public Response getMyBalanceWithoutAuthentication() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .when()
                .get(USER_BALANCE_ENDPOINT);
    }

    public WalletBalanceStatementClient() {
        this.tokenProvider = new TokenProvider();
        this.userTokenProvider = new UserTokenProvider();
    }

    public Response getMyWallets() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .auth()
                .oauth2(userTokenProvider.getAccessToken())
                .when()
                .get(USER_WALLETS_ENDPOINT);
}

    public Response getBalanceByPhone(String phone) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .auth()
                .oauth2(tokenProvider.getAccessToken())
                .queryParam("phone", phone)
                .when()
                .get(BALANCE_BY_PHONE_ENDPOINT);
    }

    public Response getBalanceByPhoneWithoutAuthentication(String phone) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .queryParam("phone", phone)
                .when()
                .get(BALANCE_BY_PHONE_ENDPOINT);
    }

    public Response getStatement(
            String phone,
            int page,
            int size,
            String sort
    ) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .auth()
                .oauth2(userTokenProvider.getAccessToken())
                .queryParam("phone", phone)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(STATEMENT_ENDPOINT);
    }

    public Response getStatement(
            String phone,
            String from,
            String to,
            int page,
            int size,
            String sort
    ) {
        var request = given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .auth()
                .oauth2(userTokenProvider.getAccessToken())
                .queryParam("phone", phone)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort);

        if (from != null) {
            request.queryParam("from", from);
        }

        if (to != null) {
            request.queryParam("to", to);
        }

        return request
                .when()
                .get(STATEMENT_ENDPOINT);
    }

    public Response getStatementWithoutAuthentication(
            String phone,
            int page,
            int size,
            String sort
    ) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .queryParam("phone", phone)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(STATEMENT_ENDPOINT);
    }
}