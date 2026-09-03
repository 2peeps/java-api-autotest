package qa.dmitriy.base;

import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.baseURI;

public abstract class BaseApiTest {

    @BeforeAll
    static void setUp() {
        baseURI = "https://jsonplaceholder.typicode.com";
    }
}