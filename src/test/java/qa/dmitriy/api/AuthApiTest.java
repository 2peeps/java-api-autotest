package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.AuthClient;

import static org.assertj.core.api.Assertions.assertThat;

class AuthApiTest {

    private final AuthClient authClient = new AuthClient();

    @Test
    void shouldGetAccessToken() {
        Response response = authClient.getToken();

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getString("access_token"))
                .isNotBlank();
    }
}