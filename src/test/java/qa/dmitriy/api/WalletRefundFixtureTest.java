package qa.dmitriy.api;

import org.junit.jupiter.api.Test;
import qa.dmitriy.config.TestConfig;
import qa.dmitriy.fixture.WalletRefundFixture;
import qa.dmitriy.model.Payout;

import static org.assertj.core.api.Assertions.assertThat;

class WalletRefundFixtureTest {

    private final WalletRefundFixture fixture =
            new WalletRefundFixture();

    @Test
    void shouldGetLatestSuccessfulPayout() {

        Payout payout =
                fixture.getLatestSuccessfulPayout();

        assertThat(payout)
                .isNotNull();

        assertThat(payout.id())
                .isNotNull();

        assertThat(payout.iin())
                .isEqualTo(TestConfig.testIin());

        assertThat(payout.phone())
                .isEqualTo(TestConfig.testPhone());

        assertThat(payout.amount())
                .isPositive();

        assertThat(payout.status())
                .isEqualTo("SUCCESS");

        assertThat(payout.externalId())
                .isNotBlank();

        assertThat(payout.transactionId())
                .isNotNull();
    }
}