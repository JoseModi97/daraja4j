plugins {
    `java-platform`
    `maven-publish`
    signing
}

description = "Bill of materials for daraja4j: import this to pin matching versions of every " +
        "daraja4j module without repeating version numbers."

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(project(":daraja4j-core"))
        api(project(":daraja4j-servlet"))
        api(project(":daraja4j-jakarta"))
        api(project(":daraja4j-spring-boot2-starter"))
        api(project(":daraja4j-spring-boot3-starter"))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "daraja4j-bom"
            from(components["javaPlatform"])

            pom {
                name.set("daraja4j-bom")
                description.set(project.description)
                url.set("https://github.com/JoseModi97/daraja4j")
                inceptionYear.set("2026")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("josemodi97")
                        name.set("Jose Modi")
                        url.set("https://github.com/JoseModi97")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/JoseModi97/daraja4j.git")
                    developerConnection.set("scm:git:ssh://git@github.com/JoseModi97/daraja4j.git")
                    url.set("https://github.com/JoseModi97/daraja4j")
                }
            }
        }
    }

    repositories {
        maven {
            name = "buildDir"
            url = uri(rootProject.layout.buildDirectory.dir("staging-deploy"))
        }
    }
}
