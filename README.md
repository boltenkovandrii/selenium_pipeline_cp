# What is this?
Just a small demo project to show how some tools work together:
- Java 17
- Junit6
- Selenium 
- Selenium hub (only set up for pipeline)
- Allure report
- GitHub Actions 

### But what does it do?
With this project you can run some of the provided tests on https://www.wikipedia.org/ both on your local machine and in CI (GitHub Actions).
The tests themselves are not very meaningful and are provided for demonstration purposes only.
Also, there are pipelines for building custom Hub/Chrome/Firefox/Edge images, and cleaning old data.

# Project structure:
Most important files, packages and directories are:
- **java/com/andrii/test/tests** - package with test scenarios
- **java/com/andrii/test/pages** - package with PageObjects
- **src/test/java/com/andrii/test/base** - various utility classes
- **src/test/resources/drivers** directory for various browser drivers for various systems
- **src/test/resources/config.properties** - configuration options for startup (although these options are often overridden by command line options)
- **.github/workflows** - GitHub workflows
- **dockerfiles** directory for Dockerfiles used by CI/workflows (image builds)

# Running tests on local machine:
- Import project with your IDE
- Update parameters at **src/test/resources/config.properties** especially **_browser_**. This step is optional and parameters can be overridden by command line options.
- (Optional) Define the scope of the tests you want to run by annotating them with **@Tag("YOUR_TAG")**. By default, tests are run for **@Tag("regression")**, and all tests will be run.
- Run tests with command line. Sample commands: 
```powershell
 # Simplest command: tests will be run according to default tags configuration (tagged with "regression" and not "WIP") and parameters from **config.properties**
 ./gradlew clean test
 
 # only tests annotated with **@Tag("current")** will be run, using MS Edge. Tests will be run in 2 threads.
 ./gradlew clean test -DincludeTags=current -Dbrowser=edge -Dthreads=2
 
 # run specific test.
 ./gradlew clean test --tests '*checkPopupTest'
```
- After run is finished, you can find generated report **build/reports/allure-report/allureReport**. To see it from IDE just use 'open in browser' option on index.html file from this directory. 

# Code quality and formatting
- Checkstyle is used to enforce code formatting and quality. The configuration is provided in `src/test/resources/checkstyle.xml` file.
- PMD is used to enforce code quality and detect potential bugs. The configuration is provided in `src/test/resources/pmd.ruleset.xml` file.
- Task `qualityCheck` runs both checkstyle and pmd checks, and will fail if any of the checks fail.
- Commands for manual run of the quality checks:
```powershell
# Run checkstyle and pmd checks
./gradlew qualityCheck 

# Run quality checks and then tests (if quality checks pass)
./gradlew clean check 
```

# Running tests from GitHub
- Go to Actions and select "Run tests with Selenium Grid".
- Press "Run workflow", set variables for the build and start workflow
- Project uses own images for Selenium hub and browser images based on official ones (mostly for demonstration purposes). You can select specific versions, but easier is just to run on latest.
- After test if finished, Allure Report will be available on jobs page along with some other useful logs (needs ~1min to be prepared)  
- Recent reports are stored in 'gh-pages' branch and could be accessed via URL's like this: https://boltenkovandrii.github.io/selenium_pipeline_cp/reports/20260529_051007_main/