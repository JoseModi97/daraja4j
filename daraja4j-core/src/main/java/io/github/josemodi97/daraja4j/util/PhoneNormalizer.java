package io.github.josemodi97.daraja4j.util;

import java.util.regex.Pattern;

/**
 * Normalizes Kenyan MSISDNs to the {@code 2547XXXXXXXX} / {@code 2541XXXXXXXX}
 * format Daraja requires for {@code PartyA}/{@code PhoneNumber} fields, and
 * validates the result.
 */
public final class PhoneNormalizer {

    private static final Pattern NON_DIGITS = Pattern.compile("\\D+");
    private static final Pattern SHORT_LOCAL_NUMBER = Pattern.compile("^[17]\\d{8}$");
    private static final Pattern VALID_MPESA_NUMBER = Pattern.compile("^254[17]\\d{8}$");

    private PhoneNormalizer() {
    }

    /**
     * Normalizes Kenyan phone numbers to {@code 254XXXXXXXXX} format.
     * Handles formats such as:
     * <ul>
     *   <li>{@code 0712345678} &rarr; {@code 254712345678}</li>
     *   <li>{@code 0112345678} &rarr; {@code 254112345678}</li>
     *   <li>{@code +254712345678} &rarr; {@code 254712345678}</li>
     *   <li>{@code 712345678} &rarr; {@code 254712345678}</li>
     *   <li>{@code 112345678} &rarr; {@code 254112345678}</li>
     * </ul>
     *
     * @param phone raw phone number in any of the above shapes; may be {@code null}
     * @return the normalized MSISDN, or an empty string if {@code phone} is {@code null}
     */
    public static String normalize(String phone) {
        if (phone == null) {
            return "";
        }

        String digits = NON_DIGITS.matcher(phone).replaceAll("");

        if (digits.startsWith("254")) {
            return digits;
        }

        if (digits.startsWith("0")) {
            return "254" + digits.substring(1);
        }

        if (SHORT_LOCAL_NUMBER.matcher(digits).matches()) {
            return "254" + digits;
        }

        return digits;
    }

    /**
     * @param normalizedPhone a phone number already run through {@link #normalize(String)}
     * @return {@code true} if it matches a valid Safaricom/Airtel MSISDN
     *         ({@code 2547XXXXXXXX} or {@code 2541XXXXXXXX})
     */
    public static boolean isValidMpesaPhone(String normalizedPhone) {
        return normalizedPhone != null && VALID_MPESA_NUMBER.matcher(normalizedPhone).matches();
    }
}
