package qa.dmitriy.api;

import qa.dmitriy.config.TestConfig;
import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import qa.dmitriy.base.BaseApiTest;
import qa.dmitriy.util.ExcelExportHelper;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class PayoutExportTest extends BaseApiTest {

    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String SUCCESS_STATUS_DISPLAY = "Успешно проведена";

    private static final String TEST_IIN = TestConfig.testIin();
    private static final String TEST_PHONE = TestConfig.testPhone();

    private static final String DATE_FROM = "2026-08-01";
    private static final String DATE_TO = "2026-08-31";

    private static final int ID_COLUMN = 0;
    private static final int IIN_COLUMN = 1;
    private static final int PHONE_COLUMN = 2;
    private static final int AMOUNT_COLUMN = 3;
    private static final int STATUS_COLUMN = 4;
    private static final int TRANSACTION_ID_COLUMN = 5;
    private static final int ERROR_COLUMN = 6;
    private static final int CREATED_AT_COLUMN = 7;
    private static final int PROCESSED_AT_COLUMN = 8;

    private static final List<String> EXPECTED_HEADERS = List.of(
            "ID",
            "ИИН",
            "Телефон",
            "Сумма",
            "Статус",
            "Transaction ID",
            "Ошибка",
            "Дата создания",
            "Дата обработки"
    );

    @Test
    void shouldExportPayoutsToExcel() {
        Response response = payoutClient.exportPayouts();

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertThat(ExcelExportHelper.getDataRowCount(workbook))
                    .isGreaterThan(0);

            for (int columnIndex = 0;
                 columnIndex < EXPECTED_HEADERS.size();
                 columnIndex++) {

                assertThat(ExcelExportHelper.getCellValue(
                        workbook,
                        0,
                        columnIndex
                )).isEqualTo(EXPECTED_HEADERS.get(columnIndex));
            }
        });
    }

    @Test
    void shouldExportPayoutsBySuccessStatus() {
        Response response = payoutClient.exportPayoutsByStatus(
                SUCCESS_STATUS
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        STATUS_COLUMN
                )).isEqualTo(SUCCESS_STATUS_DISPLAY);
            });
        });
    }

    @Test
    void shouldExportPayoutsByIin() {
        Response response = payoutClient.exportPayoutsByIin(TEST_IIN);

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        IIN_COLUMN
                )).isEqualTo(TEST_IIN);
            });
        });
    }

    @Test
    void shouldExportPayoutsByPhone() {
        Response response = payoutClient.exportPayoutsByPhone(TEST_PHONE);

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        PHONE_COLUMN
                )).isEqualTo(TEST_PHONE);
            });
        });
    }

    @Test
    void shouldExportPayoutsForDateRange() {
        Response response = payoutClient.exportPayoutsByDateRange(
                DATE_FROM,
                DATE_TO
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, this::assertHasDataRows);
    }

    @Test
    void shouldReturnEmptyExportForFutureDateRange() {
        Response response = payoutClient.exportPayoutsByDateRange(
                "2099-01-01",
                "2099-01-31"
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertThat(ExcelExportHelper.getDataRowCount(workbook))
                    .isEqualTo(0);
        });
    }

    @Test
    void shouldExportPayoutsCreatedOnBoundaryDate() {
        Response response = payoutClient.exportPayoutsByDateRange(
                "2026-08-04",
                "2026-08-04"
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                String createdAt = getCellValue(
                        workbook,
                        rowIndex,
                        CREATED_AT_COLUMN
                );

                assertThat(createdAt)
                        .isNotBlank()
                        .contains("04.08.2026");
            });
        });
    }

    @Test
    void shouldExportPayoutsByMultipleStatuses() {
        Response response = payoutClient.exportPayoutsByStatuses(
                SUCCESS_STATUS,
                "ERROR"
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        STATUS_COLUMN
                )).isEqualTo(SUCCESS_STATUS_DISPLAY);
            });
        });
    }

    @Test
    void shouldExportPayoutsByIinAndDateRange() {
        Response response = payoutClient.exportPayoutsByIinAndDateRange(
                TEST_IIN,
                DATE_FROM,
                DATE_TO
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        IIN_COLUMN
                )).isEqualTo(TEST_IIN);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        CREATED_AT_COLUMN
                )).isNotBlank();
            });
        });
    }

    @Test
    void shouldExportPayoutsByIinAndPhone() {
        Response response = payoutClient.exportPayoutsByIinAndPhone(
                TEST_IIN,
                TEST_PHONE
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        IIN_COLUMN
                )).isEqualTo(TEST_IIN);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        PHONE_COLUMN
                )).isEqualTo(TEST_PHONE);
            });
        });
    }

    @Test
    void shouldExportPayoutsByIinPhoneAndDateRange() {
        Response response = payoutClient.exportPayoutsByIinPhoneAndDateRange(
                TEST_IIN,
                TEST_PHONE,
                DATE_FROM,
                DATE_TO
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        IIN_COLUMN
                )).isEqualTo(TEST_IIN);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        PHONE_COLUMN
                )).isEqualTo(TEST_PHONE);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        CREATED_AT_COLUMN
                )).isNotBlank();
            });
        });
    }

    @Test
    void shouldExportPayoutsByIinPhoneAndStatus() {
        Response response = payoutClient.exportPayoutsByIinPhoneAndStatus(
                TEST_IIN,
                TEST_PHONE,
                SUCCESS_STATUS
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        IIN_COLUMN
                )).isEqualTo(TEST_IIN);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        PHONE_COLUMN
                )).isEqualTo(TEST_PHONE);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        STATUS_COLUMN
                )).isEqualTo(SUCCESS_STATUS_DISPLAY);
            });
        });
    }

    @Test
    void shouldExportPayoutsByAllFilters() {
        Response response = payoutClient.exportPayoutsByAllFilters(
                TEST_IIN,
                TEST_PHONE,
                DATE_FROM,
                DATE_TO,
                SUCCESS_STATUS
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        IIN_COLUMN
                )).isEqualTo(TEST_IIN);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        PHONE_COLUMN
                )).isEqualTo(TEST_PHONE);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        STATUS_COLUMN
                )).isEqualTo(SUCCESS_STATUS_DISPLAY);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        CREATED_AT_COLUMN
                )).isNotBlank();
            });
        });
    }

    @Test
    void shouldReturnEmptyExportForIncompatibleFilters() {
        Response response = payoutClient.exportPayoutsByIinAndPhone(
                TEST_IIN,
                "70000000000"
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertThat(ExcelExportHelper.getDataRowCount(workbook))
                    .isEqualTo(0);
        });
    }

    @Test
    void shouldExportSuccessfulPayoutsWithRequiredFields() {
        Response response = payoutClient.exportPayoutsByStatus(
                SUCCESS_STATUS
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertNotBlank(workbook, rowIndex, ID_COLUMN);
                assertNotBlank(workbook, rowIndex, IIN_COLUMN);
                assertNotBlank(workbook, rowIndex, PHONE_COLUMN);
                assertNotBlank(workbook, rowIndex, AMOUNT_COLUMN);

                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        STATUS_COLUMN
                )).isEqualTo(SUCCESS_STATUS_DISPLAY);

                assertNotBlank(
                        workbook,
                        rowIndex,
                        TRANSACTION_ID_COLUMN
                );

                assertNotBlank(
                        workbook,
                        rowIndex,
                        CREATED_AT_COLUMN
                );

                assertNotBlank(
                        workbook,
                        rowIndex,
                        PROCESSED_AT_COLUMN
                );
            });
        });
    }

    @Test
    void shouldHaveProcessedDateForSuccessfulPayouts() {
        Response response = payoutClient.exportPayoutsByStatus(
                SUCCESS_STATUS
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertNotBlank(
                        workbook,
                        rowIndex,
                        PROCESSED_AT_COLUMN
                );
            });
        });
    }

    @Test
    void shouldHaveEmptyErrorForSuccessfulPayouts() {
        Response response = payoutClient.exportPayoutsByStatus(
                SUCCESS_STATUS
        );

        assertSuccessfulResponse(response);

        withWorkbook(response, workbook -> {
            assertHasDataRows(workbook);

            forEachDataRow(workbook, rowIndex -> {
                assertThat(getCellValue(
                        workbook,
                        rowIndex,
                        ERROR_COLUMN
                )).isBlank();
            });
        });
    }

    private void assertSuccessfulResponse(Response response) {
        assertThat(response.statusCode())
                .isEqualTo(200);

        assertThat(response.asByteArray())
                .isNotEmpty();
    }

    private void assertHasDataRows(Workbook workbook) {
        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);
    }

    private void withWorkbook(
            Response response,
            Consumer<Workbook> assertions) {

        try (Workbook workbook = ExcelExportHelper.openWorkbook(
                response.asByteArray())) {

            assertions.accept(workbook);

        } catch (Exception e) {
            throw new AssertionError(
                    "Не удалось обработать XLSX",
                    e
            );
        }
    }

    private void forEachDataRow(
            Workbook workbook,
            Consumer<Integer> assertion) {

        int lastRowIndex = workbook
                .getSheetAt(0)
                .getLastRowNum();

        for (int rowIndex = 1;
             rowIndex <= lastRowIndex;
             rowIndex++) {

            assertion.accept(rowIndex);
        }
    }

    private String getCellValue(
            Workbook workbook,
            int rowIndex,
            int columnIndex) {

        return ExcelExportHelper.getCellValue(
                workbook,
                rowIndex,
                columnIndex
        );
    }

    private void assertNotBlank(
            Workbook workbook,
            int rowIndex,
            int columnIndex) {

        assertThat(getCellValue(
                workbook,
                rowIndex,
                columnIndex
        )).isNotBlank();
    }
}