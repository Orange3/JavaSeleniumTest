package test;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import pages.FormPage;
import pages.FormResponsePage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestClass {
    private static ChromeDriverService service;
    public static WebDriver driver;
    public static WebDriverWait wait;
    FormPage formPage;
    FormResponsePage formResponsePage;

    @BeforeSuite
    public void createAndStartService() throws IOException {
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("chromedriver.exe"))
                .usingAnyFreePort()
                .build();
        service.start();
        driver = new ChromeDriver(service);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, 10);
        driver.get("https://goo.gl/forms/t16Uov7ZHXCrB2ZE2");
        formPage = new FormPage(driver);
        formResponsePage = new FormResponsePage(driver);
    }

    @Test(description = "Check Email form for a correctness of an entered data", groups = "form", priority = 1)
    public void emailFieldTest() {
        resetFields();
        //Check that error message doesn't appear with valid data
        formPage.enterEmail("user_name@domain.com");
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed(), false);

        formPage.enterEmail("username@yahoo.corporate.in");
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed(), false);

        //Check that error message appears with invalid data
        formPage.enterEmail("username@yahoo..com");
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed(), true);

        formPage.enterEmail(".username@yahoo.com");
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed(), true); //test will be failed. Message should be displayed but it isn't
        resetFields();
    }

    @Test(description = "Check Age form for a correctness of an entered data", groups = "form", priority = 2)
    public void ageFieldTest() throws InterruptedException {
        resetFields();
        //Check that error message doesn't appear with valid data. Valid data should satisfy conditions:
        //only numbers are acceptable, age should starts from 19XX and ends with 20XX, month <12, days<31
        String month = "12", day = "30", year = "2000";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(year+"-"+month+"-"+day, formPage.getAgeFieldEnteredData());
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed(), false);

        month = "3"; day = "30"; year = "1972";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(formPage.getAgeFieldEnteredData(),year+"-0"+month+"-"+day);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed(), false);

        //Check that error message appears with invalid data
        month = "13"; day = "18"; year="1953";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(formPage.getAgeFieldEnteredData(),year+"-"+12+"-"+day);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed(), false);

        month = "2"; day = "30"; year="1999";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed(), true); //test will be failed. Incorrect date is present. Error Message should be displayed but it isn't

        month = "31"; day="12"; year="012345";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed(),true); //test will be failed. Incorrect date is present. Error Message should be displayed but it isn't

        month = "as"; day="ds"; year="012345";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed(), true);
    }

    @Test(description = "Check name form for a correctness of an entered data", groups = "form", priority = 3)
    public void nameFieldTest(){
        resetFields();
        //Check name field for valid data. It shouldn't include special symbols. Text length should be less than 20
        formPage.enterYourNameField("Arley Gilmore");
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed(), false);

        formPage.enterYourNameField("Cairro Worthington"); //string.length = 18
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed(), false); //test will be failed. Incorrect message length is set.

        //Check invalid data
        formPage.enterYourNameField("Hubert Blaine Wolfeschlegelsteinhausenbergerdorff");
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed(), true);

        formPage.enterYourNameField("");
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed(), true);

        formPage.enterYourNameField("123#$%asd");
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed(), true); //test is failed. Special symbols are not detected.
    }

    @Test(description = "Verify a correctness of checked options in the Mood form", groups = "form", priority = 4)
    public void moodFieldsTest(){
        resetFields();
        //Valid data
        //Check 1 selected option; Error message shouldn't be displayed
        formPage.getCheckBoxesList().get(0).click();
        Assert.assertEquals(formPage.countCheckedOptions(formPage.getCheckBoxesList()), 1);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed(), false);

        formPage.resetCheckBoxes(formPage.getCheckBoxesList());

        //Check "another" selected option and empty text field. Error message should be displayed
        formPage.getCheckBoxesList().get(4).click();
        Assert.assertEquals( formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed(), true);
        Assert.assertEquals(formPage.countCheckedOptions(formPage.getCheckBoxesList()), 1);

        //Check filled text-form and error message
        formPage.enterTextIntoMoodOtherOption("Great!");
        Assert.assertEquals(formPage.countCheckedOptions(formPage.getCheckBoxesList()), 1);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed(), false);

        //Invalid data
        //Check appearance of error for non selected options
        formPage.resetCheckBoxes(formPage.getCheckBoxesList());
        Assert.assertEquals(formPage.countCheckedOptions(formPage.getCheckBoxesList()), 0);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed(), true);

        //Check several selected options
        formPage.getCheckBoxesList().get(1).click();
        formPage.getCheckBoxesList().get(3).click();
        Assert.assertEquals(formPage.countCheckedOptions(formPage.getCheckBoxesList()), 2);
        Assert.assertEquals(formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed(), true);//Test will be failed. Error message isn't displayed.
                                                                                                            // Data can be sent. Radio button should be used instead of checkboxes
    }

    @Test(description = "Verify that a data can be sent", groups = "form", priority = 5)
    public void submitDataTest() throws InterruptedException {
        resetFields();
        //Submit with empty data
        formPage.clickSubmit();
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed());
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed());
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed());
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed());

        //Submit with valid data
        formPage.enterEmail("user_name@domain.com");
        formPage.enterAge("11","20","1995");
        formPage.enterYourNameField("Arley Gilmore");
        formPage.getCheckBoxesList().get(0).click();
        formPage.clickSubmit();

        wait.until(ExpectedConditions.visibilityOf(formResponsePage.getConfirmMessage()));

        //Check sent one more form
        Assert.assertEquals(formResponsePage.getSentOneMoreLink().isDisplayed(), true);
        formResponsePage.getSentOneMoreLink().click();
        Assert.assertEquals(formPage.getEmailField().isDisplayed(), true);
    }

    @AfterClass
    public static void createAndStopService() {
        //service.stop();
        driver.quit();
    }

    public void resetFields(){
        formPage.resetCheckBoxes(formPage.getCheckBoxesList());
        formPage.getAgeField().sendKeys(Keys.DELETE,
                Keys.ARROW_RIGHT, Keys.DELETE,
                Keys.ARROW_RIGHT, Keys.DELETE);
        formPage.getEmailField().clear();
        formPage.getYourNameField().clear();
        formPage.getMoodOtherOptionField().clear();
    }
}

