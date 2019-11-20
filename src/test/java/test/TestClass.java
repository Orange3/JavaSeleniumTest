package test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.testng.annotations.*;
import pages.TestForm;

import java.io.File;
import java.io.IOException;

public class TestClass {
    private static ChromeDriverService service;
    public static WebDriver driver;
    TestForm testForm;
    @BeforeSuite
    public static void createAndStartService() throws IOException {
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("chromedriver.exe"))
                .usingAnyFreePort()
                .build();
        service.start();
        driver = new ChromeDriver(service);
        driver.manage().window().maximize();
        //driver.manage().timeouts().implicitlyWait(5000);
    }

    @Test(description = "Test", priority = 1)
    public void simpleTest() {
        driver.get("https://docs.google.com/forms/d/e/1FAIpQLSdqT5F9_qhPDmJ4lfIH7buVkUvjf4LS9ODdqD7PYfVbfFTnpA/viewform");
        testForm = new TestForm(driver);
        testForm.yourNameField.click();
        testForm.yourNameField.sendKeys("asdasdadasd");
    }

    @AfterClass
    public static void createAndStopService() {
        //service.stop();
        driver.quit();
    }
}

