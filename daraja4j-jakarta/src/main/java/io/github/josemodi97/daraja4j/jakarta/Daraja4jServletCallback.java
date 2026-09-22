package io.github.josemodi97.daraja4j.jakarta;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Invoked by a daraja4j servlet handler once it has parsed a callback body into {@code T}. */
@FunctionalInterface
public interface Daraja4jServletCallback<T> {
    void handle(T result, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
