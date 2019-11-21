package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;


public class FormPage {
    WebDriver driver;

    public enum FormType {
        EMAIL, AGE, NAME, MOOD
    }

    public FormPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[@class='freebirdFormviewerViewHeaderHeader exportHeader']")
    private WebElement titleField;

    @FindBy(xpath = "//input[@name='emailAddress']")
    private WebElement emailField;

    @FindBy(css = "div[class='freebirdFormviewerViewItemsDateInputsContainer'] input[type='date']")
    private WebElement ageField;

    @FindBy(xpath = "//input[@name='entry.260210294']")
    private WebElement yourNameField;

    @FindBy(css = "div[role='list'] div[role='checkbox']")
    private List<WebElement> checkBoxes;

    @FindBy(css = "input[class='quantumWizTextinputSimpleinputInput exportInput']")
    private WebElement moodOtherOptionField;

    @FindBy(css = "div[class='freebirdFormviewerViewItemsItemErrorMessage']" )
    private List<WebElement> errorMessages; //error messages for each field

    @FindBy(css = "div[class='freebirdFormviewerViewNavigationButtonsAndProgress']  div[role='button']")
    private WebElement submitButton;

    //filling email field
    public void enterEmail(String email){
        emailField.clear();
        emailField.click();
        emailField.sendKeys(email);
        titleField.click();
    }

    //filling age field
    public void enterAge(String month, String day, String year){
        ageField.click();
        ageField.click();
        ageField.clear();
        ageField.sendKeys(month);
        ageField.sendKeys(day);
        ageField.sendKeys(year);
        titleField.click();
    }

    //return value from age field
    public String getSetAge(){
        return ageField.getAttribute("data-initial-value");
    }

    public void enterYourNameField(String name){
        yourNameField.click();
        yourNameField.clear();
        yourNameField.sendKeys(name);
    }

    public List<WebElement> getCheckBoxesList(){
        return checkBoxes;
    }

    //Check that checkbox/radio button is checked
    public boolean isOptionSelected(WebElement element){
        if(element.getAttribute("aria-checked").equals("true"))
            return true;
        else return false;
    }

    public void enterTextIntoMoodOtherOption(String text){
        moodOtherOptionField.click();
        moodOtherOptionField.clear();
        moodOtherOptionField.sendKeys(text);
    }

    //Uncheck options
    public void resetCheckBoxes(List<WebElement> options){
        for(int i=0; i<options.size(); i++){
            if (isOptionSelected(options.get(i)))
                options.get(i).click();
        }
    }

    //Method for calculating selected checkboxes
    public int countCheckedOptions(List<WebElement> options){
        int checkedOptionsCounter = 0;
        for(int i=0; i<options.size(); i++){
            if (isOptionSelected(options.get(i))) checkedOptionsCounter++;
        }
        return checkedOptionsCounter;
    }

    public void clickSubmit(){
        submitButton.click();
    }
    //Return of error message for specified field
    public WebElement getErrorMessage(FormType field){
        switch (field){
            case EMAIL: return errorMessages.get(0);
            case AGE: return errorMessages.get(1);
            case NAME: return errorMessages.get(2);
            case MOOD: return errorMessages.get(3);
            default: return null;
        }
    }
}
