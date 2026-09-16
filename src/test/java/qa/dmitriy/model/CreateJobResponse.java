package qa.dmitriy.model;

import java.util.UUID;

public record CreateJobResponse(
        UUID jobId,
        String status,
        Integer requestedCount,
        Integer uniqueCount,
        String createdAt
) {
}