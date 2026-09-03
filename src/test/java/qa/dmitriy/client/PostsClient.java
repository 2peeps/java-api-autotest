package qa.dmitriy.client;

import io.restassured.response.Response;
import qa.dmitriy.model.PostResponse;

import static io.restassured.RestAssured.given;

public class PostsClient {

    public Response getPostByIdResponse(int postId) {
        return given()
                .when()
                .get("/posts/" + postId);
    }

    public PostResponse getPostById(int postId) {
        return getPostByIdResponse(postId)
                .as(PostResponse.class);
    }
}