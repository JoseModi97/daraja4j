package io.github.josemodi97.daraja4j.jakarta;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Reads a servlet request's raw body as text. Daraja posts JSON, not form
 * parameters, so callback handlers need the raw body rather than
 * {@code request.getParameterMap()}.
 */
final class ServletRequestBodyReader {

    private ServletRequestBodyReader() {
    }

    static String readBody(HttpServletRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            char[] buffer = new char[4096];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                body.append(buffer, 0, read);
            }
        }
        return body.toString();
    }
}
