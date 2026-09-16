package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.auth.ExternalSystemTokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;
import qa.dmitriy.model.EmployeeCheckRequest;

import static io.restassured.RestAssured.given;

public class EmployeeCheckClient {

    private static final String EMPLOYEE_CHECK_ENDPOINT =
            "/api/employee-checks";

    private static final String EMPLOYEE_CHECK_STATUS_ENDPOINT =
            "/api/employee-checks/%s/status";

    private final ExternalSystemTokenProvider tokenProvider =
            new ExternalSystemTokenProvider();

    private static final String EMPLOYEE_CHECK_RESULT_ENDPOINT =
            "/api/employee-checks/%s";

    private static final String EMPLOYEE_CHECK_ARCHIVE_ENDPOINT =
            "/api/employee-checks/%s/archive";

    public Response submitCheckWithoutAuthentication(
            EmployeeCheckRequest request) {

        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .contentType("application/json")
                .body(request)
                .when()
                .post(EMPLOYEE_CHECK_ENDPOINT);}

    public Response getResultWithoutAuthentication(String jobId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .when()
                .get(EMPLOYEE_CHECK_RESULT_ENDPOINT.formatted(jobId));
    }

    public Response getStatusWithoutAuthentication(String jobId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .when()
                .get(EMPLOYEE_CHECK_STATUS_ENDPOINT.formatted(jobId));
    }

    public Response getArchiveWithoutAuthentication(String jobId) {
        return given()
            .spec(RestAssuredConfig.defaultSpecification())
            .baseUri(TestConfig.walletBaseUrl())
            .when()
            .get(EMPLOYEE_CHECK_ARCHIVE_ENDPOINT.formatted(jobId));
}

    public Response getArchiveWithoutAuthentification(String jobId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .when()
                .get(EMPLOYEE_CHECK_ARCHIVE_ENDPOINT.formatted(jobId));
    }

    public Response getArchive(String jobId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                )
                .when()
                .get(EMPLOYEE_CHECK_ARCHIVE_ENDPOINT.formatted(jobId));
    }

    public Response getResult(String jobId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                )
                .when()
                .get(EMPLOYEE_CHECK_RESULT_ENDPOINT.formatted(jobId));
    }

    public Response submitCheck(EmployeeCheckRequest request) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                )
                .contentType("application/json")
                .body(request)
                .when()
                .post(EMPLOYEE_CHECK_ENDPOINT);
    }

    public Response getStatus(String jobId) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                )
                .when()
                .get(EMPLOYEE_CHECK_STATUS_ENDPOINT.formatted(jobId));
    }
}