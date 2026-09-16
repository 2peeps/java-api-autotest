package qa.dmitriy.model;

public record EmployeeCheckResult(
        String fullName,
        String taxFormByKgd,
        String taxFormByRekassa,
        String portalRegistrationDate,
        Boolean avrDebt,
        String liquidationStartDate
) {
}