package qa.dmitriy.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public final class RestAssuredConfig {

    private RestAssuredConfig() {
    }

    public static RequestSpecification defaultSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.baseUrl())
                .setContentType("application/json")
                .build();
    }
}