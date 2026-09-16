package qa.dmitriy.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public final class RestAssuredConfig {

    private RestAssuredConfig() {
    }

    public static RequestSpecification defaultSpecification() {
        return new RequestSpecBuilder()
                .setContentType("application/json")
                .addFilter(new AllureRestAssured())
                .build();
    }
}