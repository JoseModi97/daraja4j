# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-09-22

Initial release. `0.x` - the public API (`Daraja4jConfig`, `Daraja4jClient`,
the request/result model classes) is still open to change before `1.0.0`.

### Added

- `daraja4j-core` - dependency-free Java SDK core for the Safaricom Daraja
  M-Pesa API, covering OAuth access-token management (cached and
  auto-refreshed), STK Push (initiate + query), C2B (simulate + register
  URLs), B2C, B2B, B2Pochi, transaction reversal, transaction status,
  account balance, the Pull API, and M-Pesa Ratiba standing orders.
- Multi-release JPMS jar for `daraja4j-core`: a Java 8 baseline
  (`HttpURLConnection` transport) with a real `module-info.java` for Java
  9+, and a `java.net.http.HttpClient`-based transport variant for Java
  11+ (HTTP/2 negotiated automatically) - both transparent to callers.
- `daraja4j-servlet` / `daraja4j-jakarta` - `javax.servlet` /
  `jakarta.servlet` callback handlers for STK Push, B2C/B2B/Reversal/
  Balance/StatusQuery results, and C2B confirmations.
- `daraja4j-spring-boot2-starter` / `daraja4j-spring-boot3-starter` -
  auto-configured `Daraja4jClient` bean bound to `daraja4j.*` properties,
  plus three independent opt-in webhook endpoints publishing application
  events.
- `daraja4j-cli` - picocli command-line tool (`stk-push`, `status`, `b2c`,
  `reverse`, `balance`, `parse-callback`), distributed as a fat jar and a
  GraalVM native-image binary.
- `daraja4j-maven-plugin` / `daraja4j-gradle-plugin` - project scaffolding
  (`init` goal / `daraja4jInit` task) that auto-detects the consuming
  project's framework and writes a placeholder `daraja4j.properties` plus
  a working STK-callback example.
- `daraja4j-bom` - bill of materials pinning matching versions of every
  library module.
- Published to Maven Central under `io.github.josemodi97`.
- Published to the Gradle Plugin Portal as `io.github.josemodi97.daraja4j`.

[Unreleased]: https://github.com/JoseModi97/daraja4j/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/JoseModi97/daraja4j/releases/tag/v0.1.0
