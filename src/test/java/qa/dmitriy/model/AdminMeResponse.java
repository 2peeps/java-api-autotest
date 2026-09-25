package qa.dmitriy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record AdminMeResponse(
        String id,
        String name,
        String email,
        boolean admin,

        @JsonProperty("module_access")
        Map<String, String> moduleAccess,

        List<Company> companies
) {
    public record Company(
            long id,
            String bin,
            String name
    ) {
    }
}