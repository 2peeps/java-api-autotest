package qa.dmitriy.base;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import qa.dmitriy.client.PostsClient;
import qa.dmitriy.config.RestAssuredConfig;

public abstract class BaseApiTest {

    protected final PostsClient postsClient = new PostsClient();

    @BeforeAll
    static void setUp() {
        RestAssured.requestSpecification =
                RestAssuredConfig.defaultSpecification();
    }
}