package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.PayoutClient;
import qa.dmitriy.config.TestConfig;

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
                        0,
                        20
                );

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getList("content"))
                .isNotNull();

        System.out.println(
                "Payout search response:\n" +
                response.asPrettyString()
        );
    }

}