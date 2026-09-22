plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "2.1.1"
}

group = "io.github.josemodi97"
// Single source of truth for "what version of the main reactor's modules
// does this standalone build's own version - and its test dependencies
// below - line up with". Pass -PdarajaVersion=X.Y.Z to override; the
// fallback must be kept in sync with the root reactor's current version
// (see pom.xml) since the two are released independently.
val darajaReactorVersion = project.findProperty("darajaVersion") as String? ?: "0.1.0"
version = darajaReactorVersion

description = "Gradle plugin for daraja4j: scaffolds a placeholder daraja4j.properties " +
        "credentials file into your project (./gradlew daraja4jInit)."

repositories {
    mavenCentral()
    // The generated Spring Boot examples are compile-verified in tests
    // against the real daraja4j-spring-boot2/3-starter jars, which aren't
    // on Maven Central (this repo isn't published yet) - only available
    // locally after `gradle publishToMavenLocal` (or `mvn install`) from
    // the root reactor, since this is a standalone build. See PLAN.md.
    mavenLocal()
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.named<JavaCompile>("compileJava") {
    // Gradle plugin bytecode is loaded by whatever JVM runs the Gradle
    // daemon; targeting Java 8 keeps this plugin usable by the widest range
    // of Gradle/JDK combinations, matching the rest of daraja4j. This is
    // deliberately NOT applied to compileTestJava: the test suite's
    // compile-verification of the generated Spring Boot examples needs
    // daraja4j-spring-boot3-starter and daraja4j-jakarta on its classpath,
    // which declare a Java 17 / 11 floor via Gradle module metadata - a
    // constraint on the shipped plugin jar, not on tests that never leave
    // this build.
    options.release.set(8)
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Test-only: the generated framework examples (Daraja4jInitTask /
    // FrameworkExample) are verified by actually compiling them with
    // javax.tools.JavaCompiler against these real dependency jars - not
    // just eyeballed - so a wrong generated import or method signature
    // fails the build, not just looks plausible.
    testImplementation("javax.servlet:javax.servlet-api:4.0.1")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testImplementation("io.github.josemodi97:daraja4j-core:$darajaReactorVersion")
    testImplementation("io.github.josemodi97:daraja4j-servlet:$darajaReactorVersion")
    testImplementation("io.github.josemodi97:daraja4j-jakarta:$darajaReactorVersion")
    testImplementation("io.github.josemodi97:daraja4j-spring-boot2-starter:$darajaReactorVersion")
    testImplementation("io.github.josemodi97:daraja4j-spring-boot3-starter:$darajaReactorVersion")
}

gradlePlugin {
    // Required by com.gradle.plugin-publish for the Plugin Portal listing.
    website.set("https://github.com/JoseModi97/daraja4j")
    vcsUrl.set("https://github.com/JoseModi97/daraja4j")

    plugins {
        create("daraja4j") {
            id = "io.github.josemodi97.daraja4j"
            implementationClass = "io.github.josemodi97.daraja4j.gradle.Daraja4jPlugin"
            displayName = "daraja4j"
            description = project.description
            // Keyword tags are how the Plugin Portal's own search surfaces
            // this plugin - the same discoverability goal as the Maven
            // Central SEO checklist in PLAN.md.
            tags.set(listOf(
                "daraja", "mpesa", "kenya", "payment-gateway", "safaricom",
                "stk-push", "fintech", "mobile-money", "sdk", "scaffolding", "payments"
            ))
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named("test") {
    dependsOn(tasks.named("pluginUnderTestMetadata"))
}
