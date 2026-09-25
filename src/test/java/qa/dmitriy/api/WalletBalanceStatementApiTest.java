package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import qa.dmitriy.auth.UserTokenProvider;
import qa.dmitriy.client.WalletBalanceStatementClient;

import static org.assertj.core.api.Assertions.assertThat;

class WalletBalanceStatementApiTest {

    private static final String TEST_PHONE = "77771711700";
    private static final String ZERO_BALANCE_PHONE = "77763434338";

    private final WalletBalanceStatementClient walletClient =
            new WalletBalanceStatementClient();

    @Test
    void shouldGetBalanceByExistingPhone() {
        Response response =
                walletClient.getBalanceByPhone(TEST_PHONE);

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getString("phone"))
                .isEqualTo(TEST_PHONE);

        assertThat(response.jsonPath().getString("iin"))
                .isNotBlank();

        assertThat(response.jsonPath().getString("balance.total"))
                .isNotNull();

        assertThat(response.jsonPath().getString("balance.blocked"))
                .isNotNull();

        assertThat(response.jsonPath()
                .getString("balance.base_account_amount"))
                .isNotNull();

        assertThat(response.jsonPath()
                .getString("balance.comission_account_amount"))
                .isNotNull();
    }

    @Test
    void shouldReturnNotFoundForNonExistingPhone() {
        Response response =
                walletClient.getBalanceByPhone("70000000001");

        response.then()
                .statusCode(404);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("176");

        assertThat(response.jsonPath()
                .getString("error_client_message"))
                .isEqualTo("Кошелек не найден");
    }

    @Test
    void shouldRejectBalanceRequestWithoutAuthentication() {
        Response response =
                walletClient.getBalanceByPhoneWithoutAuthentication(
                        TEST_PHONE
                );

        response.then()
                .statusCode(401);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("001");
    }

    @Test
    void shouldGetStatementForExistingPhone() {
        Response response = walletClient.getStatement(
                TEST_PHONE,
                0,
                20,
                "createdAt,DESC"
        );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getList("content"))
                .isNotNull();

        assertThat(response.jsonPath().getInt("size"))
                .isEqualTo(20);

        assertThat(response.jsonPath().getInt("number"))
                .isEqualTo(0);

        assertThat(response.jsonPath().getInt("total_elements"))
                .isGreaterThan(0);

        assertThat(response.jsonPath().getInt("total_pages"))
                .isGreaterThan(0);

        assertThat(response.jsonPath().getBoolean("first"))
                .isTrue();

        assertThat(response.jsonPath().getBoolean("empty"))
                .isFalse();
    }

    @Test
    void shouldRejectStatementRequestWithoutAuthentication() {
        Response response =
                walletClient.getStatementWithoutAuthentication(
                        TEST_PHONE,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(401);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("001");
    }

    @Test
    void shouldReturnEmptyStatementForFutureDate() {
        Response response = walletClient.getStatement(
                TEST_PHONE,
                "2099-01-01",
                null,
                0,
                20,
                "createdAt,DESC"
        );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getList("content"))
                .isEmpty();

        assertThat(response.jsonPath().getInt("total_elements"))
                .isEqualTo(0);

        assertThat(response.jsonPath().getBoolean("empty"))
                .isTrue();
    }

    @Test
    void shouldAuthenticateAsUserAndReceiveAccessToken() {
        UserTokenProvider tokenProvider =
                new UserTokenProvider();

        String accessToken =
                tokenProvider.getAccessToken();

        assertThat(accessToken)
                .isNotBlank();
    }

    @Test
    void shouldGetZeroBalanceForExistingWallet() {
        Response response =
                walletClient.getBalanceByPhone(
                        ZERO_BALANCE_PHONE
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getString("phone"))
                .isEqualTo(ZERO_BALANCE_PHONE);

        assertThat(response.jsonPath().getString("iin"))
                .isNotBlank();

        assertThat(response.jsonPath().getInt("balance.total"))
                .isZero();

        assertThat(response.jsonPath().getInt("balance.blocked"))
                .isZero();

        assertThat(response.jsonPath()
                .getInt("balance.base_account_amount"))
                .isZero();

        assertThat(response.jsonPath()
                .getInt("balance.comission_account_amount"))
                .isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "7777171170",
            "777123",
            "777171117000",
            "777ABC11700",
            "777-171-11700",
            "777 17111700",
            "+77717111700"
    })
    void shouldReturnNotFoundForInvalidOrUnknownPhone(String phone) {
        Response response =
                walletClient.getBalanceByPhone(phone);

        response.then()
                .statusCode(404);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("176");

        assertThat(response.jsonPath()
                .getString("error_client_message"))
                .isEqualTo("Кошелек не найден");

        assertThat(response.jsonPath()
                .getString("error_technical_message"))
                .isNotBlank();

        String details =
                response.jsonPath().getString("details");

        assertThat(details)
                .isNull();
    }

    @Test
    void shouldReturnNotFoundForEmptyPhone() {
        Response response =
                walletClient.getBalanceByPhone("");

        response.then()
                .statusCode(404);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("176");

        assertThat(response.jsonPath()
                .getString("error_client_message"))
                .isEqualTo("Кошелек не найден");

        assertThat(response.jsonPath()
                .getString("error_technical_message"))
                .contains("phone:");

        String details =
                response.jsonPath().getString("details");

        assertThat(details)
                .isNull();
    }
}