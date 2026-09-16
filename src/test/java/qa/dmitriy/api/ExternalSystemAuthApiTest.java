package qa.dmitriy.api;

import org.junit.jupiter.api.Test;
import qa.dmitriy.auth.ExternalSystemTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalSystemAuthApiTest {

    private final ExternalSystemTokenProvider tokenProvider =
            new ExternalSystemTokenProvider();

    @Test
    void shouldGetExternalSystemAccessToken() {

        String accessToken = tokenProvider.getAccessToken();

        assertThat(accessToken)
                .isNotBlank();
    }
}