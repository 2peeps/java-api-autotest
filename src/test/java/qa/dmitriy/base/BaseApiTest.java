package qa.dmitriy.base;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import qa.dmitriy.client.PayoutClient;
import qa.dmitriy.client.PostsClient;
import qa.dmitriy.client.WalletClient;
import qa.dmitriy.config.RestAssuredConfig;
import qa.dmitriy.client.MockPaymentStatementsClient;

public abstract class BaseApiTest {

    protected final PostsClient postsClient = new PostsClient();
    protected final WalletClient walletClient = new WalletClient();
    protected final PayoutClient payoutClient = new PayoutClient();
    protected final MockPaymentStatementsClient mockPaymentStatementsClient = new MockPaymentStatementsClient();

    @BeforeAll
    static void setUp() {
        RestAssured.requestSpecification =
                RestAssuredConfig.defaultSpecification();
    }
}