package io.github.josemodi97.daraja4j.model;

import io.github.josemodi97.daraja4j.internal.JsonReader;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Parses the JSON body Daraja posts to a C2B {@code validationUrl} or
 * {@code confirmationUrl} once a customer pays a registered shortcode.
 * Confirmation's field set is a superset of Validation's, so one class
 * serves both.
 *
 * <p><strong>This is a structural parser, not a signature verifier</strong> -
 * see the project README's Security section for how to establish trust in a
 * callback before acting on it.
 */
public final class C2bConfirmation {

    private final String transactionType;
    private final String transId;
    private final String transTime;
    private final BigDecimal transAmount;
    private final String businessShortCode;
    private final String billRefNumber;
    private final String invoiceNumber;
    private final BigDecimal orgAccountBalance;
    private final String thirdPartyTransId;
    private final String msisdn;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String rawResponse;

    private C2bConfirmation(String transactionType, String transId, String transTime, BigDecimal transAmount,
                             String businessShortCode, String billRefNumber, String invoiceNumber,
                             BigDecimal orgAccountBalance, String thirdPartyTransId, String msisdn,
                             String firstName, String middleName, String lastName, String rawResponse) {
        this.transactionType = transactionType;
        this.transId = transId;
        this.transTime = transTime;
        this.transAmount = transAmount;
        this.businessShortCode = businessShortCode;
        this.billRefNumber = billRefNumber;
        this.invoiceNumber = invoiceNumber;
        this.orgAccountBalance = orgAccountBalance;
        this.thirdPartyTransId = thirdPartyTransId;
        this.msisdn = msisdn;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.rawResponse = rawResponse;
    }

    public static C2bConfirmation parse(String rawJsonBody) {
        Map<String, Object> root = JsonReader.parseObject(rawJsonBody);
        return new C2bConfirmation(
                JsonReader.getString(root, "TransactionType"),
                JsonReader.getString(root, "TransID"),
                JsonReader.getString(root, "TransTime"),
                toBigDecimal(JsonReader.getString(root, "TransAmount")),
                JsonReader.getString(root, "BusinessShortCode"),
                JsonReader.getString(root, "BillRefNumber"),
                JsonReader.getString(root, "InvoiceNumber"),
                toBigDecimal(JsonReader.getString(root, "OrgAccountBalance")),
                JsonReader.getString(root, "ThirdPartyTransID"),
                JsonReader.getString(root, "MSISDN"),
                JsonReader.getString(root, "FirstName"),
                JsonReader.getString(root, "MiddleName"),
                JsonReader.getString(root, "LastName"),
                rawJsonBody);
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransId() {
        return transId;
    }

    public String getTransTime() {
        return transTime;
    }

    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public String getBusinessShortCode() {
        return businessShortCode;
    }

    public String getBillRefNumber() {
        return billRefNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public BigDecimal getOrgAccountBalance() {
        return orgAccountBalance;
    }

    public String getThirdPartyTransId() {
        return thirdPartyTransId;
    }

    /** The payer's MSISDN. On a Validation callback this may arrive partially masked, e.g. {@code "2547*****126"}. */
    public String getMsisdn() {
        return msisdn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    /** The raw JSON Daraja posted, for anything not exposed by a typed getter. */
    public String getRawResponse() {
        return rawResponse;
    }

    private static BigDecimal toBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
