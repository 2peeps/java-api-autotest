package qa.dmitriy.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public final class RestAssuredConfig {

    private RestAssuredConfig() {
    }

    public static RequestSpecification defaultSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("https://jsonplaceholder.typicode.com")
                .setContentType("application/json")
                .log(LogDetail.ALL)
                .build();
    }
}