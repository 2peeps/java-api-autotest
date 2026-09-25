package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.WalletClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PayoutRegistryDetailsApiTest {

    private final WalletClient walletClient =
            new WalletClient();

    @Test
    void shouldGetPayoutRegistryDetails() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getPayoutRegistryDetails(registryId);

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getLong("id"))
                .isEqualTo(registryId);
    }

    @Test
    void shouldReturnValidRegistryDetailsStructure() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getPayoutRegistryDetails(registryId);

        response.then()
                .statusCode(200);

        assertThat(response.jsonPath().getLong("id"))
                .isEqualTo(registryId);

        assertThat(response.jsonPath().getString("filename"))
                .isNotNull();

        assertThat(response.jsonPath().getString("status"))
                .isNotNull();

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
    void shouldReturnItemsCollection() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.getPayoutRegistryDetails(registryId);

        response.then()
                .statusCode(200);

        List<Map<String, Object>> items =
                response.jsonPath().getList("items");

        assertThat(items)
                .isNotNull();
    }

    @Test
    void shouldReturnNotFoundForUnknownRegistry() {
        Response response =
                walletClient.getPayoutRegistryDetails(
                        999999999L
                );

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldHandleZeroRegistryId() {
        Response response =
                walletClient.getPayoutRegistryDetails(0L);

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldHandleNegativeRegistryId() {
        Response response =
                walletClient.getPayoutRegistryDetails(-1L);

        response.then()
                .statusCode(404);
    }

    @Test
    void shouldRejectRequestWithoutAuthentication() {
        Response response =
                walletClient.getPayoutRegistryDetailsWithoutAuthentication(
                        1L
                );

        response.then()
                .statusCode(401);
    }

    private long getExistingRegistryId() {
        Response response =
                walletClient.getPayoutRegistries(
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