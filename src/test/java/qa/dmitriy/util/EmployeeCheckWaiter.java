package qa.dmitriy.util;

import io.restassured.response.Response;
import qa.dmitriy.client.EmployeeCheckClient;
import qa.dmitriy.model.EmployeeCheckStatusResponse;

public class EmployeeCheckWaiter {

    private static final int POLLING_INTERVAL_MS = 5000;
    private static final int TIMEOUT_MS = 60000;

    private final EmployeeCheckClient employeeCheckClient;

    public EmployeeCheckWaiter(
            EmployeeCheckClient employeeCheckClient) {
        this.employeeCheckClient = employeeCheckClient;
    }

    public EmployeeCheckStatusResponse waitForCompletion(
            String jobId) {

        long deadline =
                System.currentTimeMillis() + TIMEOUT_MS;

        while (System.currentTimeMillis() < deadline) {

            Response response =
                    employeeCheckClient.getStatus(jobId);

            if (response.statusCode() != 200) {
                throw new AssertionError(
                        "Failed to get employee check status. "
                                + "HTTP status: "
                                + response.statusCode()
                                + ", jobId: "
                                + jobId
                );
            }

            EmployeeCheckStatusResponse result =
                    response.as(EmployeeCheckStatusResponse.class);

            if ("COMPLETED".equals(result.overallStatus())) {
                return result;
            }

            sleep();
        }

        throw new AssertionError(
                "Employee check job did not reach COMPLETED "
                        + "within " + TIMEOUT_MS + " ms. "
                        + "jobId: " + jobId
        );
    }

    private void sleep() {
        try {
            Thread.sleep(POLLING_INTERVAL_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new AssertionError(
                    "Polling was interrupted",
                    e
            );
        }
    }
}