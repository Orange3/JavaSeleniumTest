package test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.testng.Assert;
import org.testng.annotations.*;

import pages.FormPage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class TestClass {
    private static ChromeDriverService service;
    public static WebDriver driver;
    FormPage formPage;

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
        driver.get("https://goo.gl/forms/t16Uov7ZHXCrB2ZE2");
        formPage = new FormPage(driver);
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
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.EMAIL).isDisplayed()); //Error is expected, test is failed. Message should be displayed, but it isn't

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
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed()); //test is failed. Incorrect date is present. Error Message should be displayed, but it isn't

        month = "31"; day="12"; year="012345";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed()); //test is failed. Incorrect date is present. Error Message should be displayed, but it isn't

        month = "as"; day="ds"; year="012345";
        formPage.enterAge(month,day,year);
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.AGE).isDisplayed()); //test is failed. Incorrect date is present. Error Message should be displayed, but it isn't
    }

    @Test(description = "Check name form for a correctness of an entered data", groups = "form", priority = 3)
    public void nameFieldTest(){
        //Check name field for valid data. It shouldn't include special symbols. Text length should be less than 20
        formPage.enterYourNameField("Arley Gilmore");
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed());

        formPage.enterYourNameField("Cairro Worthington"); //string.length = 18
        Assert.assertEquals(false, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed()); //test is failed. Incorrect message length is set.

        //Check invalid data
        formPage.enterYourNameField("Hubert Blaine Wolfeschlegelsteinhausenbergerdorff");
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed());

        formPage.enterYourNameField("");
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed());

        formPage.enterYourNameField("123#$%asd");
        Assert.assertEquals(true, formPage.getErrorMessage(FormPage.FormType.NAME).isDisplayed()); //test is failed. Special symbols are not detected.
    }
    @Test(description = "Check name form for a correctness of an entered data", groups = "form", priority = 4)
    public void moodFieldsTest(){

        formPage.getCheckbox(0).click();
        Assert.assertEquals(true, formPage.getCheckbox(0).isSelected());
    }

    @AfterClass
    public static void createAndStopService() {
        //service.stop();
        driver.quit();
    }
}

