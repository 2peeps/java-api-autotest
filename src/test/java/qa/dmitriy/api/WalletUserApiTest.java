package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.WalletBalanceStatementClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WalletUserApiTest {

    private static final String TEST_IIN = "880324301100";
    private static final String TEST_PHONE = "77771711700";

    private final WalletBalanceStatementClient walletClient =
            new WalletBalanceStatementClient();

    @Test
    void shouldGetMyWallets() {
        Response response =
                walletClient.getMyWallets();

        response.then()
                .statusCode(200);

        List<Map<String, Object>> wallets =
                response.jsonPath().getList("$");

        assertThat(wallets)
                .isNotNull()
                .isNotEmpty();

        assertThat(wallets)
                .allSatisfy(wallet -> {
                    assertThat(wallet.get("name"))
                            .isNotNull();

                    assertThat(wallet.get("last_name"))
                            .isNotNull();

                    assertThat(wallet.get("phone"))
                            .isNotNull();

                    assertThat(wallet.get("iin"))
                            .isEqualTo(TEST_IIN);

                    assertThat(wallet.get("status"))
                            .isNotNull();

                    assertThat(wallet.get("identification_status"))
                            .isNotNull();

                    assertThat(wallet.get("balance"))
                            .isNotNull();

                    assertThat(wallet.get("identified"))
                            .isNotNull();
                });
    }

    @Test
    void shouldReturnWalletForExpectedPhone() {
        Response response =
                walletClient.getMyWallets();

        response.then()
                .statusCode(200);

        List<Map<String, Object>> wallets =
                response.jsonPath().getList("$");

        Map<String, Object> wallet =
                wallets.stream()
                        .filter(item ->
                                TEST_PHONE.equals(item.get("phone")))
                        .findFirst()
                        .orElseThrow(() ->
                                new AssertionError(
                                        "Wallet with expected phone was not found"
                                ));

        assertThat(wallet.get("iin"))
                .isEqualTo(TEST_IIN);

        assertThat(wallet.get("identification_status"))
                .isEqualTo("IDENTIFIED");

        assertThat(wallet.get("identified"))
                .isEqualTo(true);

        assertThat(wallet.get("error_message"))
                .isNull();
    }

    @Test
    void shouldReturnConsistentIdentificationState() {
        Response response =
                walletClient.getMyWallets();

        response.then()
                .statusCode(200);

        List<Map<String, Object>> wallets =
                response.jsonPath().getList("$");

        assertThat(wallets)
                .allSatisfy(wallet -> {
                    String identificationStatus =
                            (String) wallet.get("identification_status");

                    Boolean identified =
                            (Boolean) wallet.get("identified");

                    if ("IDENTIFIED".equals(identificationStatus)) {
                        assertThat(identified)
                                .isTrue();

                        assertThat(wallet.get("error_message"))
                                .isNull();
                    }

                    if ("ERROR".equals(identificationStatus)) {
                        assertThat(identified)
                                .isFalse();

                        assertThat(wallet.get("error_message"))
                                .isNotNull();
                    }
                });
    }

    @Test
    void shouldReturnValidBalanceStructure() {
        Response response =
                walletClient.getMyWallets();

        response.then()
                .statusCode(200);

        List<Map<String, Object>> wallets =
                response.jsonPath().getList("$");

        assertThat(wallets)
                .allSatisfy(wallet -> {
                    Map<String, Object> balance =
                            (Map<String, Object>) wallet.get("balance");

                    assertThat(balance)
                            .isNotNull();

                    assertThat(balance.get("total"))
                            .isNotNull();

                    assertThat(balance.get("blocked"))
                            .isNotNull();

                    assertThat(balance.get("base_account_amount"))
                            .isNotNull();

                    assertThat(balance.get("comission_account_amount"))
                            .isNotNull();
                });
    }
}