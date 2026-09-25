package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.WalletClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WalletRegistryDetailsApiTest {

    private final WalletClient walletClient =
            new WalletClient();

    @Test
    void shouldGetWalletRegistryDetails() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getWalletRegistryDetails(registryId);

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getLong("id"))
                .isEqualTo(registryId);
    }

    @Test
    void shouldReturnValidRegistryStructure() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getWalletRegistryDetails(registryId);

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getLong("id"))
                .isPositive();

        assertThat(response.jsonPath().getString("filename"))
                .isNotNull()
                .isNotBlank();

        assertThat(response.jsonPath().getString("status"))
                .isNotNull()
                .isNotBlank();

        assertThat(response.jsonPath().getInt("success_count"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getInt("error_count"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getString("created_at"))
                .isNotNull();

        assertThat(response.jsonPath().getString("updated_at"))
                .isNotNull();
    }

    @Test
    void shouldReturnValidRegistryStatus() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getWalletRegistryDetails(registryId);

        response.then()
                .statusCode(200);

        String status =
                response.jsonPath().getString("status");

        assertThat(status)
                .isIn(
                        "PENDING",
                        "PROCESSING",
                        "PROCESSED",
                        "ERROR"
                );
    }

    @Test
    void shouldReturnItemsCollection() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getWalletRegistryDetails(registryId);

        response.then()
                .statusCode(200);

        List<Map<String, Object>> items =
                response.jsonPath().getList("items");

        assertThat(items)
                .isNotNull();
    }

    @Test
    void shouldReturnNonNegativeCounters() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getWalletRegistryDetails(registryId);

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getInt("success_count"))
                .isGreaterThanOrEqualTo(0);

        assertThat(response.jsonPath().getInt("error_count"))
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldReturnNotFoundForUnknownRegistry() {
        Response response =
                walletClient.getWalletRegistryDetails(
                        Long.MAX_VALUE
                );

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldHandleZeroRegistryId() {
        Response response =
                walletClient.getWalletRegistryDetails(0);

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldHandleNegativeRegistryId() {
        Response response =
                walletClient.getWalletRegistryDetails(-1);

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldRejectRequestWithoutAuthentication() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getWalletRegistryDetailsWithoutAuthentication(
                        registryId
                );

        response.then()
                .statusCode(401);
    }

    private long getExistingRegistryId() {
        Response response =
                walletClient.getWalletRegistries(
                        0,
                        1,
                        "createdAt,DESC"
                );

        response.then()
                .statusCode(200);

        List<Map<String, Object>> registries =
                response.jsonPath().getList("content");

        assertThat(registries)
                .isNotNull()
                .isNotEmpty();

        Object id =
                registries.get(0).get("id");

        assertThat(id)
                .isNotNull();

        return ((Number) id).longValue();
    }
}