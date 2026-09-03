package qa.dmitriy.base;

import org.junit.jupiter.api.BeforeAll;
import qa.dmitriy.client.PostsClient;

import static io.restassured.RestAssured.baseURI;

public abstract class BaseApiTest {

    protected final PostsClient postsClient = new PostsClient();

    @BeforeAll
    static void setUp() {
        baseURI = "https://jsonplaceholder.typicode.com";
    }
}