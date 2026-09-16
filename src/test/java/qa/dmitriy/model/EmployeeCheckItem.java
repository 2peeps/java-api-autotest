package qa.dmitriy.model;

import java.util.List;

public record EmployeeCheckItem(
        String iin,
        String status,
        EmployeeCheckResult data,
        List<EmployeeCheckError> errors,
        String processedAt
) {
}