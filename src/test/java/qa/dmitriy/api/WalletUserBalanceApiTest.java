package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.WalletBalanceStatementClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WalletUserBalanceApiTest {

    private static final String TEST_IIN = "880324301100";
    private static final String TEST_PHONE = "77771711700";

    private final WalletBalanceStatementClient walletClient =
            new WalletBalanceStatementClient();

    @Test
    void shouldGetMyBalances() {
        Response response =
                walletClient.getMyBalance();

        response.then()
                .statusCode(200);

        List<Map<String, Object>> balances =
                response.jsonPath().getList("$");

        assertThat(balances)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void shouldReturnBalancesForExpectedUser() {
        Response response =
                walletClient.getMyBalance();

        response.then()
                .statusCode(200);

        List<Map<String, Object>> balances =
                response.jsonPath().getList("$");

        assertThat(balances)
                .allSatisfy(balance -> {
                    assertThat(balance.get("iin"))
                            .isEqualTo(TEST_IIN);

                    assertThat(balance.get("phone"))
                            .isNotNull();

                    assertThat(balance.get("balance"))
                            .isNotNull();
                });

        assertThat(balances.stream()
                .anyMatch(balance ->
                        TEST_PHONE.equals(balance.get("phone"))))
                .isTrue();
    }

    @Test
    void shouldReturnValidBalanceStructure() {
        Response response =
                walletClient.getMyBalance();

        response.then()
                .statusCode(200);

        List<Map<String, Object>> balances =
                response.jsonPath().getList("$");

        assertThat(balances)
                .allSatisfy(item -> {
                    Map<String, Object> balance =
                            (Map<String, Object>) item.get("balance");

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

    @Test
    void shouldRejectBalanceRequestWithoutAuthentication() {
        Response response =
                walletClient.getMyBalanceWithoutAuthentication();

        response.then()
                .statusCode(401);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("001");
    }
}