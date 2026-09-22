# daraja4j

daraja4j is a dependency-free Java SDK for the **Safaricom Daraja M-Pesa API**: OAuth access-token management, M-Pesa Express (STK Push), C2B, B2C, B2B, B2Pochi, transaction reversal, transaction status, account balance, the Pull API, and M-Pesa Ratiba standing orders — with a plain, framework-agnostic Java API that drops into Spring Boot, Jakarta EE, Quarkus, Micronaut, plain servlets, or a bare `public static void main`.

It is an independent, community-built SDK. It is not produced, endorsed, or supported by Safaricom or M-Pesa.

## Why daraja4j

- **Runs everywhere** — `daraja4j-core` compiles and is tested against the Java 8 language level, so the same jar runs unmodified on Java 8, 11, 17, 21, and every release after it. (`daraja4j-jakarta` needs Java 11+ and `daraja4j-spring-boot3-starter` needs Java 17+ — inherent to the ecosystems they target, Jakarta EE 9+ and Spring Boot 3 respectively.)
- **Zero runtime dependencies at the core** — `daraja4j-core` uses only `javax.crypto`, `java.net`, `java.security`, and `java.math` from the JDK itself. Nothing to conflict with your existing dependency tree. Framework modules add only what that framework already requires.
- **Framework-neutral by design** — the core has no compile-time knowledge of Spring, servlets, or any web framework, so it works identically in a Spring Boot controller, a Quarkus resource, a Lambda handler, or a CLI tool.
- **Correct by construction** — `BigDecimal` for money (never a lossy `double`), a builder API instead of stringly-typed maps, transparent OAuth token caching/refresh, and typed parsers for every callback shape Daraja sends.
- **Available for both Maven and Gradle** — the source tree builds under both, so contributors aren't locked into one toolchain.
- **Async-friendly** — every operation has a `CompletableFuture`-returning sibling, so it composes cleanly with reactive and async codebases without pulling in a reactive library.
- **A real JPMS module, not just a manifest hint** — `daraja4j-core` ships as a multi-release jar with a genuine `module-info.java` for Java 9+, and swaps in a `java.net.http.HttpClient`-based transport (HTTP/2, negotiated automatically) for Java 11+ — both transparent to callers.
- **Ships as a native binary too** — `daraja4j-cli` builds with GraalVM native-image for a dependency-free, instant-startup executable, alongside the regular JVM fat jar.

## Compatibility

| | Supported |
|---|---|
| Java | 8, 11, 17, 21, and every release in between (compiled at the `--release 8` bytecode level) |
| Build tools (as a dependency) | Any Maven 3.x or Gradle version — it's a plain jar + standard POM once published |
| Build tools (building this repo) | Maven 3.6.3+, Gradle 8.0+ |
| Frameworks | Plain Java, Spring / Spring Boot (2.x and 3.x, dedicated starters), Jakarta EE & Java EE servlets (dedicated adapters), Quarkus, Micronaut, Vert.x, Android (API 26+, via desugaring) |
| Module system | Works on the classpath (Java 8+) and as a real named module on the module path (Java 9+) |

## Installation

