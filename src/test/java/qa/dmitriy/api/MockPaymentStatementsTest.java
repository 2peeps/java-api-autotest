package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.base.BaseApiTest;

import static org.assertj.core.api.Assertions.assertThat;

class MockPaymentStatementsTest extends BaseApiTest {

    @Test
    void shouldCreatePaymentStatementMock() {
        String statementId = "AUTO-SZ208-CREATE-001";

        Response response = mockPaymentStatementsClient.createStatement(
                """
                {
                  "statementId": "%s"
                }
                """.formatted(statementId)
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getString("statementId"))
                .isEqualTo(statementId);

        assertThat(response.jsonPath().getString("status"))
                .isEqualTo("ACCEPTED");

        assertThat(response.jsonPath().getInt("requestedCount"))
                .isPositive();

        assertThat(response.jsonPath().getString("createdAt"))
                .isNotBlank();
    }

    @Test
    void shouldReturnRequestedItemIdsFromStatusesMock() {
        String statementId = "AUTO-SZ208-STATUS-001";
        String firstItemId = "AUTO-ITEM-001";
        String secondItemId = "AUTO-ITEM-002";

        Response response = mockPaymentStatementsClient.getItemStatuses(
                statementId,
                """
                {
                  "itemIds": [
                    "%s",
                    "%s"
                  ]
                }
                """.formatted(firstItemId, secondItemId)
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getList("items.itemId"))
                .containsExactly(firstItemId, secondItemId);
    }

    @Test
    void shouldCancelPaymentStatementMock() {
        String statementId = "AUTO-SZ208-CANCEL-001";

        Response response = mockPaymentStatementsClient.cancelStatement(statementId);

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getString("statementId"))
                .isEqualTo(statementId);

        assertThat(response.jsonPath().getString("status"))
                .isEqualTo("CANCELED");

        assertThat(response.jsonPath().getInt("canceledItemsCount"))
                .isPositive();

        assertThat(response.jsonPath().getString("canceledAt"))
                .isNotBlank();
    }
}