package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.TokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class AdminAuthClient {

    private static final String ADMIN_AUTH_ENDPOINT =
            "/api/auth/admin";

    private static final String ADMIN_REFRESH_ENDPOINT =
            "/api/auth/admin/refresh";

    private static final String ADMIN_ME_ENDPOINT =
            "/api/auth/admin/me";

    private static final String RESET_PASSWORD_ENDPOINT =
            "/api/auth/admin/reset-password";

    private final TokenProvider tokenProvider = new TokenProvider();

    public Response authenticate(String email, String password) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .contentType("application/json")
                .body("""
                        {
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post(ADMIN_AUTH_ENDPOINT);
    }

    public Response refresh(String refreshToken) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .contentType("application/json")
                .body("""
                        {
                          "refresh_token": "%s"
                        }
                        """.formatted(refreshToken))
                .when()
                .post(ADMIN_REFRESH_ENDPOINT);
    }

    public Response getMe() {
        return authorizedRequest()
                .when()
                .get(ADMIN_ME_ENDPOINT);
    }

    public Response getMeWithoutAuthentication() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .when()
                .get(ADMIN_ME_ENDPOINT);
    }

    public Response resetPassword(
            String currentPassword,
            String newPassword) {

        return authorizedRequest()
                .contentType("application/json")
                .body("""
                        {
                          "current_password": "%s",
                          "new_password": "%s"
                        }
                        """.formatted(currentPassword, newPassword))
                .when()
                .put(RESET_PASSWORD_ENDPOINT);
    }

    private RequestSpecification authorizedRequest() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                );
    }
}