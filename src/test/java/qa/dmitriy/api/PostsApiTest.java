package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import qa.dmitriy.base.BaseApiTest;
import qa.dmitriy.model.PostResponse;

import static org.assertj.core.api.Assertions.assertThat;

class PostsApiTest extends BaseApiTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shouldGetPostById(int postId) {
        PostResponse post = postsClient.getPostById(postId);

        assertThat(post.id())
                .isEqualTo(postId);

        assertThat(post.userId())
                .isPositive();

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