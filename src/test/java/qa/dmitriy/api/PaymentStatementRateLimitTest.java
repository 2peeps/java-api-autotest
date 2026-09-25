package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import qa.dmitriy.auth.EcomTokenProvider;
import qa.dmitriy.base.BaseApiTest;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatementRateLimitTest extends BaseApiTest {

    private static final String STATEMENT_ID = "ECOM-2026-09-25-0017";
    private static final int RATE_LIMIT = 30;
    private static final int RATE_LIMIT_WINDOW_SECONDS = 60;

    private final EcomTokenProvider tokenProvider =
            new EcomTokenProvider();

    @BeforeEach
    void waitForRateLimitWindow() throws InterruptedException {
        Thread.sleep((RATE_LIMIT_WINDOW_SECONDS + 5L) * 1000);
    }

    @Test
    void shouldReturn429After30PaymentStatementStatusRequests() {

        String token = tokenProvider.getAccessToken();

        for (int i = 1; i <= RATE_LIMIT; i++) {

            Response response = given()
                    .baseUri(TestConfig.paymentStatementsBaseUrl())
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get(
                            "/api/payment-statements/{statementId}/status",
                            STATEMENT_ID
                    );

            assertThat(response.statusCode())
                    .as(
                            "Request #%d should not be rate limited",
                            i
                    )
                    .isEqualTo(200);
        }

        Response limitedResponse = given()
                .baseUri(TestConfig.paymentStatementsBaseUrl())
                .header("Authorization", "Bearer " + token)
                .when()
                .get(
                        "/api/payment-statements/{statementId}/status",
                        STATEMENT_ID
                );

        assertThat(limitedResponse.statusCode())
                .as(
                        "Request #%d should be rate limited",
                        RATE_LIMIT + 1
                )
                .isEqualTo(429);
    }

    @Test
    void shouldReturn429After30PaymentStatementFullResultRequests() {

    String token = tokenProvider.getAccessToken();

    for (int i = 1; i <= RATE_LIMIT; i++) {

        Response response = given()
                .baseUri(TestConfig.paymentStatementsBaseUrl())
                .header("Authorization", "Bearer " + token)
                .when()
                .get(
                        "/api/payment-statements/{statementId}",
                        STATEMENT_ID
                );

        assertThat(response.statusCode())
                .as(
                        "Full result request #%d should not be rate limited",
                        i
                )
                .isEqualTo(200);
    }

    Response limitedResponse = given()
            .baseUri(TestConfig.paymentStatementsBaseUrl())
            .header("Authorization", "Bearer " + token)
            .when()
            .get(
                    "/api/payment-statements/{statementId}",
                    STATEMENT_ID
            );

    assertThat(limitedResponse.statusCode())
            .as(
                    "Full result request #%d should be rate limited",
                    RATE_LIMIT + 1
            )
            .isEqualTo(429);
    }
    @Test
    void shouldReturn429After30PaymentStatementItemStatusesRequests() {

    String token = tokenProvider.getAccessToken();

    for (int i = 1; i <= RATE_LIMIT; i++) {

        Response response = given()
                .baseUri(TestConfig.paymentStatementsBaseUrl())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                        {
                            "itemIds": [
                                "REC-000145"
                            ]
                        }
                        """)
                .when()
                .post(
                        "/api/payment-statements/{statementId}/items/statuses",
                        STATEMENT_ID
                );

        assertThat(response.statusCode())
                .as(
                        "Item statuses request #%d should not be rate limited",
                        i
                )
                .isEqualTo(200);
    }

    Response limitedResponse = given()
            .baseUri(TestConfig.paymentStatementsBaseUrl())
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body("""
                    {
                        "itemIds": [
                            "REC-000145"
                        ]
                    }
                    """)
            .when()
            .post(
                    "/api/payment-statements/{statementId}/items/statuses",
                    STATEMENT_ID
            );

    assertThat(limitedResponse.statusCode())
            .as(
                    "Item statuses request #%d should be rate limited",
                    RATE_LIMIT + 1
            )
            .isEqualTo(429);
    }
}