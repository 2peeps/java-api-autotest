package qa.dmitriy.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {

    private static final Properties PROPERTIES = loadProperties();

    private TestConfig() {
    }

    public static String postsBaseUrl() {
        return getRequiredProperty("posts.base.url");
    }

    public static String walletBaseUrl() {
        return getRequiredProperty("wallet.base.url");
    }

    public static String testIin() {
        return getRequiredProperty("test.iin");
    }

    public static String testPhone() {
        return getRequiredProperty("test.phone");
    }

    public static String authUrl() {
        return getRequiredProperty("auth.url");
    }

    public static String authClientId() {
        return getRequiredProperty("auth.client-id");
    }

    public static String authUsername() {
        return getRequiredEnvironmentVariable("KEYCLOAK_USERNAME");
    }

    public static String authPassword() {
        return getRequiredEnvironmentVariable("KEYCLOAK_PASSWORD");
    }

    public static String paymentStatementsBaseUrl() {
        return getRequiredProperty("payment-statements.base.url");
    }

    public static String authClientSecret() {
        return getRequiredEnvironmentVariable("KEYCLOAK_CLIENT_SECRET");
    }

    private static String getRequiredProperty(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Property is not configured: " + key
            );
        }

        return value;
    }

    private static String getRequiredEnvironmentVariable(String key) {
        String value = System.getenv(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Environment variable is not configured: " + key
            );
        }

        return value;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        loadInto(properties, "application.properties");
        loadInto(properties, "application-local.properties");

        return properties;
    }

    private static void loadInto(Properties properties, String fileName) {
        try (InputStream inputStream = TestConfig.class
                .getClassLoader()
                .getResourceAsStream(fileName)) {

            if (inputStream == null) {
                return;
            }

            properties.load(inputStream);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load " + fileName,
                    e
            );
        }
    }
}