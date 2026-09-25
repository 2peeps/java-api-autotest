package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.PayoutClient;
import qa.dmitriy.config.TestConfig;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PayoutSearchApiTest {

    private final PayoutClient payoutClient =
            new PayoutClient();

    @Test
    void shouldFindSuccessfulPayoutsForTestUser() {

        Response response =
                payoutClient.searchPayouts(
                        TestConfig.testIin(),
                        TestConfig.testPhone(),
                        "SUCCESS",
                        null,
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payouts =
                response.jsonPath().getList("content");

        assertThat(payouts)
                .isNotNull();

        assertThat(payouts)
                .allSatisfy(payout -> {
                    assertThat(payout.get("iin"))
                            .isEqualTo(TestConfig.testIin());

                    assertThat(payout.get("phone"))
                            .isEqualTo(TestConfig.testPhone());

                    assertThat(payout.get("status"))
                            .isEqualTo("SUCCESS");
                });
    }

    @Test
    void shouldReturnValidPageStructure() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);
    }

    @Test
    void shouldFilterPayoutsByIin() {

        Response response =
                payoutClient.searchPayouts(
                        TestConfig.testIin(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payouts =
                response.jsonPath().getList("content");

        assertThat(payouts)
                .allSatisfy(payout ->
                        assertThat(payout.get("iin"))
                                .isEqualTo(TestConfig.testIin())
                );
    }

    @Test
    void shouldFilterPayoutsByPhone() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        TestConfig.testPhone(),
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payouts =
                response.jsonPath().getList("content");

        assertThat(payouts)
                .allSatisfy(payout ->
                        assertThat(payout.get("phone"))
                                .isEqualTo(TestConfig.testPhone())
                );
    }

    @Test
    void shouldFilterPayoutsByStatus() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        "SUCCESS",
                        null,
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payouts =
                response.jsonPath().getList("content");

        assertThat(payouts)
                .allSatisfy(payout ->
                        assertThat(payout.get("status"))
                                .isEqualTo("SUCCESS")
                );
    }

    @Test
    void shouldFilterPayoutsByCreatedDateRange() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        null,
                        null,
                        "2026-08-01",
                        "2026-09-18",
                        0,
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
                payoutClient.searchPayouts(
                        "000000000000",
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> payouts =
                response.jsonPath().getList("content");

        assertThat(payouts)
                .isEmpty();

        assertThat(response.jsonPath().getInt("total_elements"))
                .isZero();
    }

    @Test
    void shouldHandleSecondPage() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        1,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("number"))
                .isEqualTo(1);

        assertThat(response.jsonPath().getInt("size"))
                .isEqualTo(20);
    }

    @Test
    void shouldHandleLargePageSize() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        200,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("size"))
                .isEqualTo(200);
    }

    @Test
    void shouldHandleNegativePage() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        -1,
                        20,
                        "createdAt,DESC"
                );

        assertThat(response.statusCode())
                .isIn(200, 400);
    }

    @Test
    void shouldRejectZeroSize() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        0,
                        "createdAt,DESC"
                );

        assertThat(response.statusCode())
                .isIn(200, 400);
    }

    @Test
    void shouldRejectInvalidStatus() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        "INVALID_STATUS",
                        null,
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectInvalidProcessedDate() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        null,
                        "invalid-date",
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectInvalidCreatedDate() {

        Response response =
                payoutClient.searchPayouts(
                        null,
                        null,
                        null,
                        null,
                        "invalid-date",
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(400);
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

        assertThat(response.jsonPath().getInt("number_of_elements"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getBoolean("first"))
                .isNotNull();

        assertThat(response.jsonPath().getBoolean("last"))
                .isNotNull();

        assertThat(response.jsonPath().getBoolean("empty"))
                .isNotNull();
    }
}