[![Maven Central](https://img.shields.io/maven-central/v/io.github.josemodi97/daraja4j-core.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.josemodi97/daraja4j-core)

**Live on Maven Central** — `0.1.0` is published and resolvable with no extra repository configuration.

### Maven

```xml
<dependency>
  <groupId>io.github.josemodi97</groupId>
  <artifactId>daraja4j-core</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
implementation("io.github.josemodi97:daraja4j-core:0.1.0")
```

### Gradle (Groovy DSL)

```groovy
implementation 'io.github.josemodi97:daraja4j-core:0.1.0'
```

Javadoc: [javadoc.io/doc/io.github.josemodi97/daraja4j-core](https://javadoc.io/doc/io.github.josemodi97/daraja4j-core/0.1.0/index.html)

The `daraja4j-gradle-plugin` project-scaffolding plugin is uploaded to the Gradle Plugin Portal but pending its first-time manual ID review — see [Project scaffolding](#project-scaffolding-maven--gradle-plugins) below.

Building from source instead? `mvn install` / `./gradlew publishToMavenLocal` from a clone of this repo installs to your local repository. See [PLAN.md](PLAN.md) for the release process.

## Modules

`daraja4j-core` is the only one you need for a plain Java app. Add one of these on top for framework glue:

| Artifact | For | Brings |
|---|---|---|
| `daraja4j-core` | Any Java 8+ app | `Daraja4jClient`, OAuth token caching, and every Daraja operation. No dependencies. |
| `daraja4j-servlet` | Tomcat 8/9, Spring Boot 2, plain Java EE servlets | `javax.servlet` callback handlers for STK, Result, and C2B notifications. |
| `daraja4j-jakarta` | Tomcat 10+, Spring Boot 3, Jakarta EE 9+ | The same handlers, for the `jakarta.servlet` namespace. |
| `daraja4j-spring-boot2-starter` | Spring Boot 2.x | Auto-configured `Daraja4jClient` bean from `daraja4j.*` properties, plus opt-in webhook endpoints + events. |
| `daraja4j-spring-boot3-starter` | Spring Boot 3.x | Same, for the Jakarta namespace (Java 17+ floor). |
| `daraja4j-cli` | Terminal / CI | `stk-push`, `status`, `b2c`, `reverse`, `balance`, `parse-callback` subcommands. |

## Quickstart

### 1. Configure

```java
Daraja4jConfig config = Daraja4jConfig.builder()
        .consumerKey("YOUR_CONSUMER_KEY")
        .consumerSecret("YOUR_CONSUMER_SECRET")
        .environment(Daraja4jConfig.Environment.SANDBOX)
        .defaultShortcode("174379")
        .defaultPasskey("YOUR_PASSKEY")
        .defaultCallbackUrl("https://yourapp.example.com/daraja4j/stk-callback")
        .build();

Daraja4jClient client = new Daraja4jClient(config);
```

Prefer environment variables (containers, CI, 12-factor apps)? `Daraja4jConfig.fromEnvironment()` reads `DARAJA4J_CONSUMER_KEY`, `DARAJA4J_CONSUMER_SECRET`, `DARAJA4J_ENVIRONMENT`, `DARAJA4J_SHORTCODE`, `DARAJA4J_PASSKEY`, `DARAJA4J_CALLBACK_URL`, and more — see `Daraja4jConfig` javadoc for the full list.

OAuth access tokens are fetched, cached, and refreshed automatically — you never touch a token directly.

### 2. Trigger an STK Push

```java
StkPushResult ack = client.stkPush(StkPushRequest.builder()
        .amount(500)
        .partyA("0712345678")
        .accountReference("INV-0001")
        .transactionDesc("School fees")
        .build());

System.out.println(ack.getCheckoutRequestId());
```

`ack` is only Daraja's acknowledgement that the USSD prompt was sent — the actual outcome arrives later at your `callbackUrl`, or can be polled with `client.stkPushQuery(...)`.

### 3. Parse an inbound callback

```java
// e.g. inside a Spring @PostMapping, a servlet doPost, or any framework's
// raw-body-to-String binding:
StkCallbackResult result = StkCallbackResult.parse(rawJsonBody);

if (result.isSuccess()) {
    markOrderPaid(result.getCheckoutRequestId(), result.getMpesaReceiptNumber());
} else {
    log.warn("daraja4j: STK push failed - {}", result.getResultDesc());
}
```

**Read the [Security](#security--best-practices) section below before wiring this into production** — Daraja sends no cryptographic signature on callbacks.

### 4. Disburse with B2C

```java
B2cResult ack = client.b2c(B2cRequest.builder()
        .amount(1000)
        .partyB("0712345678")
        .remarks("Refund for order 4821")
        .build());
```

Requires `initiatorName` and `securityCredential` — see `SecurityCredentialEncoder` for encoding your Initiator password with Safaricom's public certificate.

### 5. Servlet / Jakarta adapters

```java
Daraja4jStkCallbackServletHandler handler = new Daraja4jStkCallbackServletHandler()
        .onSuccess((result, req, res) -> markOrderPaid(result.getCheckoutRequestId(), result.getMpesaReceiptNumber()))
        .onFailure((result, req, res) -> log.warn(result.getResultDesc()));

// inside your doPost(HttpServletRequest req, HttpServletResponse res):
handler.handle(req, res);
```

(`daraja4j-jakarta` is the identical API under `jakarta.servlet` imports instead of `javax.servlet`.) `Daraja4jResultCallbackServletHandler` covers B2C/B2B/B2Pochi/Reversal/Balance/StatusQuery results, and `Daraja4jC2bServletHandler` covers C2B confirmations.

### 6. Spring Boot starters

```yaml
# application.yml
daraja4j:
  consumer-key: ${DARAJA4J_CONSUMER_KEY}
  consumer-secret: ${DARAJA4J_CONSUMER_SECRET}
  environment: SANDBOX
  shortcode: "174379"
  passkey: ${DARAJA4J_PASSKEY}
  callback-url: https://yourapp.example.com/daraja4j/stk-callback
  webhook:
    stk:
      enabled: true
```

That's it — a `Daraja4jClient` bean is now available for injection, and `POST /daraja4j/stk-callback` is live. React to a payment without writing a controller:

```java
@EventListener
void onPaid(Daraja4jStkPaymentVerifiedEvent event) {
    orderService.markPaid(event.getResult().getCheckoutRequestId(), event.getResult().getMpesaReceiptNumber());
}
```

The result and C2B endpoints are the same shape, gated by `daraja4j.webhook.result.enabled` and `daraja4j.webhook.c2b.enabled` respectively. Use `daraja4j-spring-boot2-starter` on Spring Boot 2.x, `daraja4j-spring-boot3-starter` on 3.x.

### CLI

```bash
# trigger an STK push
java -jar daraja4j-cli.jar stk-push --consumer-key ... --consumer-secret ... --environment SANDBOX \
    --shortcode 174379 --passkey ... --callback-url https://yourapp.example.com/daraja4j/stk-callback \
    --amount 500 --phone 0712345678 --account-reference INV-0001

# check transaction status
java -jar daraja4j-cli.jar status --transaction-id NLJ7RT61SV

# parse a captured callback payload, piped straight from curl/a log
cat captured-callback.json | java -jar daraja4j-cli.jar parse-callback --type stk
```

Every flag falls back to a `DARAJA4J_*` environment variable, so CI pipelines can omit credentials from the command line entirely.

### Project scaffolding: Maven & Gradle plugins

`daraja4j-maven-plugin` and `daraja4j-gradle-plugin` write a placeholder `daraja4j.properties` into your project (without overwriting one that already exists) — fill in your credentials and you're ready to build a `Daraja4jClient`.

```bash
# Maven
mvn io.github.josemodi97:daraja4j-maven-plugin:init

# Gradle (after adding: plugins { id("io.github.josemodi97.daraja4j") version "0.1.0" })
./gradlew daraja4jInit
```

Both auto-detect which framework you're using from your project's own dependencies — a `jakarta.servlet-api`/`javax.servlet-api` dependency, or a Spring Boot 2.x/3.x dependency, gets you a working, compile-verified STK-callback example dropped into `src/main/java/`. Override with `-Ddaraja4j.framework=<value>` (Maven) or `framework.set("<value>")` on the `daraja4jInit` task (Gradle). Supported values: `auto` (Maven default) / `plain` / `servlet` / `jakarta` / `spring-boot2` / `spring-boot3`.

## API Reference Overview

| Method | Description |
|---|---|
| `client.stkPush(StkPushRequest)` | Triggers an M-Pesa Express USSD PIN prompt |
| `client.stkPushQuery(StkPushQueryRequest)` | Polls the outcome of a previous STK push |
| `client.registerC2bUrls(RegisterC2bUrlsRequest)` | Registers validation/confirmation URLs for a shortcode |
| `client.simulateC2b(C2bSimulateRequest)` | Simulates an inbound C2B payment — sandbox only |
| `client.b2c(B2cRequest)` | Sends money to a registered M-Pesa customer |
| `client.b2b(B2bRequest)` | Pays another organization's pay bill or till |
| `client.b2Pochi(B2PochiRequest)` | Pays a Pochi la Biashara-enabled personal till |
| `client.reverseTransaction(ReversalRequest)` | Reverses a completed C2B transaction |
| `client.queryTransactionStatus(TransactionStatusRequest)` | Checks the status of a prior transaction |
| `client.queryAccountBalance(AccountBalanceRequest)` | Queries the current account balance |
| `client.registerPullTransactions(PullTransactionsRegisterRequest)` | One-time Pull API registration |
| `client.queryPullTransactions(PullTransactionsQueryRequest)` | Retrieves C2B transactions for a period |
| `client.createStandingOrder(StandingOrderRequest)` | Creates an M-Pesa Ratiba standing order |
| `client.refreshToken()` | Forces a fresh OAuth token fetch |
| `PhoneNormalizer.normalize(String)` | Normalizes Kenyan MSISDNs (`07...`, `01...`, `+254...` → `254...`) |
| `StkCredentialsGenerator.password(...)` | Builds the STK Push `Password`/`Timestamp` pair |
| `SecurityCredentialEncoder.encode(...)` | RSA-encrypts an Initiator password for `SecurityCredential` |

Every operation above has a corresponding `*Async` `CompletableFuture` variant (`stkPushAsync`, `b2cAsync`, etc.).

Full Javadoc: `https://javadoc.io/doc/io.github.josemodi97/daraja4j-core` (populates once published to Maven Central).

## Security & Best Practices

- **Keep secrets out of source control.** Load `consumerKey`/`consumerSecret`/`securityCredential` from environment variables, a secrets manager, or `Daraja4jConfig.fromEnvironment()` — never hardcode them.
- **Daraja sends no cryptographic signature on callbacks.** Unlike some payment gateways, STK/Result/C2B callback bodies carry no HMAC or signature field to verify. The `.model` parsers (`StkCallbackResult`, `Daraja4jResultCallback`, `C2bConfirmation`, `StandingOrderCallback`) are **structural parsers only** — they validate JSON shape, not authenticity. Establish trust instead through:
  - **Unguessable callback URLs** — embed a long random path segment or per-merchant token in your `callbackUrl`/`resultUrl`/`confirmationUrl`, and check it before parsing the body.
  - **Reconciliation before crediting an account** — treat a callback as advisory and confirm anything money-affecting with a follow-up `queryTransactionStatus`/`stkPushQuery` call before marking an order paid, rather than trusting the push alone.
  - **Network-layer IP allow-listing** as defense-in-depth only (Safaricom's outbound IP ranges can change — never rely on this as your sole control).
  - **HTTPS + POST-only + body-size-limited handlers.**
- **`SecurityCredential` requires the right certificate for your environment.** `SecurityCredentialEncoder.encode(...)` RSA-encrypts your Initiator password with Safaricom's *public* certificate — sandbox and production use **different** certificates. Using the wrong one for your target environment is the single most common Daraja integration failure, since the request otherwise looks well-formed and only fails opaquely at Safaricom's end.
- **STK Push timestamps must use Nairobi time.** `StkCredentialsGenerator.timestamp()` deliberately uses `Africa/Nairobi` rather than the JVM's default zone — a server hosted in UTC calling raw `LocalDateTime.now()` would silently fail Daraja's timestamp-freshness check.
- **Always normalize phone numbers** with `PhoneNormalizer.normalize(...)` before sending an STK push, B2C, or B2B request — Safaricom requires the `2547XXXXXXXX` / `2541XXXXXXXX` shape.

## Roadmap

All modules listed above are implemented, tested, and **published**: `0.1.0` is live on Maven Central, with both a real Maven and a real Gradle build, `daraja4j-core` shipping as a genuine multi-release JPMS module, and GraalVM native-image wired for the CLI. What's left: `daraja4j-gradle-plugin` is uploaded to the Gradle Plugin Portal and awaiting its first-time manual ID review, and richer init scaffolding beyond a single STK-callback example. See [PLAN.md](PLAN.md) for the full architecture and what's actually left.

## Contributing

Issues and pull requests are welcome — see [PLAN.md](PLAN.md) for the module layout. Build everything with `mvn test` or `./gradlew test` from the repo root; `daraja4j-gradle-plugin` is a standalone Gradle build (`cd daraja4j-gradle-plugin && ./gradlew test`) since a Gradle plugin project can't sanely nest inside the reactor it builds.

## License

MIT License. See [LICENSE](LICENSE) for details.
