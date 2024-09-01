package com.andrii.test.pages;

import com.andrii.test.base.TestConfigurationData;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public abstract class PageBase {
    protected TestConfigurationData data;

    public PageBase(TestConfigurationData data){
        this.data = data;
        PageFactory.initElements(data.getDriver(), this);
        waitForPageReady();
        waitForLoadingHook();
    }

    public void scrollToElement(WebElement element){
        ((JavascriptExecutor) data.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public boolean isElementPresent(final WebElement el) {
        try {
            el.getTagName(); //just attempting to access element
            return true;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public void waitTillElementDisappear(final WebElement el) {
        waitTillElementDisappear(el, new WebDriverWait(data.getDriver(), Duration.ofSeconds(10)));
    }

    public void waitTillElementDisappear(final WebElement el, WebDriverWait wait) {
        wait.until((ExpectedCondition<Boolean>) driver -> {
            try {
                el.getTagName(); //just attempting to access element
                return false;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return true;
            }
        });
    }

    public void waitTilElementClickable(WebElement element) {
        waitTilElementClickable(element, Duration.ofSeconds(2));
    }

    public void waitTilElementClickable(WebElement element, Duration duration) {
        WebDriverWait webDriverWait = new WebDriverWait(data.getDriver(), duration);
        webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public abstract void waitForLoadingHook();

    public void waitForPageReady(){
        new WebDriverWait(data.getDriver(), Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
        );
    }

    public void waitAndClick(final WebElement el) {
        try {
            waitTilElementClickable(el);
            el.click();
        } catch (WebDriverException e) {
            data.getEventListener().makeScreenshot(data.getDriver(), "Cannot click on element:" + el, e);
            throw e;
        }
    }

    public void waitAndSendKeys(WebElement el, String text) {
        try{
            if (text != null) {
                waitTilElementClickable(el);
                el.clear();
                el.sendKeys(text);
            }
        }catch(StaleElementReferenceException se){
            data.getEventListener().makeScreenshot(data.getDriver(), "StaleElementReferenceException for "+el);
            throw se;
        }catch(TimeoutException te){
            data.getEventListener().makeScreenshot(data.getDriver(), "TimeoutException for "+el);
            throw te;
        }
    }

}
