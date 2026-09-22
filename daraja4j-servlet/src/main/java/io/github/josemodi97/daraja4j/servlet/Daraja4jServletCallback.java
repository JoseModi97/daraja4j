package io.github.josemodi97.daraja4j.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Invoked by a daraja4j servlet handler once it has parsed a callback body into {@code T}. */
@FunctionalInterface
public interface Daraja4jServletCallback<T> {
    void handle(T result, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
