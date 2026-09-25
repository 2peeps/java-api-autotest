package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.WalletClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WalletRegistryExportApiTest {

    private final WalletClient walletClient =
            new WalletClient();

    @Test
    void shouldExportWalletRegistry() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.exportWalletRegistry(registryId);

        response.then()
                .statusCode(200);

        assertThat(response.asByteArray())
                .isNotEmpty();
    }

    @Test
    void shouldReturnExcelContentType() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.exportWalletRegistry(registryId);

        response.then()
                .statusCode(200);

        assertThat(response.getContentType())
                .contains("application/octet-stream");
    }

    @Test
    void shouldReturnAttachmentHeader() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.exportWalletRegistry(registryId);

        response.then()
                .statusCode(200);

        String contentDisposition =
                response.getHeader("Content-Disposition");

        assertThat(contentDisposition)
                .isNotNull()
                .contains("attachment")
                .contains("wallets_registry_" + registryId + ".xlsx");
    }

    @Test
    void shouldReturnNonEmptyExportFile() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.exportWalletRegistry(registryId);

        response.then()
                .statusCode(200);

        byte[] file =
                response.asByteArray();

        assertThat(file)
                .isNotNull()
                .hasSizeGreaterThan(100);
    }

    @Test
    void shouldReturnNotFoundForUnknownRegistry() {
        Response response =
                walletClient.exportWalletRegistry(
                        Long.MAX_VALUE
                );

        assertThat(response.statusCode())
                .isIn(404, 500);
    }

    @Test
    void shouldHandleZeroRegistryId() {
        Response response =
                walletClient.exportWalletRegistry(0);

        assertThat(response.statusCode())
                .isIn(404, 500);
    }

    @Test
    void shouldHandleNegativeRegistryId() {
        Response response =
                walletClient.exportWalletRegistry(-1);

        assertThat(response.statusCode())
                .isIn(404, 500);
    }

    @Test
    void shouldRejectRequestWithoutAuthentication() {
        long registryId = getExistingRegistryId();

        Response response =
                walletClient.exportWalletRegistryWithoutAuthentication(
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