package io.github.josemodi97.daraja4j.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class StkCredentialsGeneratorTest {

    /**
     * Golden vector taken verbatim from Safaricom's own Daraja documentation
     * for the M-Pesa Express Simulate (STK Push) endpoint: shortcode 174379,
     * the publicly documented sandbox passkey, and the exact expected
     * Base64-encoded Password for a known timestamp. Decoding the
     * documented Password confirms shortcode + passkey + timestamp is the
     * concatenation order, independently of the production code under test.
     */
    @Test
    void password_matchesSafaricomDocumentedSample() {
        String shortcode = "174379";
        String passkey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
        String timestamp = "20210628092408";
        String expected = "MTc0Mzc5YmZiMjc5ZjlhYTliZGJjZjE1OGU5N2RkNzFhNDY3Y2QyZTBjODkzMDU5YjEwZjc4ZTZiNzJhZGExZWQyYzkxOTIwMjEwNjI4MDkyNDA4";

        assertEquals(expected, StkCredentialsGenerator.password(shortcode, passkey, timestamp));
    }

    @Test
    void timestamp_matchesYyyyMMddHHmmssFormat() {
        String timestamp = StkCredentialsGenerator.timestamp(ZoneId.of("Africa/Nairobi"));
        assertTrue(Pattern.matches("\\d{14}", timestamp), "Expected 14 digits (yyyyMMddHHmmss), got: " + timestamp);
    }

    @Test
    void timestamp_usesNairobiZoneRegardlessOfDefaultOverload() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime nairobi = LocalDateTime.parse(StkCredentialsGenerator.timestamp(ZoneId.of("Africa/Nairobi")), format);
        LocalDateTime defaultOverload = LocalDateTime.parse(StkCredentialsGenerator.timestamp(), format);
        // Both calls happen within the same test tick; at worst they differ
        // by a second or two if the clock ticked over between calls.
        Duration diff = Duration.between(nairobi, defaultOverload).abs();
        assertTrue(diff.getSeconds() <= 2, "Expected timestamp() to use Africa/Nairobi, diff was: " + diff);
    }
}
