package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class PostsApiTest {

    @Test
    void shouldGetPostById() {
        Response response = given()
                .when()
                .get(TestConfig.baseUrl() + "/posts/1");

        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.jsonPath().getInt("id"))
                .isEqualTo(1);

        assertThat(response.jsonPath().getInt("userId"))
                .isEqualTo(1);
    }
}