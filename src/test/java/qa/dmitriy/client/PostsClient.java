package qa.dmitriy.client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PostsClient {

    public Response getPostById(int postId) {
        return given()
                .when()
                .get("/posts/" + postId);
    }
}