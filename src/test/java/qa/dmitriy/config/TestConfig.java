package qa.dmitriy.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {

    private static final Properties PROPERTIES = loadProperties();

    private TestConfig() {
    }

    public static String baseUrl() {
        String baseUrl = System.getProperty("base.url");

        if (baseUrl != null && !baseUrl.isBlank()) {
            return baseUrl;
        }

        return PROPERTIES.getProperty("base.url");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = TestConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "application.properties not found"
                );
            }

            properties.load(inputStream);
            return properties;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load application.properties",
                    e
            );
        }
    }
}