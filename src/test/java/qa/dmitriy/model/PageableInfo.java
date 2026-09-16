package qa.dmitriy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PageableInfo(

        @JsonProperty("page_number")
        int pageNumber,

        @JsonProperty("page_size")
        int pageSize,

        SortInfo sort,
        int offset,
        boolean paged,
        boolean unpaged
) {
}