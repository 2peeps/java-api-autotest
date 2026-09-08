package qa.dmitriy.auth;
import qa.dmitriy.client.AuthClient;
import java.time.Instant;
public class TokenProvider {

    private final AuthClient authClient = new AuthClient();

    private String accessToken;
    private Instant expiresAt;

    public synchronized String getAccessToken() {
        if (accessToken == null || isExpired()) {
            refreshToken();
        }

        return accessToken;
    }

    private void refreshToken() {
        var response = authClient.getToken();

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to obtain access token. HTTP status: "
                            + response.statusCode()
            );
        }

        accessToken = response.jsonPath()
                .getString("access_token");

        Integer expiresIn = response.jsonPath()
                .getInt("expires_in");

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException(
                    "Access token is missing in Keycloak response"
            );
        }

        if (expiresIn == null || expiresIn <= 0) {
            throw new IllegalStateException(
                    "Token expiration time is missing or invalid"
            );
        }

        expiresAt = Instant.now()
                .plusSeconds(expiresIn);
    }

    private boolean isExpired() {
        return expiresAt == null
                || Instant.now().isAfter(expiresAt.minusSeconds(30));
    }
}