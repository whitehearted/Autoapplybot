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

//package com.Job.applybot.Driver;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//
//public class DriverFactory {
//
//    /**
//     * MINIMIZE = true  → browser stays in taskbar, never pops up
//     * MINIMIZE = false → visible browser (for debugging only)
//     *
//     * We do NOT use --headless because Naukri detects and blocks it.
//     * Minimized + real Chrome is fully undetectable.
//     */
//    private static final boolean MINIMIZE = true;
//
//    public static WebDriver GetWebDriver() {
//        WebDriverManager.chromedriver().setup();
//
//        ChromeOptions options = new ChromeOptions();
//
//        // ── Anti-detection ────────────────────────────────────────────────────
//        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
//        options.setExperimentalOption("useAutomationExtension", false);
//
//        // ── Performance ───────────────────────────────────────────────────────
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--disable-gpu");
//        options.addArguments("--disable-infobars");
//        options.addArguments("--disable-notifications");
//
//        // ── Window size — real resolution so Naukri renders correctly ─────────
//        options.addArguments("--window-size=1920,1080");
//
//        // ── Start minimized ───────────────────────────────────────────────────
//        if (MINIMIZE) {
//            options.addArguments("--start-minimized");
//        }
//
//        ChromeDriver driver = new ChromeDriver(options);
//
//        // Remove navigator.webdriver flag
//        try {
//            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
//                    "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
//            );
//        } catch (Exception ignored) {}
//
//        return driver;
//    }
//}