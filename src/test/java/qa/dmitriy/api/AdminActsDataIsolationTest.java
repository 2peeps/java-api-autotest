package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.auth.AdminTokenProvider;
import qa.dmitriy.client.AdminActsClient;
import qa.dmitriy.config.TestConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdminActsDataIsolationTest {

    private static final int AR_ACT_ID = 28750;
    private static final int SMART_STAFF_ACT_ID = 29362;

    private final AdminActsClient adminActsClient = new AdminActsClient();
    private final AdminTokenProvider adminTokenProvider = new AdminTokenProvider();

    @Test
    void shouldReturnOwnActForArConsultAdmin() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminArConsultEmail(),
                TestConfig.adminArConsultPassword()
        );

        Response response = adminActsClient.getActs(
                accessToken,
                AR_ACT_ID
        );

        response.then()
                .statusCode(200);

        List<Integer> actIds = response.jsonPath()
                .getList("content.id", Integer.class);

        assertThat(actIds)
                .contains(AR_ACT_ID);
    }

    @Test
    void shouldReturnOwnActForSmartStaffAdmin() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminSmartStaffEmail(),
                TestConfig.adminSmartStaffPassword()
        );

        Response response = adminActsClient.getActs(
                accessToken,
                SMART_STAFF_ACT_ID
        );

        response.then()
                .statusCode(200);

        List<Integer> actIds = response.jsonPath()
                .getList("content.id", Integer.class);

        assertThat(actIds)
                .contains(SMART_STAFF_ACT_ID);
    }

    @Test
    void shouldReturnEmptyContentWhenArConsultAdminRequestsSmartStaffAct() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminArConsultEmail(),
                TestConfig.adminArConsultPassword()
        );

        Response response = adminActsClient.getActs(
                accessToken,
                SMART_STAFF_ACT_ID
        );

        response.then()
                .statusCode(200);

        List<Integer> actIds = response.jsonPath()
                .getList("content.id", Integer.class);

        assertThat(actIds)
                .isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenSmartStaffAdminRequestsArConsultActById() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminSmartStaffEmail(),
                TestConfig.adminSmartStaffPassword()
        );

        Response response = adminActsClient.getActById(
                accessToken,
                AR_ACT_ID
        );

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldReturnUnauthorizedWithoutAuthentication() {
        Response response = adminActsClient.getActsWithoutAuthentication(
                AR_ACT_ID
        );

        response.then()
                .statusCode(401);
    }

    @Test
    void shouldReturnOwnActByIdForArConsultAdmin() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminArConsultEmail(),
                TestConfig.adminArConsultPassword()
        );

        Response response = adminActsClient.getActById(
                accessToken,
                AR_ACT_ID
        );

        response.then()
                .statusCode(200);

        Integer actId = response.jsonPath()
                .getInt("id");

        assertThat(actId)
                .isEqualTo(AR_ACT_ID);
    }

    @Test
    void shouldReturnOwnActByIdForSmartStaffAdmin() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminSmartStaffEmail(),
                TestConfig.adminSmartStaffPassword()
        );

        Response response = adminActsClient.getActById(
                accessToken,
                SMART_STAFF_ACT_ID
        );

        response.then()
                .statusCode(200);

        Integer actId = response.jsonPath()
                .getInt("id");

        assertThat(actId)
                .isEqualTo(SMART_STAFF_ACT_ID);
    }

    @Test
    void shouldReturnNotFoundWhenArConsultAdminRequestsSmartStaffActById() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminArConsultEmail(),
                TestConfig.adminArConsultPassword()
        );

        Response response = adminActsClient.getActById(
                accessToken,
                SMART_STAFF_ACT_ID
        );

        response.then()
                .statusCode(404);
    }
}