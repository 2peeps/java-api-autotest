package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.base.BaseApiTest;
import qa.dmitriy.model.PostResponse;

import static org.assertj.core.api.Assertions.assertThat;

class PostsApiTest extends BaseApiTest {

    @Test
void shouldGetPostById() {
    PostResponse post = postsClient.getPostById(1);

    assertThat(post.id())
            .isEqualTo(1);

    assertThat(post.userId())
            .isEqualTo(1);

    assertThat(post.title())
            .isNotBlank();

    assertThat(post.body())
            .isNotBlank();
}

    @Test
    void shouldReturnNotFoundForNonExistingPost() {
        Response response = postsClient.getPostByIdResponse(999);

        assertThat(response.statusCode())
                .isEqualTo(404);
    }
}