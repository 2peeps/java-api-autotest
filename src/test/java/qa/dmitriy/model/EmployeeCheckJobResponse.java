package qa.dmitriy.model;

import java.util.List;
import java.util.UUID;

public record EmployeeCheckJobResponse(
        UUID jobId,
        String overallStatus,
        EmployeeCheckSummary summary,
        List<EmployeeCheckItem> items
) {
}