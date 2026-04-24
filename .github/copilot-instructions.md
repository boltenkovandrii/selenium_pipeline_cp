# Copilot instructions for selenium_pipeline_cp

Purpose: help future Copilot sessions understand how to build, run, and reason about this repository (Selenium + JUnit5 tests using Gradle and Allure).

## Build, test and report commands
- Full run (clean, tests, Allure report):
  - Windows: .\gradlew.bat clean test allureReport
  - Unix: ./gradlew clean test allureReport
- Run tests only: .\gradlew.bat test
- Run a specific test class: .\gradlew.bat test --tests "com.andrii.test.tests.OpenPageTest"
- Run a specific test method: .\gradlew.bat test --tests "com.andrii.test.tests.OpenPageTest.testMethodName"
- Run tests with tag filtering (overrides default 'regression'):
  - .\gradlew.bat clean test -DincludeTags=current allureReport
- Extra options commonly used:
  - -Dbrowser=edge  (browser selection)
  - -Dthreads=2     (parallel threads)
  - -DfirefoxPath="C:\\path\\to\\firefox.exe"

Notes:
- Gradle wrapper is included; prefer ./gradlew / .\gradlew.bat.
- Test task sets ignoreFailures=true in build.gradle (tests won't fail the Gradle task by default).

## GitHub Actions workflows (added)
- build-images.yml
  - Trigger: manual (workflow_dispatch)
  - Purpose: builds Docker images from dockerfiles/* and pushes to ghcr.io as:
    - ghcr.io/<owner>/<repo>-hub:<tag>
    - ghcr.io/<owner>/<repo>-chrome:<tag>
    - ghcr.io/<owner>/<repo>-firefox:<tag>
    - ghcr.io/<owner>/<repo>-edge:<tag>
  - Input: image-tag
  - Secrets: uses GITHUB_TOKEN by default; can be switched to a GHCR_PAT secret if needed

- tests-run.yml
  - Trigger: manual (workflow_dispatch)
  - Purpose: pulls the images from ghcr.io, starts Selenium Grid via docker-compose, runs Gradle tests, produces Allure report, and uploads artifacts
  - Inputs: browser, threads, tags, image-tag
  - Artifacts: public-archives.tar.gz (contains Allure report and Downloads), container logs (uploaded separately)

## High-level architecture
- Test suite implemented in src/test/java:
  - com.andrii.test.tests — JUnit5 test scenarios
  - com.andrii.test.pages — Page Object classes (PageObject pattern)
  - com.andrii.test.pages.commons — shared page fragments
  - com.andrii.test.base — test bootstrap and utilities (WebDriverManager, TestConfig, TestBase, ParallelExecutionStrategy, FileUtils, EventListener)
- Resources in src/test/resources contain drivers, config.properties, and Allure configuration.
- Reporting: Allure commandline is integrated via Gradle plugin; generated report lives at build/reports/allure-report/allureReport (open index.html).
- CI: Dockerfiles/* and GitHub Actions workflows (.github/workflows) are used to build images and run tests in CI. Workflows push/pull images to ghcr.io.

## Key repository conventions
- Test selection:
  - Tests are annotated with @Tag; default tag is 'regression' (see build.gradle includeTags property).
  - WIP tests are excluded via excludeTags 'WIP'.
- Configuration precedence:
  - src/test/resources/config.properties provides defaults but may be overridden by system properties passed to Gradle (-D...).
  - Common system properties used: includeTags, browser, threads, firefoxPath
- Parallelism:
  - Custom ParallelExecutionStrategy (com.andrii.test.base.ParallelExecutionStrategy) controls JUnit parallel execution. Expect classes to run concurrently; tests use thread-safe setup in WebDriverManager.
- Page Object pattern:
  - Pages live under com.andrii.test.pages. Tests should interact with pages rather than raw WebDriver where possible.
- Driver binaries:
  - Drivers are placed in src/test/resources/drivers (for convenience) but are likely outdated; keep them updated locally or provide path to local/bundled driver via config.
- Allure:
  - Use Gradle task 'allureReport' to generate reports after tests. Allure properties in src/test/resources/allure.properties.

## Files to consult when reasoning about behavior
- build.gradle — core Gradle configuration, test task details, JUnit/Allure integration
- src/test/resources/config.properties — runtime defaults for browsers and paths
- src/test/java/com/andrii/test/base/WebDriverManager.java — how WebDriver instances are created and configured
- src/test/java/com/andrii/test/base/ParallelExecutionStrategy.java — parallel execution rules
- src/test/java/com/andrii/test/pages/* & tests/* — test flows and Page Objects
- .github/workflows/build-images.yml — workflow to build and push Docker images to ghcr.io
- .github/workflows/tests-run.yml — workflow to start Selenium Grid via docker-compose, run Gradle tests, and upload Allure + Downloads as artifacts

## Other assistant configs
- No CLAUDE.md, AGENTS.md, .cursorrules, .windsurfrules, CONVENTIONS.md, or similar AI assistant config files detected. If added, include relevant bits here.

---

Created by Copilot CLI guidance file. Keep concise; update if CI, report paths, or test conventions change.
