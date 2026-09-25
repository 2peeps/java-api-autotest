package qa.dmitriy.auth;

import io.restassured.response.Response;
import qa.dmitriy.client.UserAuthClient;

public class UserTokenProvider {

    private final UserAuthClient userAuthClient =
            new UserAuthClient();

    public String getAccessToken() {
        Response initResponse =
                userAuthClient.initAuthentication();

        if (initResponse.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to initialize user authentication. HTTP status: "
                            + initResponse.statusCode()
            );
        }

        String sessionId =
                initResponse.jsonPath()
                        .getString("session_id");

        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalStateException(
                    "Session ID is missing in user authentication response"
            );
        }

        Response verifyResponse =
                userAuthClient.verifyAuthentication(sessionId);

        if (verifyResponse.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to verify user authentication. HTTP status: "
                            + verifyResponse.statusCode()
            );
        }

        String accessToken =
                verifyResponse.jsonPath()
                        .getString("access_token");

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException(
                    "User access token is missing in authentication response"
            );
        }

        return accessToken;
    }

    public String getAccessToken(
            String iin,
            String phone
    ) {
        Response initResponse =
                userAuthClient.initAuthentication(
                        iin,
                        phone
                );

        if (initResponse.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to initialize user authentication. HTTP status: "
                            + initResponse.statusCode()
            );
        }

        String sessionId =
                initResponse.jsonPath()
                        .getString("session_id");

        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalStateException(
                    "Session ID is missing in user authentication response"
            );
        }

        Response verifyResponse =
                userAuthClient.verifyAuthentication(sessionId);

        if (verifyResponse.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to verify user authentication. HTTP status: "
                            + verifyResponse.statusCode()
            );
        }

        String accessToken =
                verifyResponse.jsonPath()
                        .getString("access_token");

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException(
                    "User access token is missing in authentication response"
            );
        }

        return accessToken;
    }
}