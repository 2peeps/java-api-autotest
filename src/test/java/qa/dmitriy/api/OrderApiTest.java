package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.OrderClient;
import qa.dmitriy.config.TestConfig;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderApiTest {

    private static final String TEST_IIN =
            TestConfig.rewardPaymentTestIin();

    private static final long TEST_ORDER_ID =
            34L;

    private static final String DEFAULT_SORT =
            "";

    private final OrderClient orderClient =
            new OrderClient();

    @Test
    void shouldGetOrders() {
        Response response =
                orderClient.getOrders(
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);

        List<Map<String, Object>> orders =
                response.jsonPath()
                        .getList("content");

        assertThat(orders)
                .isNotNull()
                .isNotEmpty();

        assertThat(orders)
                .hasSize(17);
    }

    @Test
    void shouldReturnValidOrderStructure() {
        Response response =
                orderClient.getOrders(
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> orders =
                response.jsonPath()
                        .getList("content");

        assertThat(orders)
                .isNotNull()
                .isNotEmpty();

        assertThat(orders)
                .allSatisfy(order -> {
                    assertThat(order.get("id"))
                            .isNotNull();

                    assertThat(order.get("order_number"))
                            .isNotNull();

                    Map<String, Object> status =
                            (Map<String, Object>) order.get("status");

                    assertThat(status)
                            .isNotNull();

                    assertThat(status.get("code"))
                            .isNotNull();

                    assertThat(status.get("name"))
                            .isNotNull();

                    assertThat(order.get("upload_date"))
                            .isNotNull();

                    assertThat(order.get("company_iin_bin"))
                            .isEqualTo(TEST_IIN);

                    assertThat(order.get("company_name"))
                            .isNotNull();

                    assertThat(order.get("customer_name"))
                            .isNotNull();

                    assertThat(order.get("customer_iin_bin"))
                            .isNotNull();

                    assertThat(order.get("execution_date"))
                            .isNotNull();

                    assertThat(order.get("order_name"))
                            .isNotNull();

                    assertThat(order.get("order_description"))
                            .isNotNull();

                    assertThat(order.get("city"))
                            .isNotNull();

                    assertThat(order.get("warehouse"))
                            .isNotNull();

                    assertThat(order.get("service_type"))
                            .isNotNull();

                    assertThat(order.get("service_code"))
                            .isNotNull();
                });
    }

    @Test
    void shouldReturnExpectedOrderForTestUser() {
        Response response =
                orderClient.getOrders(
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> orders =
                response.jsonPath()
                        .getList("content");

        Map<String, Object> order =
                orders.stream()
                        .filter(item ->
                                TEST_ORDER_ID ==
                                        ((Number) item.get("id"))
                                                .longValue()
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new AssertionError(
                                        "Expected order was not found"
                                )
                        );

        assertThat(order.get("company_iin_bin"))
                .isEqualTo(TEST_IIN);

        assertThat(order.get("order_number"))
                .isEqualTo("700014");

        assertThat(order.get("service_code"))
                .isEqualTo("45614");
    }

    @Test
    void shouldHandleSecondPage() {
        Response response =
                orderClient.getOrders(
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

        assertThat(response.jsonPath().getBoolean("empty"))
                .isTrue();
    }

    @Test
    void shouldHandleLargePageSize() {
        Response response =
                orderClient.getOrders(
                        0,
                        200,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("size"))
                .isEqualTo(200);

        assertThat(
                response.jsonPath()
                        .getInt("number_of_elements")
        ).isEqualTo(17);

        assertThat(response.jsonPath().getBoolean("last"))
                .isTrue();
    }

    @Test
    void shouldNormalizeNegativePageToZero() {
        Response response =
                orderClient.getOrders(
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
    void shouldRejectOrdersWithoutAuthentication() {
        Response response =
                orderClient.getOrdersWithoutAuthentication(
                        0,
                        20,
                        DEFAULT_SORT
                );

        response.then()
                .statusCode(401);
    }

    @Test
    void shouldGetOrderById() {
        Response response =
                orderClient.getOrderById(
                        TEST_ORDER_ID
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getLong("id"))
                .isEqualTo(TEST_ORDER_ID);

        assertThat(
                response.jsonPath()
                        .getString("order_number")
        ).isEqualTo("700014");

        assertThat(
                response.jsonPath()
                        .getString("company_iin_bin")
        ).isEqualTo(TEST_IIN);
    }

    @Test
    void shouldReturnValidOrderByIdStructure() {
        Response response =
                orderClient.getOrderById(
                        TEST_ORDER_ID
                );

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getLong("id"))
                .isEqualTo(TEST_ORDER_ID);

        assertThat(
                response.jsonPath()
                        .getString("order_number")
        ).isNotBlank();

        Map<String, Object> status =
                response.jsonPath()
                        .getMap("status");

        assertThat(status)
                .isNotNull();

        assertThat(status.get("code"))
                .isNotNull();

        assertThat(status.get("name"))
                .isNotNull();

        assertThat(
                response.jsonPath()
                        .getString("company_iin_bin")
        ).isEqualTo(TEST_IIN);

        assertThat(
                response.jsonPath()
                        .getString("customer_name")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("execution_date")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("order_name")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("city")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("warehouse")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("service_type")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("service_code")
        ).isNotBlank();
    }

    @Test
    void shouldReturnNotFoundForUnknownOrderId() {
        Response response =
                orderClient.getOrderById(
                        999999L
                );

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForZeroOrderId() {
        Response response =
                orderClient.getOrderById(0L);

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForNegativeOrderId() {
        Response response =
                orderClient.getOrderById(-1L);

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldRejectOrderByIdWithoutAuthentication() {
        Response response =
                orderClient.getOrderByIdWithoutAuthentication(
                        TEST_ORDER_ID
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