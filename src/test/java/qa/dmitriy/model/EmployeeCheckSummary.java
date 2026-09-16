package qa.dmitriy.model;

public record EmployeeCheckSummary(
        Integer total,
        Integer done,
        Integer doneWithWarnings,
        Integer inProgress,
        Integer failed
) {
}