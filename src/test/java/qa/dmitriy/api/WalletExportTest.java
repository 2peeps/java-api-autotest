package qa.dmitriy.api;

import qa.dmitriy.config.TestConfig;
import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.DataFormatter;
import qa.dmitriy.util.ExcelExportHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import qa.dmitriy.base.BaseApiTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WalletExportTest extends BaseApiTest {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");


@Test
void shouldExportWalletsToExcel() {
    Response response = walletClient.exportWallets();

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        var sheet = workbook.getSheetAt(0);

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        Row headerRow = sheet.getRow(0);
        DataFormatter formatter = new DataFormatter();

        List<String> expectedHeaders = List.of(
                "ID кошелька",
                "Номер кошелька",
                "Дата открытия",
                "ФИО",
                "ИИН",
                "Статус",
                "Статус идентификации",
                "Ссылка на идентификацию",
                "Дата идентификации",
                "Ошибка"
        );

        List<String> actualHeaders = new ArrayList<>();

        for (int columnIndex = 0;
             columnIndex < expectedHeaders.size();
             columnIndex++) {

            actualHeaders.add(
                    formatter.formatCellValue(
                            headerRow.getCell(columnIndex)
                    )
            );
        }

        assertThat(actualHeaders)
                .containsExactlyElementsOf(expectedHeaders);

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}

@Test
    void shouldExportWalletsForDateRange() {
    Response response = walletClient.exportWallets(
            "2026-08-03",
            "2026-08-04"
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String openingDate = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    2
            );

            LocalDate actualDate = LocalDate.parse(
                    openingDate,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
            );

            assertThat(actualDate)
                    .isBetween(
                            LocalDate.of(2026, 8, 3),
                            LocalDate.of(2026, 8, 4)
                    );
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}

@Test
void shouldExportWalletsByIin() {
    String iin = TestConfig.testIin();

    Response response = walletClient.exportWalletsByIin(iin);

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String actualIin = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    4
            );

            assertThat(actualIin)
                    .isEqualTo(iin);
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}

@Test
void shouldExportWalletsByPhone() {
    String phone = TestConfig.testPhone();

    Response response = walletClient.exportWalletsByPhone(phone);

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String walletNumber = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    1
            );

            assertThat(walletNumber)
                    .isNotBlank();
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldExportWalletsByWalletStatus() {
    String walletStatus = "CREATED";

    Response response = walletClient.exportWalletsByWalletStatus(
            walletStatus
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String actualStatus = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    5
            );

            assertThat(actualStatus)
                    .isEqualTo("Создан");
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldExportWalletsByIdentificationStatus() {
    String identificationStatus = "IDENTIFIED";

    Response response = walletClient.exportWalletsByIdentificationStatus(
            identificationStatus
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String actualStatus = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    6
            );

            assertThat(actualStatus)
                    .isIn(
                            "Fully identified",
                            "Полностью идентифицирован"
                    );
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldExportWalletsByMultipleWalletStatuses() {
    String firstStatus = "CREATED";
    String secondStatus = "ERROR";

    Response response = walletClient.exportWalletsByWalletStatuses(
            firstStatus,
            secondStatus
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String actualStatus = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    5
            );

            assertThat(actualStatus)
                    .isIn(
                            "Создан",
                            "Ошибка"
                    );
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldExportWalletsByMultipleIdentificationStatuses() {
    String firstStatus = "IDENTIFIED";
    String secondStatus = "PENDING";

    Response response = walletClient.exportWalletsByIdentificationStatuses(
            firstStatus,
            secondStatus
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String actualStatus = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    6
            );

            assertThat(actualStatus)
                    .isIn(
                            "Полностью идентифицирован",
                            "В процессе идентификации",
                            "Fully identified",
                            "Pending"
                    );
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldExportWalletsByCombinedFilters() {
    String iin = TestConfig.testIin();
    String dateFrom = "2026-08-03";
    String dateTo = "2026-08-04";
    String walletStatus = "CREATED";
    String identificationStatus = "IDENTIFIED";

    Response response = walletClient.exportWalletsByFilters(
            iin,
            dateFrom,
            dateTo,
            walletStatus,
            identificationStatus
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String actualIin = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    4
            );

            String actualOpeningDate = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    2
            );

            String actualWalletStatus = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    5
            );

            String actualIdentificationStatus = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    6
            );

            LocalDate actualDate = LocalDate.parse(
                    actualOpeningDate,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
            );

            assertThat(actualIin)
                    .isEqualTo(iin);

            assertThat(actualDate)
                    .isBetween(
                            LocalDate.of(2026, 8, 3),
                            LocalDate.of(2026, 8, 4)
                    );

            assertThat(actualWalletStatus)
                    .isEqualTo("Создан");

            assertThat(actualIdentificationStatus)
                    .isIn(
                            "Полностью идентифицирован",
                            "Fully identified"
                    );
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldReturnEmptyExportForIncompatibleFilters() {
    String iin = TestConfig.testIin();
    String dateFrom = "2026-08-03";
    String dateTo = "2026-08-04";
    String walletStatus = "CREATED";
    String identificationStatus = "NOT_IDENTIFIED";

    Response response = walletClient.exportWalletsByFilters(
            iin,
            dateFrom,
            dateTo,
            walletStatus,
            identificationStatus
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isEqualTo(0);

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldHaveEmptyIdentificationDateForNotIdentifiedStatus() {
    String identificationStatus = "NOT_IDENTIFIED";

    Response response = walletClient.exportWalletsByIdentificationStatus(
            identificationStatus
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String actualIdentificationDate = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    8
            );

            assertThat(actualIdentificationDate)
                    .isBlank();
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldIncludeWalletsCreatedOnBoundaryDate() {
    String dateFrom = "2026-08-04";
    String dateTo = "2026-08-04";

    Response response = walletClient.exportWallets(
            dateFrom,
            dateTo
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String openingDate = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    2
            );

            assertThat(openingDate)
                    .isEqualTo("04.08.2026");
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldReturnEmptyExportForFutureDateRange() {
    Response response = walletClient.exportWallets(
            "2099-01-01",
            "2099-01-31"
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isEqualTo(0);

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
@Test
void shouldExportWalletsWithCorrectLocalOpeningDate() {
    Response response = walletClient.exportWallets(
            "2026-08-04",
            "2026-08-04"
    );

    assertThat(response.statusCode())
            .isEqualTo(200);

    assertThat(response.asByteArray())
            .isNotEmpty();

    try (Workbook workbook = ExcelExportHelper.openWorkbook(
            response.asByteArray())) {

        assertThat(ExcelExportHelper.getDataRowCount(workbook))
                .isGreaterThan(0);

        for (int rowIndex = 1;
             rowIndex <= workbook.getSheetAt(0).getLastRowNum();
             rowIndex++) {

            String openingDate = ExcelExportHelper.getCellValue(
                    workbook,
                    rowIndex,
                    2
            );

            assertThat(openingDate)
                    .isEqualTo("04.08.2026");
        }

    } catch (IOException e) {
        throw new AssertionError(
                "Не удалось обработать XLSX",
                e
        );
    }
}
}