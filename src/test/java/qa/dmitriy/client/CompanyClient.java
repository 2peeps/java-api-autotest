package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.auth.TokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;
import qa.dmitriy.model.CompanyRequest;

import static io.restassured.RestAssured.given;

public class CompanyClient {

    private static final String COMPANIES_ENDPOINT =
            "/api/companies";

    private final TokenProvider tokenProvider =
            new TokenProvider();

    public Response getCompanies() {

        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                )
                .when()
                .get(COMPANIES_ENDPOINT);
    }

    public Response getCompanies(
            int page,
            int size) {

        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                )
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get(COMPANIES_ENDPOINT);
    }

    public Response getCompaniesByBin(String bin) {

        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .queryParam("bins", bin)
                .header(
                        "Authorization",
                        "Bearer " + tokenProvider.getAccessToken()
                )
                .when()
                .get(COMPANIES_ENDPOINT);
    }

    public Response getCompaniesWithoutAuthentication() {

        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .when()
                .get(COMPANIES_ENDPOINT);
    }

    public Response createCompany(CompanyRequest request) {

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
                .post(COMPANIES_ENDPOINT);
    }
}