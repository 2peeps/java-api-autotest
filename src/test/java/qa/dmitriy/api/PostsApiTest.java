package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.base.BaseApiTest;
import qa.dmitriy.client.PostsClient;

import static org.assertj.core.api.Assertions.assertThat;

class PostsApiTest extends BaseApiTest {

    private final PostsClient postsClient = new PostsClient();

    @Test
    void shouldGetPostById() {
        Response response = postsClient.getPostById(1);

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getInt("id"))
                .isEqualTo(1);

        assertThat(response.jsonPath().getInt("userId"))
                .isEqualTo(1);
    }
}