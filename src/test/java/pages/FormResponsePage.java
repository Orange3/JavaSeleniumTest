package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class FormResponsePage {
    WebDriver driver;

    public FormResponsePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "div[class='freebirdFormviewerViewFormContentWrapper'] div[class='freebirdFormviewerViewResponseConfirmationMessage']")
    private WebElement confirmMessage;

    @FindBy(xpath = "//div[@class='freebirdFormviewerViewResponseLinksContainer']//a")
    private WebElement sentOneMoreLink;

    public WebElement getConfirmMessage(){
        return confirmMessage;
    }

    public WebElement getSentOneMoreLink(){
        return sentOneMoreLink;
    }
}
