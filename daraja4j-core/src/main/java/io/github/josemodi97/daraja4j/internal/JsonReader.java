package io.github.josemodi97.daraja4j.internal;

import io.github.josemodi97.daraja4j.exception.Daraja4jApiException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal, zero-dependency recursive-descent JSON parser. Daraja response
 * bodies are small, so this parses the whole string in memory rather than
 * streaming. Not part of the public API.
 *
 * <p>Parses into plain JDK types: {@link Map}, {@link List}, {@link String},
 * {@link Long}/{@link Double}, {@link Boolean}, or {@code null}.
 */
public final class JsonReader {

    private final String json;
    private int pos;

    private JsonReader(String json) {
        this.json = json;
    }

    /** Parses an arbitrary JSON value. */
    public static Object parse(String json) {
        JsonReader reader = new JsonReader(json);
        Object value = reader.readValue();
        return value;
    }

    /**
     * Parses a JSON object, wrapping any failure (malformed JSON, or a
     * top-level value that isn't an object) in a {@link Daraja4jApiException}
     * &mdash; used when parsing a Daraja response, where a parse failure means
     * Daraja returned something unexpected.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object value;
        try {
            value = parse(json == null ? "" : json);
        } catch (RuntimeException e) {
            throw new Daraja4jApiException(
                    "Daraja returned a response that could not be parsed as JSON: " + e.getMessage(),
                    0, null, null, null);
        }
        if (!(value instanceof Map)) {
            throw new Daraja4jApiException(
                    "Expected a JSON object in Daraja's response but got: "
                            + (value == null ? "null" : value.getClass().getSimpleName()),
                    0, null, null, null);
        }
        return (Map<String, Object>) value;
    }

    public static String getString(Map<String, Object> object, String key) {
        if (object == null) {
            return null;
        }
        Object value = object.get(key);
        return value == null ? null : String.valueOf(value);
    }

    public static Integer getInt(Map<String, Object> object, String key) {
        if (object == null) {
            return null;
        }
        Object value = object.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double getDouble(Map<String, Object> object, String key) {
        if (object == null) {
            return null;
        }
        Object value = object.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getObject(Map<String, Object> object, String key) {
        if (object == null) {
            return null;
        }
        Object value = object.get(key);
        return value instanceof Map ? (Map<String, Object>) value : null;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> getArray(Map<String, Object> object, String key) {
        if (object == null) {
            return null;
        }
        Object value = object.get(key);
        return value instanceof List ? (List<Object>) value : null;
    }

    private Object readValue() {
        skipWhitespace();
        if (pos >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON input");
        }
        char c = json.charAt(pos);
        switch (c) {
            case '{':
                return readObject();
            case '[':
                return readArray();
            case '"':
                return readString();
            case 't':
            case 'f':
                return readBoolean();
            case 'n':
                return readNull();
            default:
                return readNumber();
        }
    }

    private Map<String, Object> readObject() {
        expect('{');
        Map<String, Object> result = new LinkedHashMap<>();
        skipWhitespace();
        if (peek() == '}') {
            pos++;
            return result;
        }
        while (true) {
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            expect(':');
            Object value = readValue();
            result.put(key, value);
            skipWhitespace();
            char next = nextChar();
            if (next == '}') {
                break;
            }
            if (next != ',') {
                throw new IllegalArgumentException("Expected ',' or '}' at position " + (pos - 1));
            }
        }
        return result;
    }

    private List<Object> readArray() {
        expect('[');
        List<Object> result = new ArrayList<>();
        skipWhitespace();
        if (peek() == ']') {
            pos++;
            return result;
        }
        while (true) {
            result.add(readValue());
            skipWhitespace();
            char next = nextChar();
            if (next == ']') {
                break;
            }
            if (next != ',') {
                throw new IllegalArgumentException("Expected ',' or ']' at position " + (pos - 1));
            }
        }
        return result;
    }

    private String readString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (pos >= json.length()) {
                throw new IllegalArgumentException("Unterminated string starting near position " + pos);
            }
            char c = json.charAt(pos++);
            if (c == '"') {
                break;
            }
            if (c == '\\') {
                if (pos >= json.length()) {
                    throw new IllegalArgumentException("Unterminated escape sequence");
                }
                char escapeChar = json.charAt(pos++);
                switch (escapeChar) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'u':
                        if (pos + 4 > json.length()) {
                            throw new IllegalArgumentException("Truncated \\u escape");
                        }
                        sb.append((char) Integer.parseInt(json.substring(pos, pos + 4), 16));
                        pos += 4;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid escape character: \\" + escapeChar);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Boolean readBoolean() {
        if (json.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        }
        if (json.startsWith("false", pos)) {
            pos += 5;
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid literal at position " + pos);
    }

    private Object readNull() {
        if (json.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        throw new IllegalArgumentException("Invalid literal at position " + pos);
    }

    private Number readNumber() {
        int start = pos;
        boolean isDouble = false;
        if (pos < json.length() && json.charAt(pos) == '-') {
            pos++;
        }
        while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
            pos++;
        }
        if (pos < json.length() && json.charAt(pos) == '.') {
            isDouble = true;
            pos++;
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
                pos++;
            }
        }
        if (pos < json.length() && (json.charAt(pos) == 'e' || json.charAt(pos) == 'E')) {
            isDouble = true;
            pos++;
            if (pos < json.length() && (json.charAt(pos) == '+' || json.charAt(pos) == '-')) {
                pos++;
            }
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
                pos++;
            }
        }
        String number = json.substring(start, pos);
        if (number.isEmpty() || "-".equals(number)) {
            throw new IllegalArgumentException("Invalid number at position " + start);
        }
        return isDouble ? (Number) Double.parseDouble(number) : (Number) Long.parseLong(number);
    }

    private char peek() {
        return pos < json.length() ? json.charAt(pos) : '\0';
    }

    private char nextChar() {
        if (pos >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON input");
        }
        return json.charAt(pos++);
    }

    private void expect(char c) {
        skipWhitespace();
        if (pos >= json.length() || json.charAt(pos) != c) {
            throw new IllegalArgumentException("Expected '" + c + "' at position " + pos);
        }
        pos++;
    }

    private void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }
}
