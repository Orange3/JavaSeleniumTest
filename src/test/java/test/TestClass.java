package test;

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
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, 10);
        driver.get("https://goo.gl/forms/t16Uov7ZHXCrB2ZE2");
        formPage = new FormPage(driver);
        formResponsePage = new FormResponsePage(driver);
    }

    @Test(description = "Check email form for a correctness of an entered data", groups = "form", priority = 1)
    public void emailFieldTest() {
        //Check that error message doesn't appear with valid data
        formPage.enterEmail("user_name@domain.com");
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed());

        formPage.enterEmail("username@yahoo.corporate.in");
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed());

        //Check that error message appears with invalid data
        formPage.enterEmail("username@yahoo..com");
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed());

        formPage.enterEmail(".username@yahoo.com");
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed()); //test will be failed. Message should be displayed but it isn't
    }

    @Test(description = "Check age form for a correctness of an entered data", groups = "form", priority = 2)
    public void ageFieldTest() {
        //Check that error message doesn't appear with valid data. Valid data should satisfy conditions:
        //only numbers are acceptable, age should starts from 19XX and ends with 20XX, month <12, days<31
        String month = "12", day = "30", year = "2000";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(year+"-"+month+"-"+day, formPage.getSetAge());
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed());

        month = "3"; day = "30"; year = "1972";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(year+"-0"+month+"-"+day, formPage.getSetAge());
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed());

        //Check that error message appears with invalid data
        month = "13"; day = "18"; year="1953";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(year+"-"+12+"-"+day, formPage.getSetAge());
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed());

        month = "2"; day = "30"; year="1999";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed()); //test will be failed. Incorrect date is present. Error Message should be displayed but it isn't

        month = "31"; day="12"; year="012345";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed()); //test will be failed. Incorrect date is present. Error Message should be displayed but it isn't

        month = "as"; day="ds"; year="012345";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed()); //test will be failed. Incorrect date is present. Error Message should be displayed but it isn't
    }

    @Test(description = "Check name form for a correctness of an entered data", groups = "form", priority = 3)
    public void nameFieldTest(){
        //Check name field for valid data. It shouldn't include special symbols. Text length should be less than 20
        formPage.enterYourNameField("Arley Gilmore");
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed());

        formPage.enterYourNameField("Cairro Worthington"); //string.length = 18
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed()); //test will be failed. Incorrect message length is set.

        //Check invalid data
        formPage.enterYourNameField("Hubert Blaine Wolfeschlegelsteinhausenbergerdorff");
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed());

        formPage.enterYourNameField("");
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed());

        formPage.enterYourNameField("123#$%asd");
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed()); //test is failed. Special symbols are not detected.
    }

    @Test(description = "Verify a correctness of checked options in the mood form", groups = "form", priority = 4)
    public void moodFieldsTest(){
        //Valid data
        //Check 1 selected option; Error message shouldn't be displayed
        formPage.getCheckBoxesList().get(0).click();
        Assert.assertEquals(formPage.countCheckedOptions(formPage.getCheckBoxesList()), 1);
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed());

        formPage.resetCheckBoxes(formPage.getCheckBoxesList());

        //Check "another" selected option and empty text field. Error message should be displayed
        formPage.getCheckBoxesList().get(4).click();
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed());
        //Check filled text-form and error message
        formPage.enterTextIntoMoodOtherOption("Great!");
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed());

        //Invalid data
        //Check appearance of error for not selected options
        formPage.resetCheckBoxes(formPage.getCheckBoxesList());
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed());

        //Check several selected options
        formPage.getCheckBoxesList().get(1).click();
        formPage.getCheckBoxesList().get(3).click();
        //Test will be failed. Error message is not displayed. Data can be sent. Radio button should be used instead of checkboxes
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed());
    }

    @Test(description = "Verify that a data can be sent", groups = "form", priority = 5)
    public void submitDataTest(){
        //Submit with empty data
//        driver.get("https://goo.gl/forms/t16Uov7ZHXCrB2ZE2");
//        formPage.clickSubmit();
//        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed());
//        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed());
//        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed());
//        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.MOOD).isDisplayed());

        //Submit with valid data
        formPage.enterEmail("user_name@domain.com");
        formPage.enterAge("11","20","1995");
        formPage.enterYourNameField("Arley Gilmore");
        formPage.getCheckBoxesList().get(0).click();
        formPage.clickSubmit();
        wait.until(ExpectedConditions.visibilityOf(formResponsePage.getSendAgainLink()));

    }

    @AfterClass
    public static void createAndStopService() {
        //service.stop();
        driver.quit();
    }


}

