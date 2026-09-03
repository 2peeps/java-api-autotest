package qa.dmitriy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FirstTest {

    @Test
    void shouldVerifyBasicAssertion() {
        String actual = "Automation";

        assertThat(actual)
                .isEqualTo("Automation");
    }
}