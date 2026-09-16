package qa.dmitriy.model;

public record EmployeeCheckError(
        String source,
        String message
) {
}