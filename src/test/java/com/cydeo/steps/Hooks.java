package com.cydeo.steps;

import com.cydeo.utilities.BrowserUtils;
import com.cydeo.utilities.ConfigurationReader;
import com.cydeo.utilities.DataBaseUtil;
import com.cydeo.utilities.Driver;
import io.cucumber.java.*;
import io.restassured.RestAssured;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.time.Duration;

public class Hooks {

    @Before("@ui")
    public void setUp() {
        Driver.getDriver().manage().window().maximize();
        Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        Driver.getDriver().get(ConfigurationReader.getProperty("library_url"));
    }

    @After("@ui")
    public void tearDown(Scenario scenario) {

        if (scenario.isFailed()) {
            final byte[] screenshot = ((TakesScreenshot) Driver.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "screenshot");
        }
        Driver.closeDriver();
    }
    @Before("@db")
    public void dbHook() {
        DataBaseUtil.connectToDatabase();
    }

    @After("@db")
    public void afterdbHook() {
        DataBaseUtil.closeConnection();
        System.out.println("Successfully Closed database");
    }

    @Before()
    public void setBaseURI() {
        RestAssured.baseURI = ConfigurationReader.getProperty("base_url");
    }

//    @After()
//    public void endScenario(Scenario scenario) {
//        System.out.println("Test Result for " + scenario.getName() + " " + scenario.getStatus());
//    }



}
