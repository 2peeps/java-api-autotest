package qa.dmitriy.model;

import java.util.List;
import java.util.UUID;

public record EmployeeCheckRequest(
        UUID companyId,
        String idempotencyKey,
        List<String> iins
) {
}