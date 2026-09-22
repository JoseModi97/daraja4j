plugins {
    `java-library`
    `maven-publish`
    signing
}

description = "daraja4j adapter for javax.servlet (Tomcat 8/9, Spring Boot 2, plain Java EE servlets)."

dependencies {
    api(project(":daraja4j-core"))
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("javax.servlet:javax.servlet-api:4.0.1")
    testImplementation("org.mockito:mockito-core:5.14.2")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "io.github.josemodi97.daraja4j.servlet")
    }
}

apply(from = "${rootDir}/gradle/publishing.gradle.kts")
