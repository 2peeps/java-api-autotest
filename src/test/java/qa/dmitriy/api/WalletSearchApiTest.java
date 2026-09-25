package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.WalletClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WalletSearchApiTest {

    private static final String TEST_IIN = "880324301100";
    private static final String TEST_PHONE = "77771711700";

    private final WalletClient walletClient =
            new WalletClient();

    @Test
    void shouldSearchWallets() {
        Response response =
                walletClient.searchWallets(
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);
    }

    @Test
    void shouldFindWalletByIin() {
        Response response =
                walletClient.searchWalletsByIin(
                        TEST_IIN,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> wallets =
                response.jsonPath().getList("content");

        assertThat(wallets)
                .isNotNull()
                .isNotEmpty();

        assertThat(wallets)
                .allSatisfy(wallet ->
                        assertThat(wallet.get("iin"))
                                .isEqualTo(TEST_IIN)
                );
    }

    @Test
    void shouldFindWalletByPhone() {
        Response response =
                walletClient.searchWalletsByPhone(
                        TEST_PHONE,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> wallets =
                response.jsonPath().getList("content");

        assertThat(wallets)
                .isNotNull()
                .isNotEmpty();

        assertThat(wallets)
                .allSatisfy(wallet ->
                        assertThat(wallet.get("phone"))
                                .isEqualTo(TEST_PHONE)
                );
    }

    @Test
    void shouldSearchWalletsByCreatedStatus() {
        Response response =
                walletClient.searchWalletsByWalletStatus(
                        "CREATED",
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> wallets =
                response.jsonPath().getList("content");

        assertThat(wallets)
                .isNotNull();

        assertThat(wallets)
                .allSatisfy(wallet ->
                        assertThat(wallet.get("status"))
                                .isEqualTo("CREATED")
                );
    }

    @Test
    void shouldRejectSearchWithoutAuthentication() {
        Response response =
                walletClient.searchWalletsWithoutAuthentication(
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(401);
    }

    @Test
    void shouldHandleNegativePage() {
        Response response =
                walletClient.searchWallets(
                        -1,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);
    }

    @Test
    void shouldReturnEmptyResultForUnknownIin() {
        Response response =
                walletClient.searchWalletsByIin(
                        "000000000000",
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> wallets =
                response.jsonPath().getList("content");

        assertThat(wallets)
                .isEmpty();

        assertThat(response.jsonPath().getInt("total_elements"))
                .isZero();
    }

    private void assertPageStructure(Response response) {
        assertThat(response.jsonPath().getList("content"))
                .isNotNull();

        assertThat(response.jsonPath().getInt("size"))
                .isEqualTo(20);

        assertThat(response.jsonPath().getInt("number"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getInt("total_elements"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getInt("total_pages"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getBoolean("first"))
                .isNotNull();

        assertThat(response.jsonPath().getBoolean("last"))
                .isNotNull();

        assertThat(response.jsonPath().getBoolean("empty"))
                .isNotNull();
    }
}