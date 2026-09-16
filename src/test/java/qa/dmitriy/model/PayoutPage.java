package qa.dmitriy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PayoutPage(

        List<Payout> content,

        PageableInfo pageable,

        @JsonProperty("total_pages")
        int totalPages,

        @JsonProperty("total_elements")
        long totalElements,

        int size,
        int number,

        @JsonProperty("number_of_elements")
        int numberOfElements,

        boolean first,
        boolean last,
        boolean empty,
        SortInfo sort
) {
}