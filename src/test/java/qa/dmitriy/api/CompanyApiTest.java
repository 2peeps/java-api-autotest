package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import qa.dmitriy.client.CompanyClient;
import qa.dmitriy.model.CompanyRequest;
import java.util.Comparator;

import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("invalidCompanyRequests")
    void shouldRejectCompanyWithMissingRequiredField(
        String requestBody) {

    Response response =
            companyClient.createCompany(requestBody);

    assertThat(response.statusCode())
            .isEqualTo(400);
    }

    static Stream<Arguments> invalidCompanyRequests() {
    return Stream.of(
            Arguments.of("""
                    {
                      "bin": "123456789012",
                      "avr_sms_text": "Тестовое SMS",
                      "avr_sms_text_finishing": "Тестовое завершение"
                    }
                    """),

            Arguments.of("""
                    {
                      "name": "Autotest Company",
                      "avr_sms_text": "Тестовое SMS",
                      "avr_sms_text_finishing": "Тестовое завершение"
                    }
                    """),

            Arguments.of("""
                    {
                      "name": "Autotest Company",
                      "bin": "123456789012",
                      "avr_sms_text_finishing": "Тестовое завершение"
                    }
                    """),

            Arguments.of("""
                    {
                      "name": "Autotest Company",
                      "bin": "123456789012",
                      "avr_sms_text": "Тестовое SMS"
                    }
                    """)
    );
    }

    @Test
    void shouldRejectCompanyWithDuplicateBin() {

    Response companiesResponse =
            companyClient.getCompanies(0, 1);

    assertThat(companiesResponse.statusCode())
            .isEqualTo(200);

    String existingBin =
            companiesResponse.jsonPath()
                    .getString("content[0].bin");

    assertThat(existingBin)
            .isNotBlank();

    CompanyRequest request =
            new CompanyRequest(
                    "Autotest Duplicate Company",
                    existingBin,
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

    @Test
    void shouldUpdateCompany() {

    Response companiesResponse =
            companyClient.getCompanies(0, 1);

    assertThat(companiesResponse.statusCode())
            .isEqualTo(200);

    Long companyId =
            companiesResponse.jsonPath()
                    .getLong("content[0].id");

    String existingBin =
            companiesResponse.jsonPath()
                    .getString("content[0].bin");

    assertThat(companyId)
            .isNotNull();

    assertThat(existingBin)
            .isNotBlank();

    CompanyRequest request =
            new CompanyRequest(
                    "Autotest Updated Company",
                    existingBin,
                    "LEGAL_ENTITY",
                    "CASHLESS",
                    1,
                    "TEST",
                    "123456789012",
                    "Test Buyer",
                    "12345678901234567890",
                    "17",
                    "12345678901",
                    "Test Bank",
                    100000.0,
                    "TEST",
                    "Test position",
                    "Test FIO",
                    "TEST-001",
                    "Test contract",
                    "Обновленное SMS",
                    "Обновленное завершение"
            );

    Response response =
            companyClient.updateCompany(companyId, request);

    assertThat(response.statusCode())
            .isEqualTo(200);

    Response updatedCompaniesResponse =
            companyClient.getCompanies(0, 10);

    assertThat(updatedCompaniesResponse.statusCode())
            .isEqualTo(200);

    String updatedName =
            updatedCompaniesResponse.jsonPath()
                    .getString(
                        "content.find { it.id == " + companyId + " }.name"
                    );

    assertThat(updatedName)
            .isEqualTo("Autotest Updated Company");
    }

    @Test
    void shouldRejectUpdateForNonExistingCompany() {

    long nonExistingCompanyId = 999999999L;

    CompanyRequest request =
            new CompanyRequest(
                    "Autotest Company",
                    "123456789012",
                    "LEGAL_ENTITY",
                    "CASHLESS",
                    1,
                    "TEST",
                    "123456789012",
                    "Test Buyer",
                    "12345678901234567890",
                    "17",
                    "12345678901",
                    "Test Bank",
                    100000.0,
                    "TEST",
                    "Test position",
                    "Test FIO",
                    "TEST-001",
                    "Test contract",
                    "Тестовое SMS",
                    "Тестовое завершение"
            );

    Response response =
            companyClient.updateCompany(
                    nonExistingCompanyId,
                    request
            );

    assertThat(response.statusCode())
            .isIn(400, 404);
    }

    @Test
    void shouldFilterCompaniesById() {

    Response companiesResponse =
            companyClient.getCompanies(0, 1);

    assertThat(companiesResponse.statusCode())
            .isEqualTo(200);

    Long companyId =
            companiesResponse.jsonPath()
                    .getLong("content[0].id");

    assertThat(companyId)
            .isNotNull();

    Response filteredResponse =
            companyClient.getCompaniesById(companyId);

    assertThat(filteredResponse.statusCode())
            .isEqualTo(200);

    assertThat(filteredResponse.jsonPath().getList("content"))
            .isNotNull();

    assertThat(
            filteredResponse.jsonPath()
                    .getList("content.id", Long.class)
    )
            .containsOnly(companyId);
    }

    @Test
    void shouldHandleNegativePage() {

        Response response =
                companyClient.getCompanies(-1, 10);

        assertThat(response.statusCode())
                .isEqualTo(200);
    }

    @Test
    void shouldSortCompaniesByNameAscending() {

    Response response =
            companyClient.getCompaniesSorted(
                    0,
                    10,
                    "name,ASC"
            );

    assertThat(response.statusCode())
            .isEqualTo(200);

    var names =
            response.jsonPath()
                    .getList("content.name", String.class);

    assertThat(names)
            .isNotNull();

    assertThat(names)
            .isSortedAccordingTo(String::compareToIgnoreCase);
    }

    @Test
    void shouldSortCompaniesByNameDescending() {

        Response response =
                companyClient.getCompaniesSorted(
                        0,
                        10,
                        "name,DESC"
                );
        assertThat(response.statusCode())
                .isEqualTo(200);

        var names =
                response.jsonPath()
                        .getList("content.name", String.class);

        assertThat(names)
                .isNotNull();

        assertThat(names)
                .isSortedAccordingTo(
                        Comparator.reverseOrder()
                );
    }
}