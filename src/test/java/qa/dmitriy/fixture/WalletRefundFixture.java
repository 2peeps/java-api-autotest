package qa.dmitriy.fixture;

import io.restassured.response.Response;
import qa.dmitriy.client.PayoutClient;
import qa.dmitriy.client.RefundClient;
import qa.dmitriy.config.TestConfig;
import qa.dmitriy.model.Payout;
import qa.dmitriy.model.PayoutPage;
import qa.dmitriy.model.RefundDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WalletRefundFixture {

    private final RefundClient refundClient =
            new RefundClient();

    private final PayoutClient payoutClient =
            new PayoutClient();

    private Map<Long, Double> refundedAmountsByPayout;

    public List<Payout> getSuccessfulPayouts() {

        Response response =
                payoutClient.searchPayouts(
                        TestConfig.testIin(),
                        TestConfig.testPhone(),
                        "SUCCESS",
                        0,
                        100
                );

        assertThat(response.statusCode())
                .isEqualTo(200);

        PayoutPage page =
                response.as(PayoutPage.class);

        assertThat(page.content())
                .isNotNull();

        return page.content();
    }

    public Payout getLatestSuccessfulPayout() {

        List<Payout> payouts =
                getSuccessfulPayouts();

        assertThat(payouts)
                .isNotEmpty();

        return payouts.get(0);
    }

    public Payout ensureSuccessfulFunding(double requiredAmount) {

        Payout payout =
                getLatestSuccessfulPayout();

        assertThat(payout.amount())
                .isGreaterThanOrEqualTo(requiredAmount);

        return payout;
    }

    public double getRefundedAmountForPayout(long payoutId) {

        loadRefundedAmountsIfNeeded();

        return refundedAmountsByPayout.getOrDefault(
                payoutId,
                0.0
        );
    }

    public double getAvailableAmount(Payout payout) {

        double refundedAmount =
                getRefundedAmountForPayout(payout.id());

        return payout.amount() - refundedAmount;
    }

    private void loadRefundedAmountsIfNeeded() {

        if (refundedAmountsByPayout != null) {
            return;
        }

        refundedAmountsByPayout = new HashMap<>();

        Response response =
                refundClient.searchRefunds(
                        TestConfig.testIin(),
                        TestConfig.testPhone(),
                        null,
                        0,
                        100
                );

        assertThat(response.statusCode())
                .isEqualTo(200);

        List<Long> refundIds =
                response.jsonPath()
                        .getList("content.id", Long.class);

        if (refundIds == null || refundIds.isEmpty()) {
            return;
        }

        for (Long refundId : refundIds) {

            Response detailResponse =
                    refundClient.getRefund(refundId);

            assertThat(detailResponse.statusCode())
                    .isEqualTo(200);

            RefundDetail refund =
                    detailResponse.as(RefundDetail.class);

            if (refund.allocations() == null) {
                continue;
            }

            refund.allocations()
                    .stream()
                    .filter(allocation ->
                            allocation.payoutId() != null)
                    .filter(allocation ->
                            "SUCCESS".equals(allocation.status()))
                    .forEach(allocation ->
                            refundedAmountsByPayout.merge(
                                    allocation.payoutId(),
                                    allocation.amount(),
                                    Double::sum
                            )
                    );
        }
    }
}