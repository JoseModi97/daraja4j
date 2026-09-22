# daraja4j — Architecture & Roadmap

This document is the working plan for daraja4j: a Java SDK for the Safaricom
Daraja M-Pesa API, built as an independent product with its own name,
package namespace, and API design.

## 1. Status

| Module | Status | Description |
|---|---|---|
| `daraja4j-core` | ✅ **Implemented** | OAuth token caching, all 13 Daraja operations, JSON serialization, callback parsers. Zero runtime dependencies. |
| `daraja4j-servlet` | ✅ **Implemented** | `javax.servlet` callback handlers (STK/Result/C2B), for Tomcat 8/9, Spring Boot 2, older Java EE. |
| `daraja4j-jakarta` | ✅ **Implemented** | `jakarta.servlet` callback handlers, for Tomcat 10+, Spring Boot 3, Jakarta EE 9+. |
| `daraja4j-spring-boot2-starter` | ✅ **Implemented** | Auto-configured `Daraja4jClient` bean + `daraja4j.*` property binding + three opt-in webhook controllers/events, for Spring Boot 2.x. |
| `daraja4j-spring-boot3-starter` | ✅ **Implemented** | Same, for Spring Boot 3.x (Jakarta namespace, Java 17 floor). |
| `daraja4j-cli` | ✅ **Implemented** | `stk-push`, `status`, `b2c`, `reverse`, `balance`, `parse-callback` subcommands (picocli), distributed as a runnable fat jar (Gradle Shadow / Maven Shade) and a GraalVM native-image binary. |
| `daraja4j-maven-plugin` | ✅ **Implemented** | `mvn io.github.josemodi97:daraja4j-maven-plugin:init` — auto-detects your framework and scaffolds a working STK-callback example, not just a placeholder properties file. |
| `daraja4j-gradle-plugin` | ✅ **Implemented** | `./gradlew daraja4jInit` — the same auto-detecting scaffolding, as a standalone-built Gradle plugin. Not yet submitted to the Gradle Plugin Portal (§6b — new plugin IDs need a first-time manual review before `plugins { id(...) }` resolves publicly). |
| `daraja4j-bom` | ✅ **Implemented** | Bill-of-materials (`java-platform` / `<packaging>pom</packaging>`) pinning matching versions of every library module. |

Why core first: every other module is a thin adapter around it. Shipping a
correct, well-tested core let every framework adapter and tool above be
added without ever touching the token-management/serialization/parsing logic
again.

**Build verification**: every module has been built and tested with a real
Gradle 8.11 in this environment — not just written and assumed correct,
including the standalone `daraja4j-gradle-plugin` build resolving the main
reactor's artifacts via `mavenLocal()`. The Maven side (`daraja4j-maven-plugin`,
and the root reactor's `mvn test`) mirrors the proven Gradle module structure
closely but has not been executed in this environment (no `mvn` binary
available here) — run `mvn test` from a machine with Maven installed before
relying on it for a release.

**Not yet done** — real next steps, not filler: Maven Central publishing
itself (§6, needs real Sonatype credentials), the Gradle Plugin Portal
submission (§6b, needs a Portal account), and running the Maven side of the
build for the first time on a machine with `mvn` installed.

## 2. Package & artifact identity

- **Maven `groupId`**: `io.github.josemodi97` — uses Sonatype Central
  Publishing's GitHub-ownership verification path (no domain purchase or DNS
  TXT record required, since `github.com/JoseModi97` already proves
  ownership).
- **Java package root**: `io.github.josemodi97.daraja4j`.
- **Artifact naming**: `daraja4j-<module>` (`daraja4j-core`,
  `daraja4j-servlet`, `daraja4j-maven-plugin`, ...) — consistent, greppable,
  and matches the GitHub repo name for discoverability.
- **Automatic-Module-Name**: `io.github.josemodi97.daraja4j.<module>` is set
  on every non-core module's manifest; `daraja4j-core` ships a real
  `module-info.java` instead (see §3).

## 3. Compatibility strategy: "forward and backward compatible"

