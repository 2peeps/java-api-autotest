package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.WalletClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WalletRegistryApiTest {

    private final WalletClient walletClient =
            new WalletClient();

    @Test
    void shouldGetWalletRegistries() {
        Response response =
                walletClient.getWalletRegistries(
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);

        List<Map<String, Object>> registries =
                response.jsonPath().getList("content");

        assertThat(registries)
                .isNotNull();
    }

    @Test
    void shouldReturnValidRegistryStructure() {
        Response response =
                walletClient.getWalletRegistries(
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> registries =
                response.jsonPath().getList("content");

        assertThat(registries)
                .allSatisfy(registry -> {
                    assertThat(registry.get("id"))
                            .isNotNull();

                    assertThat(registry.get("filename"))
                            .isNotNull();

                    assertThat(registry.get("status"))
                            .isNotNull();

                    assertThat(registry.get("success_count"))
                            .isNotNull();

                    assertThat(registry.get("error_count"))
                            .isNotNull();

                    assertThat(registry.get("created_at"))
                            .isNotNull();

                    assertThat(registry.get("updated_at"))
                            .isNotNull();
                });
    }

    @Test
    void shouldFilterRegistriesByStatus() {
        Response response =
                walletClient.getWalletRegistries(
                        null,
                        null,
                        "PROCESSED",
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> registries =
                response.jsonPath().getList("content");

        assertThat(registries)
                .allSatisfy(registry ->
                        assertThat(registry.get("status"))
                                .isEqualTo("PROCESSED")
                );
    }

    @Test
    void shouldFilterRegistriesByFilename() {
        Response response =
                walletClient.getWalletRegistries(
                        null,
                        null,
                        null,
                        "test",
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);
    }

    @Test
    void shouldFilterRegistriesByDateRange() {
        Response response =
                walletClient.getWalletRegistries(
                        "2026-08-01",
                        "2026-09-18",
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);
    }

    @Test
    void shouldRejectRequestWithoutAuthentication() {
        Response response =
                walletClient.getWalletRegistriesWithoutAuthentication(
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(401);
    }

    @Test
    void shouldHandleNegativePage() {
        Response response =
                walletClient.getWalletRegistries(
                        -1,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        assertPageStructure(response);
    }

    @Test
    void shouldHandleZeroSize() {
        Response response =
                walletClient.getWalletRegistries(
                        0,
                        0,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);
    }

    @Test
    void shouldRejectInvalidStatus() {
        Response response =
                walletClient.getWalletRegistries(
                        null,
                        null,
                        "INVALID_STATUS",
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(400);
    }

    @Test
    void shouldRejectInvalidFromDate() {
        Response response =
                walletClient.getWalletRegistries(
                        "invalid-date",
                        null,
                        null,
                        null,
                        0,
                        20,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(400);
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

        assertThat(response.jsonPath().getInt("number_of_elements"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getBoolean("first"))
                .isNotNull();

        assertThat(response.jsonPath().getBoolean("last"))
                .isNotNull();

        assertThat(response.jsonPath().getBoolean("empty"))
                .isNotNull();
    }
}