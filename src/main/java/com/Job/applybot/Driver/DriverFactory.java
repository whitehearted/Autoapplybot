package com.Job.applybot.Driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverFactory {
    public static WebDriver GetWebDriver(){
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
    }
}
