/**
 * Packaged as a Java 9+ module descriptor via a multi-release jar (this file
 * lives under {@code src/main/java9}, compiled at {@code --release 9} and
 * shaded into {@code META-INF/versions/9/} - the base jar entries stay
 * Java-8-only, since Java 8 doesn't understand multi-release jars and
 * simply ignores the versioned folder).
 *
 * <p>Only the public API packages are exported; {@code .internal} stays
 * encapsulated even from module-path consumers.
 *
 * <p>This descriptor deliberately does <em>not</em> {@code requires
 * java.net.http} - that module didn't exist yet at the Java 9 platform
 * level this file is compiled against ({@code --release 9} genuinely
 * cannot resolve the symbol "java.net.http", compile error, static or
 * not), and on Java 9/10 the base {@code HttpTransport} (HttpURLConnection)
 * is what actually runs anyway. {@code src/main/java11/module-info.java} is
 * a second descriptor, compiled at {@code --release 11} and packaged into
 * {@code META-INF/versions/11/}, which Java 11+ runtimes load instead of
 * this one and which does declare {@code requires java.net.http} - that's
 * where the Java 11+ {@code HttpTransport} variant actually needs it.
 */
module io.github.josemodi97.daraja4j.core {
    exports io.github.josemodi97.daraja4j;
    exports io.github.josemodi97.daraja4j.model;
    exports io.github.josemodi97.daraja4j.exception;
    exports io.github.josemodi97.daraja4j.util;
}
