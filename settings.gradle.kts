rootProject.name = "daraja4j"

include("daraja4j-core")
include("daraja4j-servlet")
include("daraja4j-jakarta")
include("daraja4j-spring-boot2-starter")
include("daraja4j-spring-boot3-starter")
include("daraja4j-cli")
include("daraja4j-bom")

// Build-tool plugins are standalone Gradle builds (a Gradle plugin project
// cannot sanely be a subproject of the thing it builds) - see
// daraja4j-gradle-plugin/ and daraja4j-maven-plugin/ directly.
