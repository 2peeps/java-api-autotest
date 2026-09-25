package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.ActClient;
import qa.dmitriy.config.TestConfig;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ActApiTest {

    private static final String TEST_IIN =
            TestConfig.rewardPaymentTestIin();

    private static final String TEST_ACT_ID =
            "28750";

    private static final String TEST_DEAL_NUMBER =
            "800007";

    private static final String TEST_STATUS =
            "PENDING_SELF_EMPLOYED_SIGNING";

    private static final int TEST_MONTH_CREATED =
            8;

    private final ActClient actClient =
            new ActClient();

    @Test
    void shouldGetActs() {
        Response response =
                actClient.getActs(
                        0,
                        20,
                        ""
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> acts =
                response.jsonPath()
                        .getList("content");

        assertThat(acts)
                .isNotNull()
                .isNotEmpty();

        assertThat(
                response.jsonPath()
                        .getInt("total_elements")
        ).isGreaterThan(0);

        assertThat(
                response.jsonPath()
                        .getInt("number")
        ).isEqualTo(0);
    }

    @Test
    void shouldReturnValidActStructure() {
        Response response =
                actClient.getActs(
                        0,
                        20,
                        ""
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> acts =
                response.jsonPath()
                        .getList("content");

        assertThat(acts)
                .isNotNull()
                .isNotEmpty();

        assertThat(acts)
                .allSatisfy(act -> {
                    assertThat(act.get("id"))
                            .isNotNull();

                    assertThat(act.get("iin"))
                            .isEqualTo(TEST_IIN);

                    assertThat(act.get("deal_number"))
                            .isNotNull();

                    Map<String, Object> status =
                            (Map<String, Object>) act.get("status");

                    assertThat(status)
                            .isNotNull();

                    assertThat(status.get("code"))
                            .isNotNull();

                    assertThat(status.get("name"))
                            .isNotNull();

                    assertThat(act.get("month_created"))
                            .isNotNull();

                    assertThat(act.get("month_created_name"))
                            .isNotNull();

                    assertThat(act.get("work_act_type"))
                            .isNotNull();
                });
    }

    @Test
    void shouldHandleSecondPage() {
        Response response =
                actClient.getActs(
                        1,
                        20,
                        ""
                );

        response.then()
                .statusCode(200);

        assertThat(
                response.jsonPath()
                        .getInt("number")
        ).isEqualTo(1);

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
                actClient.getActs(
                        0,
                        200,
                        ""
                );

        response.then()
                .statusCode(200);

        assertThat(
                response.jsonPath()
                        .getInt("size")
        ).isEqualTo(200);

        assertThat(
                response.jsonPath()
                        .getInt("number_of_elements")
        ).isGreaterThan(0);
    }

    @Test
    void shouldNormalizeNegativePageToZero() {
        Response response =
                actClient.getActs(
                        -2,
                        20,
                        ""
                );

        response.then()
                .statusCode(200);

        assertThat(
                response.jsonPath()
                        .getInt("number")
        ).isEqualTo(0);

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
    void shouldFindActByIdFilter() {
        Response response =
                actClient.getActsWithFilters(
                        TEST_ACT_ID,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        ""
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> acts =
                response.jsonPath()
                        .getList("content");

        assertThat(acts)
                .isNotNull()
                .isNotEmpty();

        assertThat(acts)
                .allSatisfy(act ->
                        assertThat(
                                String.valueOf(
                                        act.get("id")
                                )
                        ).isEqualTo(TEST_ACT_ID)
                );
    }

    @Test
    void shouldFindActByDealNumber() {
        Response response =
                actClient.getActsWithFilters(
                        null,
                        TEST_DEAL_NUMBER,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        ""
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> acts =
                response.jsonPath()
                        .getList("content");

        assertThat(acts)
                .isNotNull()
                .isNotEmpty();

        assertThat(acts)
                .allSatisfy(act ->
                        assertThat(
                                act.get("deal_number")
                        ).isEqualTo(TEST_DEAL_NUMBER)
                );
    }

    @Test
    void shouldFilterActsByStatus() {
        Response response =
                actClient.getActsWithFilters(
                        null,
                        null,
                        TEST_STATUS,
                        null,
                        null,
                        null,
                        0,
                        20,
                        ""
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> acts =
                response.jsonPath()
                        .getList("content");

        assertThat(acts)
                .isNotNull()
                .isNotEmpty();

        assertThat(acts)
                .allSatisfy(act -> {
                    Map<String, Object> status =
                            (Map<String, Object>) act.get("status");

                    assertThat(status)
                            .isNotNull();

                    assertThat(
                            status.get("code")
                    ).isEqualTo(TEST_STATUS);
                });
    }

    @Test
    void shouldFilterActsByMonthCreated() {
        Response response =
                actClient.getActsWithFilters(
                        null,
                        null,
                        null,
                        null,
                        null,
                        TEST_MONTH_CREATED,
                        0,
                        20,
                        ""
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> acts =
                response.jsonPath()
                        .getList("content");

        assertThat(acts)
                .isNotNull()
                .isNotEmpty();

        assertThat(acts)
                .allSatisfy(act ->
                        assertThat(
                                act.get("month_created")
                        ).isEqualTo(TEST_MONTH_CREATED)
                );
    }

    @Test
    void shouldRejectActsWithoutAuthentication() {
        Response response =
                actClient.getActsWithoutAuthentication(
                        0,
                        20,
                        ""
                );

        response.then()
                .statusCode(401);
    }

    @Test
    void shouldGetActById() {
        Response response =
                actClient.getActById(TEST_ACT_ID);

        response.then()
                .statusCode(200);

        assertThat(
                response.jsonPath()
                        .getLong("id")
        ).isEqualTo(Long.parseLong(TEST_ACT_ID));

        assertThat(
                response.jsonPath()
                        .getString("iin")
        ).isEqualTo(TEST_IIN);

        assertThat(
                response.jsonPath()
                        .getString("deal_number")
        ).isEqualTo(TEST_DEAL_NUMBER);
    }

    @Test
    void shouldReturnValidActByIdStructure() {
        Response response =
                actClient.getActById(TEST_ACT_ID);

        response.then()
                .statusCode(200);

        assertThat(
                response.jsonPath()
                        .getLong("id")
        ).isEqualTo(Long.parseLong(TEST_ACT_ID));

        assertThat(
                response.jsonPath()
                        .getString("iin")
        ).isEqualTo(TEST_IIN);

        assertThat(
                response.jsonPath()
                        .getString("deal_number")
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
                        .getInt("month_created")
        ).isEqualTo(TEST_MONTH_CREATED);

        assertThat(
                response.jsonPath()
                        .getString("month_created_name")
        ).isNotBlank();

        assertThat(
                response.jsonPath()
                        .getString("work_act_type")
        ).isNotBlank();
    }

    @Test
    void shouldReturnNotFoundForUnknownActId() {
        Response response =
                actClient.getActById("999999999");

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldRejectInvalidActId() {
        Response response =
                actClient.getActById("invalid-id");

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectActByIdWithoutAuthentication() {
        Response response =
                actClient.getActByIdWithoutAuthentication(
                        TEST_ACT_ID
                );

        response.then()
                .statusCode(401);
    }
}