package com.andrii.test.pages.commons;

import com.andrii.test.base.TestConfigurationData;
import com.andrii.test.pages.PageBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CommonsSearchResultPage extends PageBase {

    @FindBy(css="section[id ='cdx-other-3'] button[aria-labelledby*='filemime__textbox']")
    private WebElement otherMediaFileButton;

    @FindBy(xpath="//button/span[text()='Other Media']/..")
    private  WebElement otherMediaTab;

    @FindBy(css="section[id ='cdx-other-3'] div.sdms-search-results h3 a")
    private List<WebElement> otherMediaResultList;

    @FindBy(css="div.sdms-search-results__pending")
    private WebElement loader;

    public CommonsSearchResultPage(TestConfigurationData data) {
        super(data);
    }

    public void waitForLoadingHook(){
        //implement if needed
    }

    public CommonsSearchResultPage selectFileType(String value){
        waitAndClick(otherMediaFileButton);
        waitAndClick(data.getDriver().findElement(By.xpath("//ul/li[text()='"+value+"']")));
        waitForPageReady();
        waitTillElementDisappear(loader);
        return new CommonsSearchResultPage(data);
    }
    
    public CommonsSearchResultPage openOtherMedia(){
        waitTilElementClickable(otherMediaTab);
        waitAndClick(otherMediaTab);
        return new CommonsSearchResultPage(data);
    }


    public CommonsFilePage openFirstResult(){
        waitAndClick(otherMediaResultList.get(0));
        return new CommonsFilePage(data);
    }

}
