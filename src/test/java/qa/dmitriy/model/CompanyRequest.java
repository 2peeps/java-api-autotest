package qa.dmitriy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompanyRequest(
        String name,
        String bin,

        @JsonProperty("rekassa_income_type_legal_entity")
        String rekassaIncomeTypeLegalEntity,

        @JsonProperty("rekassa_payment_type_cashless")
        String rekassaPaymentTypeCashless,

        @JsonProperty("rekassa_quantity")
        Integer rekassaQuantity,

        @JsonProperty("rekassa_platform_id")
        String rekassaPlatformId,

        @JsonProperty("rekassa_buyer_iin")
        String rekassaBuyerIin,

        @JsonProperty("rekassa_buyer_name")
        String rekassaBuyerName,

        @JsonProperty("rekassa_buyer_iik")
        String rekassaBuyerIik,

        @JsonProperty("rekassa_buyer_kbe")
        String rekassaBuyerKbe,

        @JsonProperty("rekassa_buyer_bik")
        String rekassaBuyerBik,

        @JsonProperty("rekassa_buyer_bank_name")
        String rekassaBuyerBankName,

        @JsonProperty("rekassa_self_employed_max_price")
        Double rekassaSelfEmployedMaxPrice,

        @JsonProperty("rekassa_revert_reason")
        String rekassaRevertReason,

        @JsonProperty("avr_payer_position")
        String avrPayerPosition,

        @JsonProperty("avr_payer_fio_short")
        String avrPayerFioShort,

        @JsonProperty("avr_contract_number")
        String avrContractNumber,

        @JsonProperty("avr_contract_title")
        String avrContractTitle,

        @JsonProperty("avr_sms_text")
        String avrSmsText,

        @JsonProperty("avr_sms_text_finishing")
        String avrSmsTextFinishing
) {
}