package io.github.josemodi97.daraja4j.util;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Generates the {@code Timestamp} and {@code Password} fields Daraja's STK
 * Push (and Ratiba) endpoints require: {@code Password} is
 * {@code Base64(Shortcode + Passkey + Timestamp)}, and {@code Timestamp} must
 * be fresh (Daraja rejects requests with a stale timestamp).
 *
 * <p>{@link #timestamp()} deliberately uses the {@code Africa/Nairobi} zone
 * rather than the JVM's default time zone: a server hosted in UTC (or any
 * other zone) calling {@code LocalDateTime.now()} would silently produce a
 * timestamp hours off from Nairobi time and have every STK push rejected as
 * stale.
 */
public final class StkCredentialsGenerator {

    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private StkCredentialsGenerator() {
    }

    /** The current time in Nairobi, formatted as {@code yyyyMMddHHmmss}. */
    public static String timestamp() {
        return timestamp(NAIROBI);
    }

    /** The current time in the given zone, formatted as {@code yyyyMMddHHmmss}. Exposed mainly for testing. */
    public static String timestamp(ZoneId zone) {
        return ZonedDateTime.now(zone).format(TIMESTAMP_FORMAT);
    }

    /** {@code Base64(shortcode + passkey + timestamp)}. */
    public static String password(String shortcode, String passkey, String timestamp) {
        String raw = nullToEmpty(shortcode) + nullToEmpty(passkey) + nullToEmpty(timestamp);
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
