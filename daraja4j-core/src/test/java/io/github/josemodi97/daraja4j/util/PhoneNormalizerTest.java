package io.github.josemodi97.daraja4j.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PhoneNormalizerTest {

    @Test
    void normalize_leadingZero_becomes254() {
        assertEquals("254712345678", PhoneNormalizer.normalize("0712345678"));
        assertEquals("254112345678", PhoneNormalizer.normalize("0112345678"));
    }

    @Test
    void normalize_plus254Prefix_stripsPlus() {
        assertEquals("254712345678", PhoneNormalizer.normalize("+254712345678"));
    }

    @Test
    void normalize_alreadyPrefixed_isUnchanged() {
        assertEquals("254712345678", PhoneNormalizer.normalize("254712345678"));
    }

    @Test
    void normalize_shortLocalNumber_getsCountryCode() {
        assertEquals("254712345678", PhoneNormalizer.normalize("712345678"));
        assertEquals("254112345678", PhoneNormalizer.normalize("112345678"));
    }

    @Test
    void normalize_null_returnsEmptyString() {
        assertEquals("", PhoneNormalizer.normalize(null));
    }

    @Test
    void normalize_stripsNonDigitFormatting() {
        assertEquals("254712345678", PhoneNormalizer.normalize("0712 345 678"));
        assertEquals("254712345678", PhoneNormalizer.normalize("0712-345-678"));
    }

    @Test
    void isValidMpesaPhone_acceptsSafaricomAndAirtelPrefixes() {
        assertTrue(PhoneNormalizer.isValidMpesaPhone("254712345678"));
        assertTrue(PhoneNormalizer.isValidMpesaPhone("254112345678"));
    }

    @Test
    void isValidMpesaPhone_rejectsWrongLengthOrPrefix() {
        assertFalse(PhoneNormalizer.isValidMpesaPhone("254212345678"));
        assertFalse(PhoneNormalizer.isValidMpesaPhone("25471234567"));
        assertFalse(PhoneNormalizer.isValidMpesaPhone(null));
    }
}
