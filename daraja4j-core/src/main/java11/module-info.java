/**
 * Overrides {@code src/main/java9/module-info.java} for Java 11+ runtimes:
 * packaged into {@code META-INF/versions/11/}, which a Java 11+ module-path
 * launch loads in preference to the {@code versions/9/} descriptor (the
 * multi-release jar mechanism picks the highest applicable versioned
 * module-info, not just the highest versioned regular class files).
 *
 * <p>The one substantive difference from the Java 9 descriptor: this one
 * adds {@code requires java.net.http}, since only here (Java 11+) does
 * that platform module actually exist, and only here does
 * {@code internal.HttpTransport} actually use
 * {@code java.net.http.HttpClient} - without this, the module system
 * denies that class read access to {@code java.net.http} even though the
 * module is present, throwing an {@code IllegalAccessError} at run time on
 * a real module-path launch despite compiling and running fine on the
 * classpath (where JPMS read restrictions don't apply).
 */
module io.github.josemodi97.daraja4j.core {
    requires java.net.http;

    exports io.github.josemodi97.daraja4j;
    exports io.github.josemodi97.daraja4j.model;
    exports io.github.josemodi97.daraja4j.exception;
    exports io.github.josemodi97.daraja4j.util;
}