**Floor: Java 8.** Both build files compile with `--release 8`
(`maven.compiler.release` / Gradle's `options.release.set(8)`), which
cross-compiles against the Java 8 API signature set regardless of which JDK
runs the build. This is the same technique used by Gson, Jackson, and Guava
to stay on Java 8 while building with modern tooling. `daraja4j-jakarta`
raises its own floor to Java 11 (Jakarta EE 9+'s own requirement) and
`daraja4j-spring-boot3-starter` to Java 17 (Spring Boot 3's own requirement)
— deliberate exceptions, not the project default.

**Ceiling: none by design.** No compiled dependency, no reflection into
internal JDK APIs, no removed API used — the jar keeps running unmodified as
new JDKs ship.

**Implemented as a multi-release jar** (`daraja4j-core` only):
- `src/main/java` — the Java 8 baseline: `HttpTransport` built on
  `HttpURLConnection`.
- `src/main/java9/module-info.java` — a real JPMS descriptor, compiled at
  `--release 9`, packaged into `META-INF/versions/9/`. Exports only
  `daraja4j` (root), `.model`, `.exception`, `.util` — `.internal` stays
  encapsulated even from module-path consumers.
- `src/main/java11/.../internal/HttpTransport.java` — an identical-API
  variant built on `java.net.http.HttpClient` (HTTP/2 auto-negotiated),
  compiled at `--release 11`, packaged into `META-INF/versions/11/`.

Verified in this environment: `jar --validate` passes on the built jar, and
`jar tf` confirms `META-INF/versions/9/module-info.class` and
`META-INF/versions/11/.../HttpTransport.class` both land in the right places
alongside the Java-8 base classes.

## 4. Daraja-specific design decisions

Unlike a simple HMAC-signed gateway, Daraja's protocol shape drove several
decisions worth recording:

- **OAuth2 client-credentials, auto-cached.** `internal.AccessTokenCache`
  holds a `volatile` cached token behind double-checked locking, fetched
  lazily on first real call (not in the constructor, so DI bean construction
  stays non-blocking and tests stay network-free) and refreshed with a
  configurable safety margin before expiry. `Daraja4jClient.refreshToken()`
  is the manual escape hatch.
- **No dependency JSON library.** `internal.JsonWriter`/`JsonReader` are
  small, hand-rolled, dependency-free — sufficient for Daraja's flat/one-level
  request bodies and moderately-nested response/callback shapes, and keep
  `daraja4j-core` at zero runtime dependencies.
- **STK Push timestamps use `Africa/Nairobi`, never the JVM default zone.**
  `StkCredentialsGenerator.timestamp()` is explicit about this — a server
  hosted in UTC calling raw `LocalDateTime.now()` would silently fail
  Daraja's timestamp-freshness check.
- **Reversal's `RecieverIdentifierType` is hardcoded to `"11"`**, not the
  general `IdentifierType` enum — Daraja's own documentation states this is
  the only accepted value for that field, unlike every other operation's
  `IdentifierType` (1/2/4).
- **B2C targets the `v3` endpoint** (`/mpesa/b2c/v3/paymentrequest`), which
  requires an `OriginatorConversationID` the caller doesn't have to supply
  (`B2cRequest`/`B2PochiRequest` auto-generate a `UUID` when unset).
- **No cryptographic callback verification exists to implement.** Daraja
  sends no signature on STK/Result/C2B/Ratiba callbacks — the `.model`
  parsers are documented as structural parsers only. See the README's
  Security section for the trust model this implies (unguessable URLs,
  reconciliation via a follow-up status query, IP allow-listing as
  defense-in-depth only).
- **`Daraja4jGateway.execute(...)` takes an explicit expected-success value**,
  not a hardcoded `"0"`, because Pull Transactions Register/Query use
  `"1000"` and Ratiba's `ResponseHeader.responseCode` uses `"200"` — passing
  `resultCodeField = null` for those three operations skips the generic
  check entirely and lets their own `Result` classes' `isSuccess()`/
  `isAccepted()` carry the real logic.
- **Field names mirror Daraja's own inconsistencies deliberately.** Two
  examples kept verbatim rather than "corrected", because the wire format is
  what it is: the C2B register/simulate response field is genuinely spelled
  `OriginatorCoversationID` (missing the 'n') in Safaricom's own API —
  `RegisterC2bUrlsResult`/`C2bSimulateResult` check both spellings so
  callers don't need to know about the typo; and B2C/B2B/Reversal/B2Pochi's
  `Occassion`/`RecieverIdentifierType` fields keep Safaricom's own
  misspellings on the wire while exposing idiomatically-spelled builder
  methods (`occasion(...)`) that translate internally.

## 5. Both build tools first-class, not one "canonical" + one "wrapper"

The root reactor is buildable end-to-end with either `mvn` or `./gradlew`
from the same source tree. Consumer-facing plugins mirror this too (Maven
`init` goal / Gradle `daraja4jInit` task, same behavior, same
`FrameworkDetector`/`FrameworkExample` logic re-implemented per build tool
since a standalone Gradle build can't share Java source with the Maven
reactor).

**Gradle plugin isolation.** `daraja4j-gradle-plugin` is deliberately a
separate, standalone Gradle build (own `settings.gradle.kts`) rather than a
subproject of the root reactor — a `java-gradle-plugin` project nesting
inside the thing it builds is awkward and creates dependency-graph cycles.
It resolves the main reactor's artifacts via `mavenLocal()` in its test
suite, keyed off a single `darajaVersion` project property (`-PdarajaVersion=X.Y.Z`)
as the one source of truth, to avoid the kind of version-drift bug that bites
projects using a separately-hardcoded test constant instead.

## 6. Versioning

Strict SemVer, `0.x.y` while the public API (`Daraja4jConfig`,
`Daraja4jClient`, the request/result model classes) is still open to change;
`1.0.0` only once the adapters have exercised it in real frameworks and a
production integration.

### 6a. Publishing to Maven Central

1. Create a Sonatype Central account, verify the `io.github.josemodi97`
   namespace via GitHub ownership.
2. Generate a GPG key, publish it to a keyserver.
3. Generate a Central Publishing Portal user token, add it as GitHub repo
   secrets (`CENTRAL_USERNAME`, `CENTRAL_PASSWORD`, `GPG_PRIVATE_KEY`,
   `GPG_PASSPHRASE`).
4. Bump the version across every POM in one shot
   (`mvn versions:set -DnewVersion=X.Y.Z -DprocessAllModules`) and the
   matching `-PdarajaVersion` default in the Gradle build files.
5. Tag `vX.Y.Z` — `release.yml` builds, signs, and uploads with
   `autoPublish=false`, so a human reviews the Portal UI before the final
   publish click.

### 6b. Publishing the Gradle plugin

Separate from Maven Central: the Gradle Plugin Portal, under its own account
and API key (`ORG_GRADLE_PROJECT_gradle.publish.key`/`.secret`), no GPG
signing required there. New plugin IDs get a manual first-time review by the
Portal team before `plugins { id(...) }` resolves publicly for anyone else —
budget for that lead time before a release announcement depends on it.

## 7. Testing strategy

- **Golden-vector tests** for `StkCredentialsGenerator.password(...)`,
  computed from a real, published Safaricom documentation sample (shortcode
  `174379`, a fixed timestamp, and the exact expected Base64 `Password` —
  decoded once during development to confirm the passkey and concatenation
  order, then hardcoded as literals in the test, independent of the
  production code path).
- **Real documentation samples, not invented ones**, for callback-parser
  tests (`StkCallbackResultTest`, `Daraja4jResultCallbackTest`) — copied
  verbatim from Safaricom's own Daraja docs, including the intentionally
  alphanumeric `ResultCode` (`"R000002"`) a failed reversal can return.
- **A real local `HttpServer`** (`com.sun.net.httpserver.HttpServer`, JDK
  built-in, no mocking framework) standing in for Daraja in
  `Daraja4jClientLocalServerTest` — exercises the whole stack end-to-end:
  OAuth token fetch, caching (asserting exactly one fetch across repeated
  and concurrent calls), forced refresh, JSON request serialization, and
  both HTTP-level and embedded-`ResponseCode` failure branches.
- **Mockito-based servlet/Jakarta handler tests** against real signed... no,
  real *structurally valid* callback bodies (there being nothing to sign),
  verifying the `onSuccess`/`onFailure` callback wiring and that a handler
  never overwrites a response the caller's own callback already committed.
- **Spring `ApplicationContextRunner` tests** proving the
  `@ConditionalOnProperty` guard on `Daraja4jAutoConfiguration` actually
  works both ways (no bean when unconfigured, a bean with the right config
  values bound when it is).
- **picocli tests** invoking the real `CommandLine(...).execute(...)`,
  capturing real stdout/stdin.
- **Plain JUnit `FrameworkDetector` tests** (no Maven/Gradle runtime needed)
  for the scaffolding plugins' auto-detection heuristic.

## 8. Security notes

See the README's Security section for the user-facing version of this. The
short version for contributors: Daraja provides no callback signature, so
`.model` parsers are intentionally documented as structural-only; treat any
change that implies otherwise (e.g. a method named `verify()` that doesn't
actually verify anything cryptographic) as a naming bug to fix before merge.
`SecurityCredentialEncoder` never logs the plaintext Initiator password or
the encrypted credential itself — only pass it through, never persist it.
