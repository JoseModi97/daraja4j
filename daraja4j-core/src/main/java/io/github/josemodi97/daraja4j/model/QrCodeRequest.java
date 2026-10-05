package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;
import java.math.BigDecimal;

/**
 * Generates an M-Pesa Dynamic QR code: scannable by the M-Pesa customer app
 * for a specific amount, merchant name, and transaction reference.
 */
public final class QrCodeRequest {

    private final String merchantName;
    private final String refNo;
    private final BigDecimal amount;
    private final QrTransactionType trxCode;
    private final String cpi;
    private final int size;

    private QrCodeRequest(Builder builder) {
        this.merchantName = builder.merchantName;
        this.refNo = builder.refNo;
        this.amount = builder.amount;
        this.trxCode = builder.trxCode != null ? builder.trxCode : QrTransactionType.PAYBILL;
        this.cpi = builder.cpi;
        this.size = builder.size > 0 ? builder.size : 300;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("QrCodeRequest", "merchantName", merchantName, "merchantName (name of business or merchant)");
        requireNonBlank("QrCodeRequest", "refNo", refNo, "refNo (order ID or invoice reference)");
        requireNonBlank("QrCodeRequest", "amount", amount == null ? null : amount.toPlainString(), "amount (total amount to pay)");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedCpi = firstNonBlank(cpi, config.getDefaultShortcode());
        requireResolved("QrCodeRequest", "cpi", resolvedCpi,
                "QrCodeRequest.builder().cpi(...) or Daraja4jConfig.builder().defaultShortcode(...)");

        return new JsonWriter()
                .field("MerchantName", merchantName)
                .field("RefNo", refNo)
                .field("Amount", amount.intValue())
                .field("TrxCode", trxCode.getWireValue())
                .field("CPI", resolvedCpi)
                .field("Size", String.valueOf(size))
                .build();
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getRefNo() {
        return refNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public QrTransactionType getTrxCode() {
        return trxCode;
    }

    public String getCpi() {
        return cpi;
    }

    public int getSize() {
        return size;
    }

    public static final class Builder {
        private String merchantName;
        private String refNo;
        private BigDecimal amount;
        private QrTransactionType trxCode;
        private String cpi;
        private int size = 300;

        private Builder() {
        }

        public Builder merchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public Builder refNo(String refNo) {
            this.refNo = refNo;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amount(long amount) {
            this.amount = BigDecimal.valueOf(amount);
            return this;
        }

        public Builder amount(double amount) {
            this.amount = BigDecimal.valueOf(amount);
            return this;
        }

        public Builder trxCode(QrTransactionType trxCode) {
            this.trxCode = trxCode;
            return this;
        }

        public Builder cpi(String cpi) {
            this.cpi = cpi;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public QrCodeRequest build() {
            return new QrCodeRequest(this);
        }
    }
}
