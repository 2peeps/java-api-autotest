package qa.dmitriy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RefundDetail(

        Long id,
        String iin,
        String phone,
        Double amount,

        @JsonProperty("refunded_amount")
        Double refundedAmount,

        String status,

        @JsonProperty("error_message")
        String errorMessage,

        @JsonProperty("created_by")
        String createdBy,

        List<RefundAllocation> allocations,

        @JsonProperty("created_at")
        String createdAt,

        @JsonProperty("updated_at")
        String updatedAt
) {
}