package qa.dmitriy.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import qa.dmitriy.client.EmployeeCheckClient;
import qa.dmitriy.model.CreateJobResponse;
import qa.dmitriy.model.EmployeeCheckRequest;
import qa.dmitriy.model.EmployeeCheckStatusResponse;
import qa.dmitriy.util.EmployeeCheckWaiter;
import qa.dmitriy.model.EmployeeCheckItem;
import qa.dmitriy.model.EmployeeCheckJobResponse;
import qa.dmitriy.config.TestConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import qa.dmitriy.model.EmployeeCheckSummary;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeCheckApiTest {

    private final EmployeeCheckClient employeeCheckClient =
            new EmployeeCheckClient();

    private final EmployeeCheckWaiter employeeCheckWaiter =
            new EmployeeCheckWaiter(employeeCheckClient);


    @Test
    void shouldCreateEmployeeCheckJob() {

        EmployeeCheckRequest request = new EmployeeCheckRequest(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                "autotest-" + UUID.randomUUID(),
                List.of("880324301100")
        );

        Response response =
                employeeCheckClient.submitCheck(request);

        assertThat(response.statusCode())
                .isIn(200, 202);

        CreateJobResponse result =
                response.as(CreateJobResponse.class);

        assertThat(result.jobId())
                .isNotNull();

        assertThat(result.status())
                .isEqualTo("PENDING");

        assertThat(result.requestedCount())
                .isEqualTo(1);

        assertThat(result.uniqueCount())
                .isEqualTo(1);

        assertThat(result.createdAt())
                .isNotBlank();
    }

    @Test
    void shouldGetEmployeeCheckJobStatus() {

    EmployeeCheckRequest request = new EmployeeCheckRequest(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "autotest-status-" + UUID.randomUUID(),
            List.of("880324301100")
    );

    Response createResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(createResponse.statusCode())
            .isIn(200, 202);

    String jobId = createResponse.jsonPath()
            .getString("jobId");

    assertThat(jobId)
            .isNotBlank();

    Response statusResponse =
            employeeCheckClient.getStatus(jobId);

    assertThat(statusResponse.statusCode())
            .isEqualTo(200);

    EmployeeCheckStatusResponse result =
            statusResponse.as(EmployeeCheckStatusResponse.class);

    assertThat(result.jobId())
            .isEqualTo(UUID.fromString(jobId));

    assertThat(result.overallStatus())
            .isIn("PENDING", "PARTIAL", "COMPLETED");

    assertThat(result.summary())
            .isNotNull();

    assertThat(result.summary().total())
            .isEqualTo(1);

    assertThat(result.summary().done())
            .isGreaterThanOrEqualTo(0);

    assertThat(result.summary().failed())
            .isGreaterThanOrEqualTo(0);

    assertThat(result.summary().doneWithWarnings())
            .isGreaterThanOrEqualTo(0);

    assertThat(result.summary().inProgress())
            .isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldWaitUntilEmployeeCheckJobCompleted() {

    EmployeeCheckRequest request = new EmployeeCheckRequest(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "autotest-completion-" + UUID.randomUUID(),
            List.of("880324301100")
    );

    Response createResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(createResponse.statusCode())
            .isIn(200, 202);

    String jobId = createResponse.jsonPath()
            .getString("jobId");

    assertThat(jobId)
            .isNotBlank();

    EmployeeCheckStatusResponse result =
            employeeCheckWaiter.waitForCompletion(jobId);

    assertThat(result.jobId().toString())
            .isEqualTo(jobId);

    assertThat(result.overallStatus())
            .isEqualTo("COMPLETED");

    assertThat(result.summary())
            .isNotNull();


    assertThat(result.summary().total())
        .isEqualTo(1);

    assertThat(
             result.summary().done()
                + result.summary().doneWithWarnings()
    )
            .isEqualTo(1);

    assertThat(result.summary().failed())
        .isEqualTo(0);

    assertThat(result.summary().inProgress())
        .isEqualTo(0);
    }

    @Test
    void shouldGetEmployeeCheckJobResult() {

    EmployeeCheckRequest request = new EmployeeCheckRequest(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "autotest-result-" + UUID.randomUUID(),
            List.of("880324301100")
    );

    Response createResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(createResponse.statusCode())
            .isIn(200, 202);

    String jobId = createResponse.jsonPath()
            .getString("jobId");

    assertThat(jobId)
            .isNotBlank();

    EmployeeCheckStatusResponse status =
            employeeCheckWaiter.waitForCompletion(jobId);

    assertThat(status.overallStatus())
            .isEqualTo("COMPLETED");

    Response resultResponse =
            employeeCheckClient.getResult(jobId);

    assertThat(resultResponse.statusCode())
            .isEqualTo(200);

    EmployeeCheckJobResponse result =
            resultResponse.as(EmployeeCheckJobResponse.class);

    assertThat(result.jobId().toString())
            .isEqualTo(jobId);

    assertThat(result.overallStatus())
            .isEqualTo("COMPLETED");

    assertThat(result.summary())
            .isNotNull();

    assertThat(result.summary().total())
            .isEqualTo(1);

    assertThat(result.items())
            .hasSize(1);

    EmployeeCheckItem item =
            result.items().get(0);

    assertThat(item.iin())
            .isEqualTo("880324301100");

    assertThat(item.status())
            .isNotBlank();

    assertThat(item.data())
            .isNotNull();
    }

    @Test
    void shouldReturnSameJobForSameIdempotencyKeyAndSameIins() {

    String idempotencyKey =
            "autotest-idempotency-same-" + UUID.randomUUID();

    EmployeeCheckRequest request = new EmployeeCheckRequest(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            idempotencyKey,
            List.of("880324301100")
    );

    Response firstResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(firstResponse.statusCode())
            .isIn(200, 202);

    String firstJobId =
            firstResponse.jsonPath().getString("jobId");

    assertThat(firstJobId)
            .isNotBlank();

    Response secondResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(secondResponse.statusCode())
            .isIn(200, 202);

    String secondJobId =
            secondResponse.jsonPath().getString("jobId");

    assertThat(secondJobId)
            .isEqualTo(firstJobId);
    }

    @Test
    void shouldReturnConflictForSameIdempotencyKeyAndDifferentIins() {

    String idempotencyKey =
            "autotest-idempotency-conflict-" + UUID.randomUUID();

    EmployeeCheckRequest firstRequest =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    idempotencyKey,
                    List.of("880324301100")
            );

    Response firstResponse =
            employeeCheckClient.submitCheck(firstRequest);

    assertThat(firstResponse.statusCode())
            .isIn(200, 202);

    assertThat(firstResponse.jsonPath()
            .getString("jobId"))
            .isNotBlank();

    EmployeeCheckRequest secondRequest =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    idempotencyKey,
                    List.of(
                            "880324301100",
                            "880324301101"
                    )
            );

    Response secondResponse =
            employeeCheckClient.submitCheck(secondRequest);

    assertThat(secondResponse.statusCode())
            .isEqualTo(409);
    }

    @Test
    void shouldDownloadEmployeeCheckArchive() {

    EmployeeCheckRequest request = new EmployeeCheckRequest(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "autotest-archive-" + UUID.randomUUID(),
            List.of("880324301100")
    );

    Response createResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(createResponse.statusCode())
            .isIn(200, 202);

    String jobId = createResponse.jsonPath()
            .getString("jobId");

    assertThat(jobId)
            .isNotBlank();

    EmployeeCheckStatusResponse status =
            employeeCheckWaiter.waitForCompletion(jobId);

    assertThat(status.overallStatus())
            .isEqualTo("COMPLETED");

    Response archiveResponse =
            employeeCheckClient.getArchive(jobId);

    assertThat(archiveResponse.statusCode())
            .isEqualTo(200);

    byte[] archiveBytes = archiveResponse.asByteArray();

    assertThat(archiveResponse.contentType())
            .isEqualTo("application/octet-stream");

    assertThat(archiveBytes)
            .isNotEmpty();

    assertThat(archiveBytes.length)
            .isGreaterThan(4);

    assertThat(archiveBytes[0])
            .isEqualTo((byte) 'P');

    assertThat(archiveBytes[1])
            .isEqualTo((byte) 'K');

    String contentDisposition =
            archiveResponse.header("Content-Disposition");

    assertThat(contentDisposition)
            .isNotBlank();

    assertThat(contentDisposition)
            .contains("employee-check-" + jobId);
    }

    @Test
    void shouldReturnNotFoundForNonExistentEmployeeCheckJob() {

        String nonExistentJobId =
                "11111111-1111-1111-1111-111111111111";

        Response response =
                employeeCheckClient.getResult(nonExistentJobId);

        assertThat(response.statusCode())
                .isEqualTo(404);
    }

    @Test
    void shouldReturnNotFoundForNonExistentEmployeeCheckJobStatus() {

    String nonExistentJobId =
            "11111111-1111-1111-1111-111111111111";

    Response response =
            employeeCheckClient.getStatus(nonExistentJobId);

    assertThat(response.statusCode())
            .isEqualTo(404);
    }

    @Test
    void shouldRejectEmployeeCheckWithoutAuthentication() {

        EmployeeCheckRequest request =
                new EmployeeCheckRequest(
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        UUID.randomUUID().toString(),
                        List.of(TestConfig.testIin())
                );
        Response response =
                employeeCheckClient
                        .submitCheckWithoutAuthentication(request);

        System.out.println(
                    "HTTP status without authentication: "
                            + response.statusCode()
        );

        assertThat(response.statusCode())
                .isIn(401, 403);
    }

    @Test
    void shouldRejectEmployeeCheckResultWithoutAuthentication() {

    String jobId =
            "11111111-1111-1111-1111-111111111111";

    Response response =
            employeeCheckClient
                    .getResultWithoutAuthentication(jobId);

    assertThat(response.statusCode())
            .isIn(401, 403);
    }

    @Test
    void shouldRejectEmployeeCheckStatusWithoutAuthentication() {

    String jobId =
            "11111111-1111-1111-1111-111111111111";

    Response response =
            employeeCheckClient
                    .getStatusWithoutAuthentication(jobId);

    assertThat(response.statusCode())
            .isIn(401, 403);
    }

    @Test
    void shouldRejectEmployeeCheckArchiveWithoutAuthentication() {

    String jobId =
            "11111111-1111-1111-1111-111111111111";

    Response response =
            employeeCheckClient
                    .getArchiveWithoutAuthentication(jobId);

    assertThat(response.statusCode())
            .isIn(401, 403);
    }
    @Test
    void shouldRejectEmployeeCheckWithEmptyIins() {

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    "autotest-empty-iins-" + UUID.randomUUID(),
                    List.of()
            );

    Response response =
            employeeCheckClient.submitCheck(request);

    assertThat(response.statusCode())
            .isIn(400);
    }

    @Test
    void shouldRejectEmployeeCheckWithoutNullIins() {

        EmployeeCheckRequest request =
                new EmployeeCheckRequest(
                        UUID.fromString(
                                "00000000-0000-0000-0000-000000000000"
                        ),
                        "autotest-null-iins-" + UUID.randomUUID(),
                        null
                );

        Response response =
                employeeCheckClient.submitCheck(request);

        assertThat(response.statusCode())
                .isEqualTo(400);
    }

    @Test
    void shouldRejectEmployeeCheckWithNullIins() {

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    "autotest-null-iins-" + UUID.randomUUID(),
                    null
            );

    Response response =
            employeeCheckClient.submitCheck(request);

    assertThat(response.statusCode())
            .isEqualTo(400);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectEmployeeCheckWithInvalidIdempotencyKey(
        String idempotencyKey) {

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    idempotencyKey,
                    List.of("880324301100")
            );

    Response response =
            employeeCheckClient.submitCheck(request);

    assertThat(response.statusCode())
            .isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123",
        "ABCDEFGHIJKL",
        "12345678901",
        "1234567890123"
    })
    void shouldRejectEmployeeCheckWithInvalidIin(String iin) {

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    "autotest-invalid-iin-" + UUID.randomUUID(),
                    List.of(iin)
            );

    Response response =
            employeeCheckClient.submitCheck(request);

    assertThat(response.statusCode())
            .isEqualTo(400);
    }

    @Test
    void shouldRejectEmployeeCheckWithNullCompanyId() {

        EmployeeCheckRequest request =
                new EmployeeCheckRequest(
                        null,
                        "autotest-null-company-" + UUID.randomUUID(),
                        List.of("880324301100")
                );

    Response response =
            employeeCheckClient.submitCheck(request);

    assertThat(response.statusCode())
            .isEqualTo(400);
    }

    @Test
    void shouldHandleDuplicateIins() {

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    "autotest-duplicate-iin-" + UUID.randomUUID(),
                    List.of(
                            "880324301100",
                            "880324301100"
                    )
            );

    Response response =
            employeeCheckClient.submitCheck(request);

    assertThat(response.statusCode())
            .isIn(200, 202);

    CreateJobResponse job =
            response.as(CreateJobResponse.class);

    assertThat(job.jobId())
            .isNotNull();

    assertThat(job.requestedCount())
            .isEqualTo(2);

    assertThat(job.uniqueCount())
            .isEqualTo(1);
    }

    @Test
    void shouldReturnConsistentStatusBetweenLightweightAndFullResult() {

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    "autotest-consistency-" + UUID.randomUUID(),
                    List.of("880324301100")
            );

    Response createResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(createResponse.statusCode())
            .isIn(200, 202);

    CreateJobResponse createdJob =
            createResponse.as(CreateJobResponse.class);

    assertThat(createdJob.jobId())
            .isNotNull();

    EmployeeCheckStatusResponse status =
            new EmployeeCheckWaiter(employeeCheckClient)
                    .waitForCompletion(
                            createdJob.jobId().toString()
                    );

    Response fullResultResponse =
            employeeCheckClient.getResult(
                    createdJob.jobId().toString()
            );

    assertThat(fullResultResponse.statusCode())
            .isEqualTo(200);

    EmployeeCheckJobResponse fullResult =
            fullResultResponse.as(
                    EmployeeCheckJobResponse.class
            );

    assertThat(fullResult.jobId())
            .isEqualTo(status.jobId());

    assertThat(fullResult.overallStatus())
            .isEqualTo(status.overallStatus());

    assertThat(fullResult.summary().total())
            .isEqualTo(status.summary().total());

    assertThat(fullResult.summary().done())
            .isEqualTo(status.summary().done());

    assertThat(fullResult.summary().doneWithWarnings())
            .isEqualTo(status.summary().doneWithWarnings());

    assertThat(fullResult.summary().inProgress())
            .isEqualTo(status.summary().inProgress());

    assertThat(fullResult.summary().failed())
            .isEqualTo(status.summary().failed());

    assertThat(fullResult.items())
            .hasSize(status.summary().total());
    }

    @Test
    void shouldKeepEmployeeCheckSummaryCountsConsistent() {

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    "autotest-summary-" + UUID.randomUUID(),
                    List.of("880324301100")
            );

    Response createResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(createResponse.statusCode())
            .isIn(200, 202);

    CreateJobResponse createdJob =
            createResponse.as(CreateJobResponse.class);

    assertThat(createdJob.jobId())
            .isNotNull();

    EmployeeCheckStatusResponse status =
            new EmployeeCheckWaiter(employeeCheckClient)
                    .waitForCompletion(
                            createdJob.jobId().toString()
                    );

    EmployeeCheckSummary summary =
            status.summary();

    assertThat(summary)
            .isNotNull();

    int processedCount =
            summary.done()
                    + summary.doneWithWarnings()
                    + summary.inProgress()
                    + summary.failed();

    assertThat(processedCount)
            .isEqualTo(summary.total());

    assertThat(summary.inProgress())
            .isEqualTo(0);

    assertThat(summary.total())
            .isGreaterThan(0);
    }

    @Test
    void shouldRejectInvalidJobIdFormat() {

    String invalidJobId = "not-a-uuid";

    Response statusResponse =
            employeeCheckClient.getStatus(invalidJobId);

    assertThat(statusResponse.statusCode())
            .isEqualTo(400);

    Response resultResponse =
            employeeCheckClient.getResult(invalidJobId);

    assertThat(resultResponse.statusCode())
            .isEqualTo(400);

    Response archiveResponse =
            employeeCheckClient.getArchive(invalidJobId);

    assertThat(archiveResponse.statusCode())
            .isEqualTo(400);
    }

    @Test
    void shouldReturnCompletedEmployeeCheckItemWithExpectedIin() {

    String iin = "880324301100";

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    "autotest-result-item-" + UUID.randomUUID(),
                    List.of(iin)
            );

    Response createResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(createResponse.statusCode())
            .isIn(200, 202);

    CreateJobResponse createdJob =
            createResponse.as(CreateJobResponse.class);

    assertThat(createdJob.jobId())
            .isNotNull();

    new EmployeeCheckWaiter(employeeCheckClient)
            .waitForCompletion(
                    createdJob.jobId().toString()
            );

    Response resultResponse =
            employeeCheckClient.getResult(
                    createdJob.jobId().toString()
            );

    assertThat(resultResponse.statusCode())
            .isEqualTo(200);

    EmployeeCheckJobResponse result =
            resultResponse.as(
                    EmployeeCheckJobResponse.class
            );

    assertThat(result.items())
            .hasSize(1);

    EmployeeCheckItem item =
            result.items().get(0);

    assertThat(item.iin())
            .isEqualTo(iin);

    assertThat(item.status())
            .isIn("DONE", "DONE_WITH_WARNINGS");

    assertThat(item.processedAt())
            .isNotBlank();

    }

    @Test
    void shouldReturnEmployeeCheckDataForCompletedItem() {

    String iin = "880324301100";

    EmployeeCheckRequest request =
            new EmployeeCheckRequest(
                    UUID.fromString(
                            "00000000-0000-0000-0000-000000000000"
                    ),
                    "autotest-result-data-" + UUID.randomUUID(),
                    List.of(iin)
            );

    Response createResponse =
            employeeCheckClient.submitCheck(request);

    assertThat(createResponse.statusCode())
            .isIn(200, 202);

    CreateJobResponse createdJob =
            createResponse.as(CreateJobResponse.class);

    assertThat(createdJob.jobId())
            .isNotNull();

    new EmployeeCheckWaiter(employeeCheckClient)
            .waitForCompletion(
                    createdJob.jobId().toString()
            );

    Response resultResponse =
            employeeCheckClient.getResult(
                    createdJob.jobId().toString()
            );

    assertThat(resultResponse.statusCode())
            .isEqualTo(200);

    EmployeeCheckJobResponse result =
            resultResponse.as(
                    EmployeeCheckJobResponse.class
            );

    EmployeeCheckItem item =
            result.items().get(0);

    assertThat(item.status())
            .isIn("DONE", "DONE_WITH_WARNINGS");

    assertThat(item.data())
            .isNotNull();

    assertThat(item.data().fullName())
            .isNotBlank();

    assertThat(item.data().taxFormByKgd())
            .isNotBlank();

    if ("DONE".equals(item.status())) {
    assertThat(item.data().taxFormByRekassa())
            .isNotBlank();
    }

    if ("DONE_WITH_WARNINGS".equals(item.status())) {
    assertThat(item.data().taxFormByRekassa())
            .isNull();

    assertThat(item.errors())
            .anySatisfy(error -> {
                assertThat(error.source())
                        .isEqualTo("REKASSA_SERVICE");

                assertThat(error.message())
                        .isEqualTo("service unavailable");
            });
    }

    assertThat(item.data().portalRegistrationDate())
            .isNotBlank();

    assertThat(item.data().avrDebt())
            .isNotNull();
    }
}