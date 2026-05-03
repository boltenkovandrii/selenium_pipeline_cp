# What is this?
Just a small demo project to show how some tools work together:
- Java 
- Junit5
- Selenium 
- Selenium hub (only set up for pipeline)
- Allure report
- GitHub Actions (ported from GitLab using Copilot)

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
- Update browser driver(s) at **src/test/resources/drivers** to match your browser(s) version. Drivers from git are probably outdated even if present.
- Update parameters at **src/test/resources/config.properties** especially **_browser_** and _**firefoxPath**_ (if you use Firefox). This step is optional and parameters can be overridden by command line options.
- (Optional) Define the scope of the tests you want to run by annotating them with **@Tag("YOUR_TAG")**. By default, tests are run for **@Tag("regression")**, and all tests will be run.
- Run tests with command line. Sample commands: 
- [ ] **./gradlew clean test** - simplest command: all tests will be run according to parameters from **config.properties**
- [ ] **./gradlew clean test -DincludeTags=current -Dbrowser=edge -Dthreads=2** - only tests annotated with **@Tag("current")** will be run, using MS Edge. Tests will be run in 2 threads.
- After run is finished, you can find generated report **build/reports/allure-report/allureReport**. To see it from IDE just use 'open in browser' option on index.html file from this directory. 

# Running tests from GitHub
- Go to Actions and select "Run tests with Selenium Grid".
- Press "Run workflow", set variables for the build and start workflow
- Project uses own images for Selenium hub and browser images based on official ones (mostly for demonstration purposes). You can select specific versions, but easier is just to run on latest.
- After test if finished, Allure Report will be available on jobs page along with some other useful logs (needs ~1min to be prepared)  

