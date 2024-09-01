# What is this?
Just a small demo project to show how some tools work together:
- Java (Java 17 to be exact, although it can be updated relatively easily to use other versions)
- Junit5
- Selenium 
- Selenium hub (only set up for pipeline)
- Allure report
- Gitlab CI\CD pipelines

### But what does it do?
With this project you can run some of the provided tests on https://www.wikipedia.org/ both on your local machine and on GitLab.
The tests themselves are not very meaningful and are provided for demonstration purposes only.

# Project structure:
Most important files, packages and directories are:
- **java/com/andrii/test/tests** - package with test scenarios
- **java/com/andrii/test/pages** - package with PageObjects
- **src/test/java/com/andrii/test/base** - various utility classes
- **src/test/resources/drivers** directory for various browser drivers for various systems
- **src/test/resources/config.properties** - configuration options for startup (although these options are often overridden by command line options)
- **.gitlab-ci.*.yml** - pipeline configuration for the GitLab
- **dockerfiles** directory for various docker files used in GitLab pipeline

# Running tests on local machine:
- Import project with your IDE
- Update browser driver(s) at **src/test/resources/drivers** to match your browser(s) version. Drivers from git are probably outdated even if present.
- Update parameters at **src/test/resources/config.properties** especially **_browser_** and _**firefoxPath**_ (if you use Firefox). This step is optional and parameters can be overridden by command line options.
- (Optional) Define the scope of the tests you want to run by annotating them with **@Tag("YOUR_TAG")**. By default, tests are run for **@Tag("regression")**, and all tests will be run.
- Run tests with command line. Sample commands: 
- [ ] **./gradlew clean test allureReport** - simplest command: all tests will be run according to parameters from **config.properties**
- [ ] **./gradlew clean test -DincludeTags=current -Dbrowser=edge -Dthreads=2 allureReport** - only tests annotated with **@Tag("current")** will be run, using MS Edge. Tests will be run in 2 threads.
- After run is finished, you can find generated report **build/reports/allure-report/allureReport**. To see it from IDE just use 'open in browser' option on index.html file from this directory. 

# Running tests from GitLab
- Go to Build->Pipelines and press "Run pipeline" button.
- Set variables for the build and press "Run pipeline"
- If you are running tests for the first time, you need to Docker images (after creating the images, they are saved 
in the local registry under Deploy->Container Registry and will be used for the next runs). So run all jobs under "build" section.
- When all needed images are ready, run "tests-run" job, which actually starts the tests.
- To see test results after job is finished, open job, click "Browse" and then "public". "Downloads" directory contains 
files, downloaded during the test run and "allureReport" - report itself  (just click index.html to view it).
