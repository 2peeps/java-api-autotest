package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.AdminAuthClient;
import qa.dmitriy.config.TestConfig;

import static org.assertj.core.api.Assertions.assertThat;

class AdminAuthApiTest {

    private final AdminAuthClient adminAuthClient =
            new AdminAuthClient();

    @Test
    void shouldAuthenticateAdmin() {
        Response response = adminAuthClient.authenticate(
                TestConfig.adminEmail(),
                TestConfig.adminPassword()
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getString("access_token"))
                .isNotBlank();

        assertThat(response.jsonPath().getString("refresh_token"))
                .isNotBlank();
    }

    @Test
    void shouldRefreshAdminAccessToken() {
        Response loginResponse = adminAuthClient.authenticate(
                TestConfig.adminEmail(),
                TestConfig.adminPassword()
        );

        assertThat(loginResponse.statusCode())
                .isEqualTo(200);

        String refreshToken = loginResponse.jsonPath()
                .getString("refresh_token");

        Response refreshResponse =
                adminAuthClient.refresh(refreshToken);

        assertThat(refreshResponse.statusCode())
                .isEqualTo(200);

        assertThat(refreshResponse.jsonPath()
                .getString("access_token"))
                .isNotBlank();
    }

    @Test
    void shouldGetCurrentAdminInfo() {
        Response response = adminAuthClient.getMe();

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath()
                .getBoolean("admin"))
                .isTrue();

        assertThat(response.jsonPath()
                .getMap("module_access"))
                .isNotNull();

        assertThat(response.jsonPath()
                .getList("companies"))
                .isNotNull();
    }

    @Test
    void shouldRejectRequestWithoutAuthentication() {
        Response response =
                adminAuthClient.getMeWithoutAuthentication();

        assertThat(response.statusCode())
                .isEqualTo(401);
    }
    @Test
    void shouldRejectAdminWithInvalidPassword() {
        Response response = adminAuthClient.authenticate(
                TestConfig.adminEmail(),
                "WrongPassword123!"
        );

        assertThat(response.statusCode())
                .isEqualTo(400);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("001");
    }
    @Test
    void shouldRejectAdminWithEmptyPassword() {
        Response response = adminAuthClient.authenticate(
                TestConfig.adminEmail(),
                ""
        );

        assertThat(response.statusCode())
                .isEqualTo(400);
    }
    @Test
    void shouldReturnInternalServerErrorForInvalidRefreshToken() {
        Response response = adminAuthClient.refresh(
                "invalid-refresh-token"
        );

        assertThat(response.statusCode())
                .isEqualTo(500);

        assertThat(response.jsonPath().getString("error_code"))
                .isEqualTo("062"); // current DEV behavior: external Keycloak returns 400 invalid_grant,
                                   // but API maps it ti 500 with error_code=062.
    }
}