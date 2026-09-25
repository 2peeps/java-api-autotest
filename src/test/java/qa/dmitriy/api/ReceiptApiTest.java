package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.ReceiptClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiptApiTest {

    private static final String TEST_IIN =
            "880324301100";

    private static final String TEST_RECEIPT_ID =
            "212f3245-81c7-4b08-92fc-af4a85bc81da";

    private final ReceiptClient receiptClient =
            new ReceiptClient();

    @Test
    void shouldGetReceipts() {
        Response response =
                receiptClient.getReceipts(
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);
    }

    @Test
    void shouldReturnValidReceiptStructure() {
        Response response =
                receiptClient.getReceipts(
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> receipts =
                response.jsonPath()
                        .getList("content");

        assertThat(receipts)
                .isNotNull();

        assertThat(receipts)
                .allSatisfy(receipt -> {
                    assertThat(receipt.get("id"))
                            .isNotNull();

                    assertThat(receipt.get("created_at"))
                            .isNotNull();

                    assertThat(receipt.get("self_employed_iin"))
                            .isNotNull();

                    assertThat(receipt.get("service_code"))
                            .isNotNull();

                    assertThat(receipt.get("service_name"))
                            .isNotNull();

                    assertThat(receipt.get("price"))
                            .isNotNull();

                    assertThat(receipt.get("status"))
                            .isNotNull();
                });
    }

    @Test
    void shouldFilterReceiptsByStatus() {
        Response response =
                receiptClient.getReceiptsByStatus(
                        "SENT",
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> receipts =
                response.jsonPath()
                        .getList("content");

        assertThat(receipts)
                .isNotNull();

        assertThat(receipts)
                .allSatisfy(receipt -> {
                    Map<String, Object> status =
                            (Map<String, Object>) receipt.get("status");

                    assertThat(status)
                            .isNotNull();

                    assertThat(status.get("code"))
                            .isEqualTo("SENT");
                });
    }

    @Test
    void shouldFilterReceiptsByMultipleStatuses() {
        Response response =
                receiptClient.getReceiptsByStatuses(
                        "SENT",
                        "REVERTED",
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> receipts =
                response.jsonPath()
                        .getList("content");

        assertThat(receipts)
                .isNotNull();

        assertThat(receipts)
                .allSatisfy(receipt -> {
                    Map<String, Object> status =
                            (Map<String, Object>) receipt.get("status");

                    assertThat(status)
                            .isNotNull();

                    assertThat(status.get("code"))
                            .isIn("SENT", "REVERTED");
                });
    }

    @Test
    void shouldFilterReceiptsByDateRange() {
        Response response =
                receiptClient.getReceiptsByDateRange(
                        "2026-04-01",
                        "2026-04-05",
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);
    }

    @Test
    void shouldHandleSecondPage() {
        Response response =
                receiptClient.getReceipts(
                        1,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("number"))
                .isEqualTo(1);

        assertThat(response.jsonPath().getInt("pageable.page_number"))
                .isEqualTo(1);

        assertThat(response.jsonPath().getInt("pageable.offset"))
                .isEqualTo(20);
    }

    @Test
    void shouldHandleLargePageSize() {
        Response response =
                receiptClient.getReceipts(
                        0,
                        200,
                        "createdAt,DESC"
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
                receiptClient.getReceipts(
                        -2,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("number"))
                .isEqualTo(0);

        assertThat(response.jsonPath().getInt("pageable.page_number"))
                .isEqualTo(0);

        assertThat(response.jsonPath().getInt("pageable.offset"))
                .isEqualTo(0);
    }

    @Test
    void shouldRejectInvalidStatus() {
        Response response =
                receiptClient.getReceiptsByStatus(
                        "INVALID_STATUS",
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectInvalidCreatedFromDate() {
        Response response =
                receiptClient.getReceiptsWithFilters(
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

    @Test
    void shouldRejectInvalidCreatedToDate() {
        Response response =
                receiptClient.getReceiptsWithFilters(
                        null,
                        null,
                        "invalid-date",
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectRequestWithoutAuthentication() {
        Response response =
                receiptClient.getReceiptsWithoutAuthentication(
                        0,
                        20,
                        "createdAt,DESC"
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

    @Test
    void shouldGetReceiptById() {
        Response response =
                receiptClient.getReceiptById(TEST_RECEIPT_ID);

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getString("id"))
                .isEqualTo(TEST_RECEIPT_ID);

        assertThat(response.jsonPath().getString("self_employed_iin"))
                .isEqualTo(TEST_IIN);
    }

    @Test
    void shouldReturnValidReceiptByIdStructure() {
        Response response =
                receiptClient.getReceiptById(TEST_RECEIPT_ID);

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getString("id"))
                .isEqualTo(TEST_RECEIPT_ID);

        assertThat(response.jsonPath().getString("created_at"))
                .isNotBlank();

        assertThat(response.jsonPath().getString("self_employed_iin"))
                .isEqualTo(TEST_IIN);

        assertThat(response.jsonPath().getString("self_employed_fio"))
                .isNotBlank();

        assertThat(response.jsonPath().getString("service_code"))
                .isNotBlank();

        assertThat(response.jsonPath().getString("service_name"))
                .isNotBlank();

        Object price =
                response.jsonPath().get("price");

        assertThat(price)
                .isNotNull();

        Map<String, Object> status =
                response.jsonPath().getMap("status");

        assertThat(status)
                .isNotNull();

        assertThat(status.get("code"))
                .isNotNull();

        assertThat(status.get("name"))
                .isNotNull();
    }

    @Test
    void shouldReturnNotFoundForUnknownReceiptId() {
        Response response =
                receiptClient.getReceiptById(
                        "00000000-0000-0000-0000-000000000000"
                );

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldRejectInvalidReceiptId() {
        Response response =
                receiptClient.getReceiptById(
                        "invalid-id"
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectReceiptByIdWithoutAuthentication() {
        Response response =
                receiptClient.getReceiptByIdWithoutAuthentication(
                        "00000000-0000-0000-0000-000000000000"
                );

        response.then()
                .statusCode(401);
    }
}