package qa.dmitriy.auth;

import qa.dmitriy.client.AuthClient;

public class AdminTokenProvider {

    private final AuthClient authClient = new AuthClient();

    public String getAccessToken(String email, String password){
        return authClient
                .getToken(email, password)
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }
}