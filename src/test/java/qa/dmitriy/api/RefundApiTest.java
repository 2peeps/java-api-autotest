package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import qa.dmitriy.client.RefundClient;
import qa.dmitriy.config.TestConfig;
import qa.dmitriy.fixture.WalletRefundFixture;
import qa.dmitriy.model.Payout;
import qa.dmitriy.model.RefundAllocation;
import qa.dmitriy.model.RefundDetail;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RefundApiTest {

    private final RefundClient refundClient =
            new RefundClient();

    private final WalletRefundFixture walletRefundFixture =
            new WalletRefundFixture();

    @Test
    void shouldRejectRefundWhenAmountExceedsBalance() {
        Response response = refundClient.createRefund(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                999999999
        );

        assertThat(response.statusCode())
                .isEqualTo(400);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("178");

        assertThat(response.jsonPath().getString("error_client_message"))
                .isEqualTo(
                        "Недостаточно средств на кошельке для запрошенной суммы возврата"
                );

        assertThat(response.jsonPath().getString("error_technical_message"))
                .contains(
                        "Requested refund amount exceeds current wallet balance"
                );

        Object details =
        response.jsonPath().get("details");

        assertThat(details)
                .isNull();
    }

    @Test
    void shouldGetRefundDetails() {
        Response searchResponse = refundClient.searchRefunds(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                "ERROR",
                0,
                1
        );

        assertThat(searchResponse.statusCode())
                .isEqualTo(200);

        Long refundId = searchResponse.jsonPath()
                .getLong("content[0].id");

        assertThat(refundId)
                .isNotNull();

        Response response = refundClient.getRefund(refundId);

        assertThat(response.statusCode())
                .isEqualTo(200);

        RefundDetail refund =
                response.as(RefundDetail.class);

        assertThat(refund.id())
                .isEqualTo(refundId);

        assertThat(refund.iin())
                .isEqualTo(TestConfig.testIin());

        assertThat(refund.phone())
                .isEqualTo(TestConfig.testPhone());

        assertThat(refund.status())
                .isEqualTo("ERROR");

        assertThat(refund.allocations())
                .isNotNull();
    }

    @Test
    void shouldSearchRefundsByIinAndPhone() {
        Response response = refundClient.searchRefunds(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                null,
                0,
                10
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getList("content"))
                .isNotNull();

        assertThat(response.jsonPath().getInt("number"))
                .isEqualTo(0);

        assertThat(response.jsonPath().getInt("size"))
                .isEqualTo(10);

        assertThat(response.jsonPath().getInt("total_elements"))
                .isGreaterThanOrEqualTo(0);

        response.jsonPath()
                .getList("content")
                .forEach(refund -> {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> item =
                            (java.util.Map<String, Object>) refund;

                    assertThat(item.get("iin"))
                            .isEqualTo(TestConfig.testIin());

                    assertThat(item.get("phone"))
                            .isEqualTo(TestConfig.testPhone());
                });
    }

    @Test
    void shouldCreateSuccessfulRefund() {
        double refundAmount = 100.0;

        walletRefundFixture.ensureSuccessfulFunding(refundAmount);

        Response response = refundClient.createRefund(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                refundAmount
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        RefundDetail refund =
                response.as(RefundDetail.class);

        assertThat(refund.id())
                .isNotNull();

        assertThat(refund.iin())
                .isEqualTo(TestConfig.testIin());

        assertThat(refund.phone())
                .isEqualTo(TestConfig.testPhone());

        assertThat(refund.amount())
                .isCloseTo(refundAmount, within(0.01));

        assertThat(refund.refundedAmount())
                .isCloseTo(refundAmount, within(0.01));

        assertThat(refund.status())
                .isEqualTo("PROCESSED");

        assertThat(refund.allocations())
                .isNotEmpty();

        assertThat(
                refund.allocations()
                        .stream()
                        .mapToDouble(RefundAllocation::amount)
                        .sum()
        ).isCloseTo(refundAmount, within(0.01));

        assertThat(refund.allocations())
                .allSatisfy(allocation -> {
                    assertThat(allocation.status())
                            .isEqualTo("SUCCESS");

                    assertThat(allocation.payoutId())
                            .isNotNull();

                    assertThat(allocation.externalId())
                            .isNotBlank();

                    assertThat(allocation.transactionId())
                            .isNotNull();
                });
    }

    @Test
    void shouldCreatePartialRefund() {
        Payout payout =
                walletRefundFixture.ensureSuccessfulFunding(100.0);

        double refundAmount =
                Math.min(100.0, payout.amount() / 2);

        Response response = refundClient.createRefund(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                refundAmount
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        RefundDetail refund =
                response.as(RefundDetail.class);

        assertThat(refund.id())
                .isNotNull();

        assertThat(refund.iin())
                .isEqualTo(TestConfig.testIin());

        assertThat(refund.phone())
                .isEqualTo(TestConfig.testPhone());

        assertThat(refund.amount())
                .isCloseTo(refundAmount, within(0.01));

        assertThat(refund.refundedAmount())
                .isCloseTo(refundAmount, within(0.01));

        assertThat(refund.status())
                .isEqualTo("PROCESSED");

        assertThat(refund.allocations())
                .isNotEmpty();

        assertThat(
                refund.allocations()
                        .stream()
                        .mapToDouble(RefundAllocation::amount)
                        .sum()
        ).isCloseTo(refundAmount, within(0.01));

        assertThat(refund.allocations())
                .allSatisfy(allocation -> {
                    assertThat(allocation.status())
                            .isEqualTo("SUCCESS");

                    assertThat(allocation.payoutId())
                            .isNotNull();

                    assertThat(allocation.externalId())
                            .isNotBlank();

                    assertThat(allocation.transactionId())
                            .isNotNull();
                });
    }

    @Test
    void shouldCreateSecondRefundForSameWallet() {
        double refundAmount = 50.0;

        Response firstResponse = refundClient.createRefund(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                refundAmount
        );

        assertThat(firstResponse.statusCode())
                .isEqualTo(200);

        RefundDetail firstRefund =
                firstResponse.as(RefundDetail.class);

        assertThat(firstRefund.id())
                .isNotNull();

        assertThat(firstRefund.status())
                .isEqualTo("PROCESSED");

        Response secondResponse = refundClient.createRefund(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                refundAmount
        );

        assertThat(secondResponse.statusCode())
                .isEqualTo(200);

        RefundDetail secondRefund =
                secondResponse.as(RefundDetail.class);

        assertThat(secondRefund.id())
                .isNotNull()
                .isNotEqualTo(firstRefund.id());

        assertThat(secondRefund.iin())
                .isEqualTo(TestConfig.testIin());

        assertThat(secondRefund.phone())
                .isEqualTo(TestConfig.testPhone());

        assertThat(secondRefund.amount())
                .isCloseTo(refundAmount, within(0.01));

        assertThat(secondRefund.refundedAmount())
                .isCloseTo(refundAmount, within(0.01));

        assertThat(secondRefund.status())
                .isEqualTo("PROCESSED");

        assertThat(secondRefund.allocations())
                .isNotEmpty();

        assertThat(
                secondRefund.allocations()
                        .stream()
                        .mapToDouble(RefundAllocation::amount)
                        .sum()
        ).isCloseTo(refundAmount, within(0.01));

        assertThat(secondRefund.allocations())
                .allSatisfy(allocation -> {
                    assertThat(allocation.status())
                            .isEqualTo("SUCCESS");

                    assertThat(allocation.payoutId())
                            .isNotNull();

                    assertThat(allocation.externalId())
                            .isNotBlank();

                    assertThat(allocation.transactionId())
                            .isNotNull();
                });
    }

    @Test
    void shouldAllocateRefundAcrossMultiplePayouts() {
        List<Payout> payouts =
                walletRefundFixture.getSuccessfulPayouts();

        Payout firstPayout = null;
        Payout secondPayout = null;

        double firstAvailable = 0.0;
        double secondAvailable = 0.0;

        for (Payout payout : payouts) {
            double available =
                    walletRefundFixture.getAvailableAmount(payout);

            if (available > 0) {
                if (firstPayout == null) {
                    firstPayout = payout;
                    firstAvailable = available;
                    continue;
                }

                secondPayout = payout;
                secondAvailable = available;
                break;
            }
        }

        assertThat(firstPayout)
                .isNotNull();

        assertThat(secondPayout)
                .isNotNull();

        assertThat(firstAvailable)
                .isPositive();

        assertThat(secondAvailable)
                .isPositive();

        double amountFromSecondPayout =
                Math.min(100.0, secondAvailable);

        double refundAmount =
                firstAvailable + amountFromSecondPayout;

        Response response = refundClient.createRefund(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                refundAmount
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        RefundDetail refund =
                response.as(RefundDetail.class);

        assertThat(refund.id())
                .isNotNull();

        assertThat(refund.status())
                .isEqualTo("PROCESSED");

        assertThat(refund.refundedAmount())
                .isCloseTo(refundAmount, within(0.01));

        assertThat(refund.allocations())
                .hasSizeGreaterThanOrEqualTo(2);

        assertThat(
                refund.allocations()
                        .stream()
                        .map(RefundAllocation::payoutId)
                        .distinct()
        ).hasSizeGreaterThanOrEqualTo(2);

        assertThat(
                refund.allocations()
                        .stream()
                        .mapToDouble(RefundAllocation::amount)
                        .sum()
        ).isCloseTo(refundAmount, within(0.01));

        assertThat(refund.allocations())
                .allSatisfy(allocation -> {
                    assertThat(allocation.amount())
                            .isPositive();

                    assertThat(allocation.payoutId())
                            .isNotNull();

                    assertThat(allocation.status())
                            .isEqualTo("SUCCESS");

                    assertThat(allocation.externalId())
                            .isNotBlank();

                    assertThat(allocation.transactionId())
                            .isNotNull();
                });
    }

    @Test
    void shouldReturn404ForNonExistingRefund() {
        long nonExistingRefundId = 999999999L;

        Response response =
                refundClient.getRefund(nonExistingRefundId);

        assertThat(response.statusCode())
                .isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -100.0})
    void shouldRejectRefundForInvalidAmount(double amount) {
        Response response = refundClient.createRefund(
                TestConfig.testIin(),
                TestConfig.testPhone(),
                amount
        );

        assertThat(response.statusCode())
                .isEqualTo(400);

        assertThat(response.jsonPath().getString("error_code"))
                .isNotBlank();

        assertThat(response.jsonPath().getString("error_client_message"))
                .isNotBlank();

        Object details =
                response.jsonPath().get("details");

        assertThat(details)
                .isNull();
    }

    @ParameterizedTest
    @CsvSource({
            ", '77771711700'",
            "'880324301100', ''"
    })
    void shouldRejectRefundWhenRequiredFieldIsEmpty(
            String iin,
            String phone) {

        Response response = refundClient.createRefund(
                iin,
                phone,
                100.0
        );

        assertThat(response.statusCode())
                .isEqualTo(400);

        assertThat(response.jsonPath().getString("error_code"))
                .isNotBlank();

        assertThat(response.jsonPath().getString("error_client_message"))
                .isNotBlank();

        Object details =
                response.jsonPath().get("details");

        assertThat(details)
                .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            {
                "iin": "880324301100",
                "phone": "77771711700"
            }
            """,
            """
            {
                "phone": "77771711700",
                "amount": 100
            }
            """,
            """
            {
                "iin": "880324301100",
                "amount": 100
            }
            """
    })
    void shouldRejectRefundWhenRequiredFieldIsMissing(
            String requestBody) {

        Response response =
                refundClient.createRefundWithBody(requestBody);

        assertThat(response.statusCode())
                .isEqualTo(400);

        assertThat(response.jsonPath().getString("error_client_message"))
                .isNotBlank();
    }

    @ParameterizedTest
    @CsvSource({
            "'999999999999', '70000000000'",
            "'010101010101', '79999999999'"
    })
    void shouldRejectRefundForNonExistingWallet(
            String iin,
            String phone) {

        Response response = refundClient.createRefund(
                iin,
                phone,
                100.0
        );

        assertThat(response.statusCode())
                .isEqualTo(400);

        assertThat(response.jsonPath().getString("error_client_message"))
                .isNotBlank();
    }

    @ParameterizedTest
    @CsvSource({
            "'123', '77771711700'",
            "'880324301100', 'abc'"
    })
    void shouldRejectRefundWithInvalidFieldFormat(
            String iin,
            String phone) {

        Response response = refundClient.createRefund(
                iin,
                phone,
                100.0
        );

        assertThat(response.statusCode())
                .isEqualTo(400);

        assertThat(response.jsonPath().getString("error_client_message"))
                .isNotBlank();
    }
}