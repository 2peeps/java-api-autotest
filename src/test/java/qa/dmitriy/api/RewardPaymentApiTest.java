package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.RewardPaymentClient;
import qa.dmitriy.config.TestConfig;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RewardPaymentApiTest {

    private static final String TEST_IIN =
            TestConfig.rewardPaymentTestIin();

    private static final String TEST_PHONE =
            TestConfig.rewardPaymentTestPhone();

    private static final String TEST_PAYMENT_ID =
            "c7f857cc-3441-4da4-b6ca-16389122b0cf";

    private static final String DEFAULT_SORT =
            "createdAt,DESC";

    private final RewardPaymentClient paymentClient =
            new RewardPaymentClient();

    @Test
    void shouldGetRewardPayments() {
        Response response =
                paymentClient.getRewardPayments(
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);

        List<Map<String, Object>> payments =
                response.jsonPath()
                        .getList("content");

        assertThat(payments)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void shouldReturnValidRewardPaymentStructure() {
        Response response =
                paymentClient.getRewardPayments(
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payments =
                response.jsonPath()
                        .getList("content");

        assertThat(payments)
                .isNotNull()
                .isNotEmpty();

        assertThat(payments)
                .allSatisfy(payment -> {
                    assertThat(payment.get("id"))
                            .isNotNull();

                    assertThat(payment.get("status"))
                            .isNotNull();

                    assertThat(payment.get("document_number"))
                            .isNotNull();

                    assertThat(payment.get("recipient_iin"))
                            .isEqualTo(TEST_IIN);

                    assertThat(payment.get("amount_for_pay"))
                            .isNotNull();

                    assertThat(payment.get("value_date"))
                            .isNotNull();
                });
    }

    @Test
    void shouldFindRewardPaymentByIdFilter() {
        Response response =
                paymentClient.getRewardPaymentsWithFilters(
                        TEST_PAYMENT_ID,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payments =
                response.jsonPath()
                        .getList("content");

        assertThat(payments)
                .isNotNull()
                .isNotEmpty();

        assertThat(payments)
                .allSatisfy(payment ->
                        assertThat(payment.get("id"))
                                .isEqualTo(TEST_PAYMENT_ID)
                );
    }

    @Test
    void shouldFindRewardPaymentByDocumentNumber() {
        Response response =
                paymentClient.getRewardPaymentsWithFilters(
                        null,
                        "1",
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payments =
                response.jsonPath()
                        .getList("content");

        assertThat(payments)
                .isNotNull()
                .isNotEmpty();

        assertThat(payments)
                .allSatisfy(payment ->
                        assertThat(payment.get("document_number"))
                                .isEqualTo("1")
                );
    }

    @Test
    void shouldFilterRewardPaymentsByStatus() {
        Response response =
                paymentClient.getRewardPaymentsWithFilters(
                        null,
                        null,
                        "PAID",
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payments =
                response.jsonPath()
                        .getList("content");

        assertThat(payments)
                .isNotNull()
                .isNotEmpty();

        assertThat(payments)
                .allSatisfy(payment -> {
                    Map<String, Object> status =
                            (Map<String, Object>) payment.get("status");

                    assertThat(status)
                            .isNotNull();

                    assertThat(status.get("code"))
                            .isEqualTo("PAID");
                });
    }

    @Test
    void shouldHandleSecondPage() {
        Response response =
                paymentClient.getRewardPayments(
                        1,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("number"))
                .isEqualTo(1);

        assertThat(
                response.jsonPath()
                        .getInt("pageable.page_number")
        ).isEqualTo(1);

        assertThat(
                response.jsonPath()
                        .getInt("pageable.offset")
        ).isEqualTo(20);
    }

    @Test
    void shouldHandleLargePageSize() {
        Response response =
                paymentClient.getRewardPayments(
                        0,
                        200,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("size"))
                .isEqualTo(200);

        assertThat(response.jsonPath().getList("content"))
                .isNotNull();
    }

    @Test
    void shouldNormalizeNegativePageToZero() {
        Response response =
                paymentClient.getRewardPayments(
                        -2,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("number"))
                .isEqualTo(0);

        assertThat(
                response.jsonPath()
                        .getInt("pageable.page_number")
        ).isEqualTo(0);

        assertThat(
                response.jsonPath()
                        .getInt("pageable.offset")
        ).isEqualTo(0);
    }

    @Test
    void shouldRejectInvalidPaymentIdFilter() {
        Response response =
                paymentClient.getRewardPaymentsWithFilters(
                        "invalid-id",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectInvalidCreatedAtFromDate() {
        Response response =
                paymentClient.getRewardPaymentsWithFilters(
                        null,
                        null,
                        null,
                        "invalid-date",
                        null,
                        null,
                        null,
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectInvalidValidateFromDate() {
        Response response =
                paymentClient.getRewardPaymentsWithFilters(
                        null,
                        null,
                        null,
                        null,
                        null,
                        "invalid-date",
                        null,
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectRewardPaymentsWithoutAuthentication() {
        Response response =
                paymentClient.getRewardPaymentsWithoutAuthentication(
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(401);
    }

    @Test
    void shouldGetRewardPaymentById() {
        Response response =
                paymentClient.getRewardPaymentById(
                        TEST_PAYMENT_ID
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getString("id"))
                .isEqualTo(TEST_PAYMENT_ID);

        assertThat(
                response.jsonPath()
                        .getString("recipient_iin")
        ).isEqualTo(TEST_IIN);
    }

    @Test
    void shouldReturnValidRewardPaymentByIdStructure() {
        Response response =
                paymentClient.getRewardPaymentById(
                        TEST_PAYMENT_ID
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getString("id"))
                .isEqualTo(TEST_PAYMENT_ID);

        Object status =
                response.jsonPath()
                        .get("status");

        assertThat(status)
                .isNotNull();

        assertThat(
                response.jsonPath()
                        .getString("document_number")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("recipient_iin")
        ).isEqualTo(TEST_IIN);

        Object amountForPay =
                response.jsonPath()
                        .get("amount_for_pay");

        assertThat(amountForPay)
                .isNotNull();

        Object valueDate =
                response.jsonPath()
                        .get("value_date");

        assertThat(valueDate)
                .isNotNull();

        assertThat(
                response.jsonPath()
                        .getString("service_code")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("position_name")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("warehouse_code")
        ).isNotBlank();
    }

    @Test
    void shouldRejectInvalidRewardPaymentId() {
        Response response =
                paymentClient.getRewardPaymentById(
                        "invalid-id"
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectRewardPaymentByIdWithoutAuthentication() {
        Response response =
                paymentClient.getRewardPaymentByIdWithoutAuthentication(
                        "00000000-0000-0000-0000-000000000000"
                );

        response.then()
                .statusCode(401);
    }

    private void assertPageStructure(Response response) {
        assertThat(response.jsonPath().getList("content"))
                .isNotNull();

        assertThat(response.jsonPath().getInt("size"))
                .isGreaterThan(0);

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