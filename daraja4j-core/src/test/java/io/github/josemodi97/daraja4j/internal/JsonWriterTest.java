package io.github.josemodi97.daraja4j.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JsonWriterTest {

    @Test
    void build_writesStringNumberAndBooleanFields() {
        String json = new JsonWriter()
                .field("Name", "Jane Doe")
                .field("Amount", 500)
                .field("SendStk", true)
                .build();

        assertEquals("{\"Name\":\"Jane Doe\",\"Amount\":500,\"SendStk\":true}", json);
    }

    @Test
    void field_omitsNullStringAndNumberValues() {
        String json = new JsonWriter()
                .field("Present", "value")
                .field("AbsentString", (String) null)
                .field("AbsentNumber", (Number) null)
                .build();

        assertEquals("{\"Present\":\"value\"}", json);
    }

    @Test
    void escape_handlesQuotesBackslashesAndControlChars() {
        String escaped = JsonWriter.escape("line1\nline2\t\"quoted\"\\backslash");
        assertEquals("line1\\nline2\\t\\\"quoted\\\"\\\\backslash", escaped);
    }

    @Test
    void rawField_isWrittenVerbatim() {
        String json = new JsonWriter()
                .field("ShortCode", "174379")
                .rawField("Nested", "{\"A\":1}")
                .build();

        assertEquals("{\"ShortCode\":\"174379\",\"Nested\":{\"A\":1}}", json);
    }
}
