package com.andrii.test.base;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;

public class WebDriverManager {

    public static WebDriver init(final TestConfigurationData data) throws MalformedURLException {
        WebDriver webDriver = null;

        if(data.isUseGrid()) {
            Capabilities capabilities = null;
            // Desired download directory inside the container
            String containerDownloadDir = "/home/seluser/Downloads";

            if("firefox".equals(data.getBrowser())){
                FirefoxOptions options = new FirefoxOptions();
                //To handle browser crashes on container
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addPreference("browser.download.folderList", 2); // 2 means use custom download directory
                options.addPreference("browser.download.dir", containerDownloadDir);
                options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf,application/octet-stream");
                options.addPreference("pdfjs.disabled", true);  // Disable the built-in PDF viewer
                capabilities = options;
            }else if ("chrome".equals(data.getBrowser())) {
                ChromeOptions options = new ChromeOptions();
                //To handle browser crashes on container
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                capabilities = options;
            }else if ("edge".equals(data.getBrowser())) {
                EdgeOptions options = new EdgeOptions();
                //To handle browser crashes on container
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                capabilities = options;
            }

            //TODO: check selenium-hub
//            final RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL("http://docker:4444/wd/hub"), capabilities);
//            final RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
            final RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL("http://selenium-hub:4444/wd/hub"), capabilities);

            remoteDriver.setFileDetector(new LocalFileDetector());
            webDriver = remoteDriver;

        }else {
            if ("firefox".equals(data.getBrowser())) {
                if (System.getProperty("os.name").contains("Windows")) {
                    System.setProperty("webdriver.gecko.driver", "src/test/resources/drivers/win/geckodriver.exe");
                } else if (System.getProperty("os.name").contains("Linux")) {
                    System.setProperty("webdriver.gecko.driver", "src/test/resources/drivers/linux/geckodriver");
                } else {
                    System.setProperty("webdriver.gecko.driver", "src/test/resources/drivers/mac/geckodriver");
                }

                FirefoxOptions options = new FirefoxOptions();

                options.setBinary(TestConfig.getConfiguration().getString("firefoxPath", "C:\\Program Files\\Mozilla Firefox\\firefox.exe"));

                FirefoxProfile profile = new FirefoxProfile();
                profile.setPreference("pdfjs.disabled", true);  // disable the built-in PDF viewer
                options.setProfile(profile);

                webDriver = new FirefoxDriver(options);
            } else if ("chrome".equals(data.getBrowser())) {
                if (System.getProperty("os.name").contains("Windows")) {
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/win/chromedriver.exe");
                } else if (System.getProperty("os.name").contains("Linux")) {
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/linux/chromedriver");
                } else {
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/mac/chromedriver");
                }

                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); //reduces the chance that website will detect that browser is managed by Selenium (and will block access). TBH probably not needed now, but let it be.
                options.addArguments("--remote-allow-origins=*"); //to handle possible issues with  Cross-Origin Resource Sharing (CORS) policies in browser. TBH probably not needed now, but let it be.
                options.addArguments("--disable-search-engine-choice-screen"); //to disable search engine suggestion on the browser start
                webDriver = new ChromeDriver(options);
            } else if ("edge".equals(data.getBrowser())) {
                if (System.getProperty("os.name").contains("Windows")) {
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/win/msedgedriver.exe");
                } else if (System.getProperty("os.name").contains("Linux")) {
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/linux/msedgedriver");
                } else {
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/mac/msedgedriver");
                }

                EdgeOptions options = new EdgeOptions();
                webDriver = new EdgeDriver(options);
            }
        }

        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));
        return webDriver;
    }

}
