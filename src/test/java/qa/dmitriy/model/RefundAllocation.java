package qa.dmitriy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record RefundAllocation(

        Long id,

        @JsonProperty("payout_id")
        Long payoutId,

        @JsonProperty("external_id")
        String externalId,

        Double amount,

        @JsonProperty("transaction_id")
        UUID transactionId,

        String status,

        @JsonProperty("error_message")
        String errorMessage,

        @JsonProperty("created_at")
        String createdAt
) {
}