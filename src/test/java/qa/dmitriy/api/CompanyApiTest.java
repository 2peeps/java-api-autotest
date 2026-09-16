package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.CompanyClient;
import qa.dmitriy.model.CompanyRequest;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyApiTest {

    private final CompanyClient companyClient =
            new CompanyClient();

    @Test
    void shouldGetCompanies() {

        Response response =
                companyClient.getCompanies();

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getList("content"))
                .isNotNull();

        assertThat(response.jsonPath().getInt("number"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getInt("size"))
                .isGreaterThan(0);

        assertThat(response.jsonPath().getInt("total_elements"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getInt("total_pages"))
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldGetCompaniesWithPagination() {

    int page = 0;
    int size = 5;

    Response response =
            companyClient.getCompanies(page, size);

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.jsonPath().getInt("number"))
            .isEqualTo(page);

    assertThat(response.jsonPath().getInt("size"))
            .isEqualTo(size);

    assertThat(response.jsonPath().getList("content"))
            .isNotNull();

    assertThat(response.jsonPath().getList("content").size())
            .isLessThanOrEqualTo(size);
    }

    @Test
    void shouldFilterCompaniesByBin() {

    Response initialResponse =
            companyClient.getCompanies(0, 1);

    assertThat(initialResponse.statusCode())
            .isEqualTo(200);

    String bin =
            initialResponse.jsonPath()
                    .getString("content[0].bin");

    assertThat(bin)
            .isNotBlank();

    Response response =
            companyClient.getCompaniesByBin(bin);

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.jsonPath().getList("content"))
            .isNotNull();

    assertThat(response.jsonPath().getInt("total_elements"))
            .isGreaterThan(0);

    response.jsonPath()
            .getList("content.bin", String.class)
            .forEach(companyBin ->
                    assertThat(companyBin)
                            .isEqualTo(bin)
            );
    }

    @Test
    void shouldRejectCompaniesWithoutAuthentication() {

        Response response =
                companyClient.getCompaniesWithoutAuthentication();

        assertThat(response.statusCode())
                .isIn(401, 403);
    }

    @Test
    void shouldHandleNegativePageParameter() {

    Response response =
            companyClient.getCompanies(-1, 10);

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.jsonPath().getInt("number"))
            .isGreaterThanOrEqualTo(0);
}

    @Test
    void shouldHandleZeroSizeParameter() {

    Response response =
            companyClient.getCompanies(0, 0);

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.jsonPath().getList("content"))
            .isNotNull();
    }

        @Test
    void shouldRejectCompanyWithInvalidBin() {

        CompanyRequest request =
                new CompanyRequest(
                        "Autotest Company",
                        "123",
                        null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null,
                        "Тестовое SMS",
                        "Тестовое завершение"
                );

        Response response =
                companyClient.createCompany(request);

        assertThat(response.statusCode())
                .isEqualTo(400);
    }

    private CompanyRequest validCompanyRequest() {
        return new CompanyRequest(
                "Autotest Company",
                "123456789012",
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                "Тестовое SMS",
                "Тестовое завершение"
        );
    }
}