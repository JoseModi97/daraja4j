package io.github.josemodi97.daraja4j.internal;

/**
 * Minimal, zero-dependency JSON object builder. Daraja request bodies are
 * flat or at most one level of caller-supplied nested literal, so this only
 * needs to build a single {@code {...}} object &mdash; not a general-purpose
 * JSON writer. Not part of the public API.
 */
public final class JsonWriter {

    private final StringBuilder sb = new StringBuilder("{");
    private boolean first = true;

    /** Adds a string field; the field is omitted entirely when {@code value} is {@code null}. */
    public JsonWriter field(String key, String value) {
        if (value == null) {
            return this;
        }
        appendKey(key);
        sb.append('"').append(escape(value)).append('"');
        return this;
    }

    /** Adds a numeric field; the field is omitted entirely when {@code value} is {@code null}. */
    public JsonWriter field(String key, Number value) {
        if (value == null) {
            return this;
        }
        appendKey(key);
        sb.append(value);
        return this;
    }

    /** Adds a boolean field. */
    public JsonWriter field(String key, boolean value) {
        appendKey(key);
        sb.append(value);
        return this;
    }

    /**
     * Adds a field whose value is a pre-built JSON literal (object or array),
     * for the rare case a caller needs nested structure. {@code rawJson} is
     * written verbatim, so the caller is responsible for its validity; the
     * field is omitted entirely when {@code rawJson} is {@code null}.
     */
    public JsonWriter rawField(String key, String rawJson) {
        if (rawJson == null) {
            return this;
        }
        appendKey(key);
        sb.append(rawJson);
        return this;
    }

    public String build() {
        return sb.toString() + "}";
    }

    private void appendKey(String key) {
        if (!first) {
            sb.append(',');
        }
        first = false;
        sb.append('"').append(escape(key)).append("\":");
    }

    /** Escapes a string for embedding inside a JSON string literal (without the surrounding quotes). */
    public static String escape(String value) {
        StringBuilder out = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"':
                    out.append("\\\"");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\t':
                    out.append("\\t");
                    break;
                case '\b':
                    out.append("\\b");
                    break;
                case '\f':
                    out.append("\\f");
                    break;
                default:
                    if (c < 0x20) {
                        out.append("\\u");
                        String hex = Integer.toHexString(c);
                        for (int pad = hex.length(); pad < 4; pad++) {
                            out.append('0');
                        }
                        out.append(hex);
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}
