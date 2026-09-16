package qa.dmitriy.model;

public record CompanyRequest(
        String name,
        String bin,
        String rekassaIncomeTypeLegalEntity,
        String rekassaPaymentTypeCashless,
        Integer rekassaQuantity,
        String rekassaPlatformId,
        String rekassaBuyerIin,
        String rekassaBuyerName,
        String rekassaBuyerIik,
        String rekassaBuyerKbe,
        String rekassaBuyerBik,
        String rekassaBuyerBankName,
        Double rekassaSelfEmployedMaxPrice,
        String rekassaRevertReason,
        String avrPayerPosition,
        String avrPayerFioShort,
        String avrContractNumber,
        String avrContractTitle,
        String avrSmsText,
        String avrSmsTextFinishing
) {
}