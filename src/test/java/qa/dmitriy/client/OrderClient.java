package qa.dmitriy.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import qa.dmitriy.auth.UserTokenProvider;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.config.TestConfig;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDERS_ENDPOINT =
            "/api/self-employed/orders";

    private static final String ORDER_BY_ID_ENDPOINT =
            "/api/self-employed/orders/{id}";

    private final UserTokenProvider userTokenProvider =
            new UserTokenProvider();

    public Response getOrders(
            int page,
            int size,
            String sort
    ) {
        RequestSpecification request =
        orderRequest()
                .queryParam("page", page)
                .queryParam("size", size);

    if (sort != null && !sort.isBlank()) {
    request.queryParam("sort", sort);
    }

        return request
            .when()
            .get(ORDERS_ENDPOINT);
        }

    public Response getOrdersWithoutAuthentication(
            int page,
            int size,
            String sort
    ) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .when()
                .get(ORDERS_ENDPOINT);
    }

    public Response getOrderById(long id) {
        return orderRequest()
                .pathParam("id", id)
                .when()
                .get(ORDER_BY_ID_ENDPOINT);
    }

    public Response getOrderByIdWithoutAuthentication(long id) {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .pathParam("id", id)
                .when()
                .get(ORDER_BY_ID_ENDPOINT);
    }

    private RequestSpecification orderRequest() {
        return given()
                .spec(RestAssuredConfig.defaultSpecification())
                .baseUri(TestConfig.walletBaseUrl())
                .auth()
                .oauth2(
                        userTokenProvider.getAccessToken(
                                TestConfig.rewardPaymentTestIin(),
                                TestConfig.rewardPaymentTestPhone()
                        )
                );
    }
}