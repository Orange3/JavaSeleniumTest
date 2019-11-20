package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class TestForm {
    WebDriver driver;

    @FindBy(xpath = "//input[@name='emailAddress']")
    private WebElement emailField;

    @FindBy(xpath = "//input[@name='entry.260210294']")
    public  WebElement yourNameField;

    public  TestForm(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
}
