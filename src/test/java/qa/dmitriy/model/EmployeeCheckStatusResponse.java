package qa.dmitriy.model;

import java.util.UUID;

public record EmployeeCheckStatusResponse(
        UUID jobId,
        String overallStatus,
        EmployeeCheckSummary summary
) {
}