package qa.dmitriy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Payout(

        Long id,
        String iin,
        String phone,
        Double amount,
        String status,

        @JsonProperty("processed_at")
        String processedAt,

        @JsonProperty("transaction_id")
        UUID transactionId,

        @JsonProperty("external_id")
        String externalId,

        @JsonProperty("error_message")
        String errorMessage,

        @JsonProperty("created_at")
        String createdAt,

        @JsonProperty("updated_at")
        String updatedAt
) {
}