package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class UserAuthClient {

    private static final String INIT_ENDPOINT =
            "/api/auth/user/init";

    private static final String VERIFY_ENDPOINT =
            "/api/auth/user/verify";

    public Response initAuthentication() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .contentType("application/json")
                .body("""
                        {
                          "iin": "%s",
                          "phone": "%s"
                        }
                        """.formatted(
                        TestConfig.testIin(),
                        TestConfig.testPhone()
                ))
                .when()
                .post(INIT_ENDPOINT);
    }

    public Response verifyAuthentication(String sessionId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .contentType("application/json")
                .body("""
                        {
                          "session_id": "%s",
                          "otp_code": "%s"
                        }
                        """.formatted(
                        sessionId,
                        TestConfig.testOtp()
                ))
                .when()
                .post(VERIFY_ENDPOINT);
    }

    public Response initAuthentication(
            String iin,
            String phone
    ){
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .contentType("application/json")
                .body("""
                        {
                        "iin": "%s",
                        "phone": "%s"
                        }
                        """.formatted(iin, phone))
                .when()
                .post(INIT_ENDPOINT);
    }
}