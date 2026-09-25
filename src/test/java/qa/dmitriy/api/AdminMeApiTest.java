package qa.dmitriy.api;

import org.junit.jupiter.api.Test;
import qa.dmitriy.auth.AdminTokenProvider;
import qa.dmitriy.base.BaseApiTest;
import qa.dmitriy.client.AdminMeClient;
import qa.dmitriy.config.TestConfig;
import qa.dmitriy.model.AdminMeResponse;

import static org.assertj.core.api.Assertions.assertThat;

class AdminMeApiTest extends BaseApiTest {

    private final AdminMeClient adminMeClient = new AdminMeClient();
    private final AdminTokenProvider adminTokenProvider = new AdminTokenProvider();

    @Test
    void shouldReturnCurrentAdminInfoForAdminWithWalletRole() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminArConsultEmail(),
                TestConfig.adminArConsultPassword()
        );

        var response = adminMeClient.getCurrentAdminInfo(accessToken);

        assertThat(response.statusCode())
                .isEqualTo(200);

        AdminMeResponse admin = response.as(AdminMeResponse.class);

        assertThat(admin.id())
                .isNotBlank();

        assertThat(admin.name())
                .isEqualTo("too_ar_consalt_user1");

        assertThat(admin.email())
                .isEqualTo("too_ar_consalt_user1@gmail.com");

        assertThat(admin.admin())
                .isTrue();

        assertThat(admin.moduleAccess())
                .containsEntry("WALLET", "FULL");

        assertThat(admin.companies())
                .anySatisfy(company -> {
                    assertThat(company.id())
                            .isEqualTo(1L);

                    assertThat(company.bin())
                            .isEqualTo("220540023170");

                    assertThat(company.name())
                            .isEqualTo("ТОО АР Консалт");
                });
    }

    @Test
    void shouldReturnUnauthorizedWithoutAuthentication() {
        var response = adminMeClient.getCurrentAdminInfoWithoutAuthentication();

        assertThat(response.statusCode())
                .isEqualTo(401);
    }

    @Test
    void shouldReturnAdminInfoWithoutWalletAccess() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminSmartStaffEmail(),
                TestConfig.adminSmartStaffPassword()
        );

    var response = adminMeClient.getCurrentAdminInfo(accessToken);

        assertThat(response.statusCode())
                .isEqualTo(200);

    AdminMeResponse admin = response.as(AdminMeResponse.class);

        assertThat(admin.admin())
                .isTrue();

        assertThat(admin.moduleAccess())
                .isEmpty();

    assertThat(admin.companies())
            .anySatisfy(company -> {
                assertThat(company.id())
                        .isEqualTo(2L);

                assertThat(company.bin())
                        .isEqualTo("260740007430");

                assertThat(company.name())
                        .isEqualTo("Autotest Updated Company");
            });
    }

    @Test
    void shouldReturnWalletAccessForAdminWithoutAdminRole() {
    String accessToken = adminTokenProvider.getAccessToken(
            TestConfig.adminWalletOnlyEmail(),
            TestConfig.adminWalletOnlyPassword()
    );

    var response = adminMeClient.getCurrentAdminInfo(accessToken);

    assertThat(response.statusCode())
            .isEqualTo(200);

    AdminMeResponse admin = response.as(AdminMeResponse.class);

    assertThat(admin.admin())
            .isFalse();

    assertThat(admin.moduleAccess())
            .containsEntry("WALLET", "FULL");

    assertThat(admin.companies())
            .anySatisfy(company -> {
                assertThat(company.id())
                        .isEqualTo(1L);

                assertThat(company.bin())
                        .isEqualTo("220540023170");

                assertThat(company.name())
                        .isEqualTo("ТОО АР Консалт");
            });
    }

    @Test
    void shouldReturnForbiddenForAdminWithoutRoles() {
        String accessToken = adminTokenProvider.getAccessToken(
                TestConfig.adminNoRolesEmail(),
                TestConfig.adminNoRolesPassword()
        );

        var response = adminMeClient.getCurrentAdminInfo(accessToken);

        assertThat(response.statusCode())
                .isEqualTo(403);
    }
}