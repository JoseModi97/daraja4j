package io.github.josemodi97.daraja4j.model;

import static io.github.josemodi97.daraja4j.model.RequestValidation.firstNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireNonBlank;
import static io.github.josemodi97.daraja4j.model.RequestValidation.requireResolved;

import io.github.josemodi97.daraja4j.Daraja4jConfig;
import io.github.josemodi97.daraja4j.internal.JsonWriter;

/**
 * Retrieves C2B transactions for {@code shortcode} within a period, once
 * registered via {@link PullTransactionsRegisterRequest}. {@code startDate}
 * and {@code endDate} use Daraja's own format, e.g.
 * {@code "2019-07-31 20:35:21"}.
 *
 * <p>Daraja documents this endpoint as {@code GET} with a JSON request body
 * (non-standard, but supported by {@code Daraja4jClient}).
 */
public final class PullTransactionsQueryRequest {

    private final String shortcode;
    private final String startDate;
    private final String endDate;
    private final int offsetValue;

    private PullTransactionsQueryRequest(Builder builder) {
        this.shortcode = builder.shortcode;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.offsetValue = builder.offsetValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        requireNonBlank("PullTransactionsQueryRequest", "startDate", startDate, "startDate (e.g. '2019-07-31 20:35:21')");
        requireNonBlank("PullTransactionsQueryRequest", "endDate", endDate, "endDate (e.g. '2019-07-31 22:35:21')");
    }

    public String toJson(Daraja4jConfig config) {
        validate();

        String resolvedShortcode = firstNonBlank(shortcode, config.getDefaultShortcode());
        requireResolved("PullTransactionsQueryRequest", "shortcode", resolvedShortcode,
                "PullTransactionsQueryRequest.builder().shortcode(...) or Daraja4jConfig.builder().defaultShortcode(...)");

        return new JsonWriter()
                .field("ShortCode", resolvedShortcode)
                .field("StartDate", startDate)
                .field("EndDate", endDate)
                .field("OffSetValue", String.valueOf(offsetValue))
                .build();
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getOffsetValue() {
        return offsetValue;
    }

    /** Builder for {@link PullTransactionsQueryRequest}. */
    public static final class Builder {
        private String shortcode;
        private String startDate;
        private String endDate;
        private int offsetValue;

        private Builder() {
        }

        public Builder shortcode(String shortcode) {
            this.shortcode = shortcode;
            return this;
        }

        public Builder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        /** Which row to start from, for paging through results; defaults to {@code 0}. */
        public Builder offsetValue(int offsetValue) {
            this.offsetValue = offsetValue;
            return this;
        }

        public PullTransactionsQueryRequest build() {
            return new PullTransactionsQueryRequest(this);
        }
    }
}
