package com.andrii.test.pages.commons;

import com.andrii.test.base.TestConfigurationData;
import com.andrii.test.pages.PageBase;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class CommonsMainPage extends PageBase {

    @FindBy(id="mw-AnonymousI18N-picker")
    private WebElement languageSelect;

    @FindBy(id="searchInput")
    private  WebElement searchInput;

    @FindBy(id="searchButton")
    private  WebElement searchButton;


    public CommonsMainPage(TestConfigurationData data) {
        super(data);
    }

    public void waitForLoadingHook(){
        waitTilElementClickable(languageSelect);
    }


    public CommonsMainPage selectLanguage(String language){
        Select searchLanguageSelect = new Select(this.languageSelect);
        searchLanguageSelect.selectByVisibleText(language);
        return new CommonsMainPage(data);
    }

    public CommonsSearchResultPage performSearch(String searchPhrase){
        waitAndSendKeys(searchInput, searchPhrase);
        waitAndClick(searchButton);
        return new CommonsSearchResultPage(data);
    }

}
