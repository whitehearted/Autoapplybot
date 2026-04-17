//package com.Job.applybot.bot;
//
//import com.Job.applybot.Driver.DriverFactory;
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.ui.*;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Bot {
//
//    public void searchjob(String url) throws InterruptedException {
//        WebDriver driver = DriverFactory.GetWebDriver();
//        login(driver);
//        driver.get(url);
//
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        String mainWindow = driver.getWindowHandle();
//        int page = 1;
//
//        while (true) {
//            System.out.println("===== PAGE " + page + " =====");
//
//            // Scroll to load all job cards
//            for (int i = 0; i < 3; i++) {
//                js.executeScript("window.scrollBy(0,1000)");
//                Thread.sleep(1000);
//            }
//
//            // Collect all job hrefs before looping to avoid StaleElementReferenceException
//            List<WebElement> jobEls = driver.findElements(
//                    By.xpath("//a[contains(@href,'job-listings') and contains(@class,'title')]"));
//
//            if (jobEls.isEmpty()) {
//                System.out.println("No jobs found on page " + page + ".");
//                break;
//            }
//
//            List<String> hrefs  = new ArrayList<>();
//            List<String> titles = new ArrayList<>();
//            for (WebElement el : jobEls) {
//                try {
//                    hrefs.add(el.getAttribute("href"));
//                    titles.add(el.getText().toLowerCase());
//                } catch (Exception ignored) {}
//            }
//            System.out.println("Found " + hrefs.size() + " jobs on page " + page);
//
//            // Process each job
//            for (int i = 0; i < hrefs.size(); i++) {
//                String title = titles.get(i);
//                String link  = hrefs.get(i);
//
//                if (!(title.contains("java") || title.contains("fresher") || title.contains("software"))) {
//                    System.out.println("Skipping (title filter): " + title);
//                    continue;
//                }
//
//                System.out.println("Processing: " + title);
//
//                try {
//                    js.executeScript("window.open(arguments[0], '_blank');", link);
//                    Thread.sleep(3000);
//
//                    List<String> tabs = new ArrayList<>(driver.getWindowHandles());
//                    driver.switchTo().window(tabs.get(tabs.size() - 1));
//
//                    applyjob(driver);
//
//                } catch (InvalidArgumentException fatal) {
//                    System.out.println("FATAL: Session died — stopping bot.");
//                    return;
//                } catch (Exception e) {
//                    System.out.println("Error on [" + title + "]: " + e.getMessage());
//                } finally {
//                    // Close all extra tabs, then switch back to main window
//                    try {
//                        for (String tab : new ArrayList<>(driver.getWindowHandles())) {
//                            if (!tab.equals(mainWindow)) {
//                                driver.switchTo().window(tab);
//                                driver.close();
//                            }
//                        }
//                        driver.switchTo().window(mainWindow);
//                    } catch (Exception closeEx) {
//                        System.out.println("Tab cleanup warning: " + closeEx.getMessage());
//                        try { driver.switchTo().window(mainWindow); } catch (Exception ignored) {}
//                    }
//                    Thread.sleep(1500);
//                }
//            }
//
//            // ── Pagination ────────────────────────────────────────────────────────
//            // Naukri uses several pagination patterns — try all of them
//            if (!goToNextPage(driver, js, page)) {
//                System.out.println("No more pages.");
//                break;
//            }
//            page++;
//            Thread.sleep(3000);
//        }
//
//        System.out.println("===== Bot finished =====");
//    }
//
//    /**
//     * Tries multiple pagination strategies and returns true if navigation succeeded.
//     *
//     * Naukri pagination HTML patterns observed:
//     *   Pattern A: <a class="pagination-btn">2</a>          — text is page number
//     *   Pattern B: <a data-page="2">2</a>                   — data-page attribute
//     *   Pattern C: <li class="pagination">...<a>2</a></li>  — inside li.pagination
//     *   Pattern D: <a class="btn-next">Next</a>             — generic Next button
//     */
//    private boolean goToNextPage(WebDriver driver, JavascriptExecutor js, int currentPage) {
//        int nextPage = currentPage + 1;
//
//        // Strategy A: exact text match for next page number (most common)
//        try {
//            WebElement btn = driver.findElement(By.xpath(
//                    "//a[normalize-space(text())='" + nextPage + "']"));
//            js.executeScript("arguments[0].click();", btn);
//            System.out.println("Pagination A: clicked page " + nextPage);
//            return true;
//        } catch (NoSuchElementException ignored) {}
//
//        // Strategy B: data-page attribute
//        try {
//            WebElement btn = driver.findElement(By.xpath(
//                    "//a[@data-page='" + nextPage + "']"));
//            js.executeScript("arguments[0].click();", btn);
//            System.out.println("Pagination B (data-page): clicked page " + nextPage);
//            return true;
//        } catch (NoSuchElementException ignored) {}
//
//        // Strategy C: inside pagination list items
//        try {
//            WebElement btn = driver.findElement(By.xpath(
//                    "//*[contains(@class,'pagination')]//a[normalize-space(text())='" + nextPage + "']"));
//            js.executeScript("arguments[0].click();", btn);
//            System.out.println("Pagination C (list): clicked page " + nextPage);
//            return true;
//        } catch (NoSuchElementException ignored) {}
//
//        // Strategy D: "Next" / ">" button
//        try {
//            WebElement btn = driver.findElement(By.xpath(
//                    "//a[contains(@class,'next') or normalize-space(text())='Next' " +
//                            "or normalize-space(text())='>' or @aria-label='Next page']"));
//            js.executeScript("arguments[0].click();", btn);
//            System.out.println("Pagination D (Next btn): clicked");
//            return true;
//        } catch (NoSuchElementException ignored) {}
//
//        // Strategy E: span or button with page number
//        try {
//            WebElement btn = driver.findElement(By.xpath(
//                    "//*[self::button or self::span][normalize-space(text())='" + nextPage + "']"));
//            js.executeScript("arguments[0].click();", btn);
//            System.out.println("Pagination E (span/button): clicked page " + nextPage);
//            return true;
//        } catch (NoSuchElementException ignored) {}
//
//        // Debug: log what pagination elements exist
//        System.out.println("Pagination debug — all page links found:");
//        List<WebElement> allPageLinks = driver.findElements(By.xpath(
//                "//*[contains(@class,'pagination') or contains(@class,'page')]//a | " +
//                        "//a[@data-page]"));
//        for (WebElement el : allPageLinks) {
//            System.out.println("  page link: text=[" + el.getText() + "] " +
//                    "data-page=[" + safeAttr(el,"data-page") + "] class=[" + safeAttr(el,"class") + "]");
//        }
//
//        return false;
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────────
//    public void applyjob(WebDriver driver) {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//        try {
//            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
//                    By.xpath("//button[contains(.,'Apply')]")));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
//            System.out.println("Apply clicked.");
//            Thread.sleep(6000);
//
//            boolean isChatBot = !driver.findElements(By.cssSelector(".chatbot_Drawer")).isEmpty();
//            boolean isPopup   = !driver.findElements(
//                    By.cssSelector(".apply-layer-container,.question-title")).isEmpty();
//
//            if (isChatBot)     { System.out.println("-> ChatBot flow"); ChatBotHandler.handle(driver); }
//            else if (isPopup)  { System.out.println("-> Popup flow");   PopupHandler.handle(driver);   }
//            else                 System.out.println("-> Direct apply (no questions).");
//
//        } catch (TimeoutException e) {
//            System.out.println("Apply button not found or already applied.");
//        } catch (Exception e) {
//            System.out.println("applyjob error: " + e.getMessage());
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────────
//    private void login(WebDriver driver) throws InterruptedException {
//        driver.get("https://www.naukri.com/nlogin/login");
//        Thread.sleep(2000);
//        driver.findElement(By.id("usernameField")).sendKeys("marimuthu26052000@gmail.com");
//        driver.findElement(By.id("passwordField")).sendKeys("Mari@1234");
//        driver.findElement(By.xpath("//button[text()='Login']")).click();
//        Thread.sleep(4000);
//        System.out.println("Login done.");
//    }
//
//    private String safeAttr(WebElement el, String attr) {
//        try { String v = el.getAttribute(attr); return v == null ? "" : v; }
//        catch (Exception e) { return ""; }
//    }
//}

//package com.Job.applybot.bot;
//
//import com.Job.applybot.Driver.DriverFactory;
//import com.Job.applybot.model.ApplicationTracker;
//import com.Job.applybot.Service.ApplicationResult;
//import com.Job.applybot.Service.ApplicationResult.Status;
//import com.Job.applybot.model.UserProfile;
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.ui.*;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//public class Bot {
//
//    public void searchjob(String url, UserProfile profile) throws InterruptedException {
//        WebDriver driver = DriverFactory.GetWebDriver();
//        ApplicationTracker tracker = new ApplicationTracker(profile.getFullName());
//
//        login(driver, profile);
//        driver.get(url);
//
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        String mainWindow     = driver.getWindowHandle();
//        int page              = 1;
//        int maxPages          = profile.getMaxPages() > 0 ? profile.getMaxPages() : 10;
//
//        while (page <= maxPages) {
//            System.out.println("===== PAGE " + page + " =====");
//            for (int i = 0; i < 3; i++) {
//                js.executeScript("window.scrollBy(0,1000)");
//                Thread.sleep(1000);
//            }
//
//            List<WebElement> jobEls = driver.findElements(
//                    By.xpath("//a[contains(@href,'job-listings') and contains(@class,'title')]"));
//            if (jobEls.isEmpty()) { System.out.println("No jobs on page " + page); break; }
//
//            List<String> hrefs = new ArrayList<>(), titles = new ArrayList<>();
//            for (WebElement el : jobEls) {
//                try { hrefs.add(el.getAttribute("href")); titles.add(el.getText().toLowerCase()); }
//                catch (Exception ignored) {}
//            }
//            System.out.println("Found " + hrefs.size() + " jobs on page " + page);
//
//            for (int i = 0; i < hrefs.size(); i++) {
//                String title = titles.get(i);
//                String link  = hrefs.get(i);
//
//                String roleFilter = profile.getRole() != null
//                        ? profile.getRole().toLowerCase().replace("-", " ") : "java";
//                if (!matchesRole(title, roleFilter)) {
//                    System.out.println("Skipping (filter): " + title);
//                    continue;
//                }
//
//                System.out.println("Processing: " + title);
//
//                // ── Check if session is still alive before opening each job ──────
//                if (!isSessionAlive(driver)) {
//                    System.out.println("Session lost — stopping bot.");
//                    tracker.finish();
//                    return;
//                }
//
//                applyAndTrack(driver, js, mainWindow, title, link, profile, tracker);
//                Thread.sleep(1500);
//            }
//
//            if (!goToNextPage(driver, js, page)) { System.out.println("No more pages."); break; }
//            page++; Thread.sleep(3000);
//        }
//
//        tracker.finish();
//        System.out.println("===== Bot finished — report: " + tracker.getFilePath() + " =====");
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Open one job tab, apply, track result, always return to mainWindow.
//    // NEVER throws — all errors are caught and logged.
//    // ─────────────────────────────────────────────────────────────────────────
//    private void applyAndTrack(WebDriver driver, JavascriptExecutor js,
//                               String mainWindow, String title, String jobUrl,
//                               UserProfile profile, ApplicationTracker tracker) {
//        try {
//            // Open job in new tab
//            js.executeScript("window.open(arguments[0], '_blank');", jobUrl);
//            Thread.sleep(2000);
//
//            // Find the new tab handle
//            String jobTab = null;
//            for (String h : driver.getWindowHandles()) {
//                if (!h.equals(mainWindow)) jobTab = h;
//            }
//            if (jobTab == null) {
//                System.out.println("Job tab did not open — skipping: " + title);
//                tracker.add(new ApplicationResult(profile.getFullName(), title,
//                        "Unknown", jobUrl, null, Status.SKIPPED, "Tab did not open"));
//                return;
//            }
//
//            driver.switchTo().window(jobTab);
//            String company = extractCompany(driver);
//
//            ApplicationResult result = doApply(driver, title, company, jobUrl, profile);
//            tracker.add(result);
//
//        } catch (Exception e) {
//            System.out.println("Error on [" + title + "]: " + e.getMessage());
//            tracker.add(new ApplicationResult(profile.getFullName(), title,
//                    "Unknown", jobUrl, null, Status.FAILED,
//                    e.getClass().getSimpleName() + ": " + truncate(e.getMessage(), 120)));
//        } finally {
//            // ── Always clean up extra tabs and return to mainWindow ────────────
//            safeCleanupTabs(driver, mainWindow);
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Core apply — detects all outcomes and returns the correct status.
//    // ─────────────────────────────────────────────────────────────────────────
//    private ApplicationResult doApply(WebDriver driver, String title, String company,
//                                      String jobUrl, UserProfile profile)
//            throws InterruptedException {
//
//        String username = profile.getFullName();
//
//        // ── Already applied check ─────────────────────────────────────────────
//        if (!driver.findElements(By.xpath(
//                        "//*[contains(text(),'Already Applied') or contains(text(),'already applied')]"))
//                .isEmpty()) {
//            System.out.println("Already applied — skipping.");
//            return new ApplicationResult(username, title, company, jobUrl, null,
//                    Status.SKIPPED, "Already applied");
//        }
//
//        // ── Find Apply button ─────────────────────────────────────────────────
//        WebElement applyBtn;
//        try {
//            applyBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
//                    .until(ExpectedConditions.elementToBeClickable(
//                            By.xpath("//button[contains(.,'Apply')]")));
//        } catch (TimeoutException e) {
//            System.out.println("Apply button not found.");
//            return new ApplicationResult(username, title, company, jobUrl, null,
//                    Status.SKIPPED, "Apply button not found");
//        }
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyBtn);
//        System.out.println("Apply clicked.");
//        Thread.sleep(3000);
//
//        // ── Detect same-tab redirect away from Naukri ─────────────────────────
//        String currentUrl = driver.getCurrentUrl();
//        if (!currentUrl.contains("naukri.com")) {
//            System.out.println("Redirected to company site: " + currentUrl);
//            return new ApplicationResult(username, title, company, jobUrl, currentUrl,
//                    Status.REDIRECTED, "Redirected: " + currentUrl);
//        }
//
//        // ── Detect external tab opened by Naukri ─────────────────────────────
//        // Collect ALL handles and check for non-Naukri ones
//        String externalUrl  = null;
//        String externalTab  = null;
//        String jobTabHandle = null;
//
//        Set<String> allTabs = driver.getWindowHandles();
//        for (String handle : allTabs) {
//            try {
//                driver.switchTo().window(handle);
//                String tabUrl = driver.getCurrentUrl();
//                if (!tabUrl.contains("naukri.com")) {
//                    externalUrl  = tabUrl;
//                    externalTab  = handle;
//                } else {
//                    jobTabHandle = handle;
//                }
//            } catch (Exception ignored) {
//                // Tab may have closed itself — skip
//            }
//        }
//
//        if (externalUrl != null && !externalUrl.isBlank()) {
//            System.out.println("External tab detected: " + externalUrl);
//
//            // Safely close the external tab WITHOUT crashing if it is already gone
//            if (externalTab != null) {
//                try {
//                    driver.switchTo().window(externalTab);
//                    driver.close();
//                } catch (Exception ignored) {
//                    // External site already closed the tab — nothing to do
//                }
//            }
//
//            // Switch back to the job tab (Naukri tab)
//            if (jobTabHandle != null) {
//                try { driver.switchTo().window(jobTabHandle); }
//                catch (Exception ignored) {}
//            }
//
//            return new ApplicationResult(username, title, company, jobUrl, externalUrl,
//                    Status.REDIRECTED, "External tab: " + externalUrl);
//        }
//
//        // Make sure we are on the job tab
//        if (jobTabHandle != null) {
//            try { driver.switchTo().window(jobTabHandle); }
//            catch (Exception ignored) {}
//        }
//
//        // ── ChatBot / Popup / Direct ──────────────────────────────────────────
//        boolean isChatBot = !driver.findElements(By.cssSelector(".chatbot_Drawer")).isEmpty();
//        boolean isPopup   = !driver.findElements(
//                By.cssSelector(".apply-layer-container,.question-title")).isEmpty();
//
//        try {
//            if (isChatBot) {
//                System.out.println("-> ChatBot flow");
//                ChatBotHandler.handle(driver);
//                return new ApplicationResult(username, title, company, jobUrl, null,
//                        Status.SUCCESS, "ChatBot answered");
//            } else if (isPopup) {
//                System.out.println("-> Popup flow");
//                PopupHandler.handle(driver);
//                return new ApplicationResult(username, title, company, jobUrl, null,
//                        Status.SUCCESS, "Popup answered");
//            } else {
//                System.out.println("-> Direct apply");
//                return new ApplicationResult(username, title, company, jobUrl, null,
//                        Status.DIRECT_APPLY, "No questions asked");
//            }
//        } catch (Exception e) {
//            System.out.println("Apply flow error: " + e.getMessage());
//            return new ApplicationResult(username, title, company, jobUrl, null,
//                    Status.FAILED, "Flow error: " + truncate(e.getMessage(), 120));
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Close every tab except mainWindow, then switch back to mainWindow.
//    // Completely silent — never throws.
//    // ─────────────────────────────────────────────────────────────────────────
//    private void safeCleanupTabs(WebDriver driver, String mainWindow) {
//        try {
//            Set<String> handles = driver.getWindowHandles();
//            for (String tab : new ArrayList<>(handles)) {
//                if (!tab.equals(mainWindow)) {
//                    try {
//                        driver.switchTo().window(tab);
//                        driver.close();
//                    } catch (Exception ignored) {
//                        // Tab already closed or session issue — skip it
//                    }
//                }
//            }
//        } catch (Exception ignored) {
//            // getWindowHandles() itself failed — session may be gone
//        }
//        // Always try to land on mainWindow
//        try { driver.switchTo().window(mainWindow); }
//        catch (Exception ignored) {}
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Quick session health check — returns false if session is truly dead
//    // ─────────────────────────────────────────────────────────────────────────
//    private boolean isSessionAlive(WebDriver driver) {
//        try {
//            driver.getWindowHandles(); // lightest possible Selenium call
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    private String extractCompany(WebDriver driver) {
//        String[] selectors = {
//                ".jd-header-comp-name a", "[class*='comp-name']",
//                ".company-name", ".orgName", "[class*='companyName']"
//        };
//        for (String sel : selectors) {
//            try {
//                List<WebElement> els = driver.findElements(By.cssSelector(sel));
//                if (!els.isEmpty()) {
//                    String text = els.get(0).getText().trim();
//                    if (!text.isBlank()) return text;
//                }
//            } catch (Exception ignored) {}
//        }
//        return "Unknown";
//    }
//
//    private boolean matchesRole(String title, String roleFilter) {
//        String[] parts = roleFilter.split("[\\s-]+");
//        for (String p : parts) { if (p.length() > 2 && title.contains(p)) return true; }
//        return false;
//    }
//
//    private String truncate(String s, int max) {
//        if (s == null) return "";
//        return s.length() > max ? s.substring(0, max) + "…" : s;
//    }
//
//    private void login(WebDriver driver, UserProfile profile) throws InterruptedException {
//        driver.get("https://www.naukri.com/nlogin/login");
//        Thread.sleep(2000);
//        driver.findElement(By.id("usernameField")).sendKeys(profile.getNaukriEmail());
//        driver.findElement(By.id("passwordField")).sendKeys(profile.getNaukriPassword());
//        driver.findElement(By.xpath("//button[text()='Login']")).click();
//        Thread.sleep(4000);
//        System.out.println("Login done: " + profile.getNaukriEmail());
//    }
//
//    private boolean goToNextPage(WebDriver driver, JavascriptExecutor js, int cur) {
//        int next = cur + 1;
//        String[] xpaths = {
//                "//a[normalize-space(text())='" + next + "']",
//                "//a[@data-page='" + next + "']",
//                "//*[contains(@class,'pagination')]//a[normalize-space(text())='" + next + "']",
//                "//a[contains(@class,'next') or normalize-space(text())='Next' or @aria-label='Next page']"
//        };
//        for (String xp : xpaths) {
//            try {
//                WebElement b = driver.findElement(By.xpath(xp));
//                js.executeScript("arguments[0].click();", b);
//                return true;
//            } catch (NoSuchElementException ignored) {}
//        }
//        return false;
//    }
//}



package com.Job.applybot.bot;

import com.Job.applybot.Driver.DriverFactory;
import com.Job.applybot.model.ApplicationTracker;
import com.Job.applybot.Service.ApplicationResult;
import com.Job.applybot.Service.ApplicationResult.Status;
import com.Job.applybot.model.UserProfile;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.*;

public class Bot {

    private static final String RECOMMENDED_URL = "https://www.naukri.com/recommendedjobs";

    public void searchjob(String url, UserProfile profile) throws InterruptedException {
        WebDriver driver = DriverFactory.GetWebDriver();
        ApplicationTracker tracker = new ApplicationTracker(profile.getFullName());

        login(driver, profile);

        if (profile.isRecommendedOnly()) {
            System.out.println("=== MODE: RECOMMENDED JOBS ONLY ===");
            processRecommendedPage(driver, tracker, profile);
        } else {
            System.out.println("=== MODE: SEARCH — " + url + " ===");
            processSearchPages(driver, tracker, profile, url);
            if (profile.isIncludeRecommended()) {
                System.out.println("=== ALSO PROCESSING: RECOMMENDED JOBS ===");
                processRecommendedPage(driver, tracker, profile);
            }
        }

        tracker.finish();

        // ── COMPLETION NOTIFICATION ───────────────────────────────────────────
        long success  = tracker.countByStatus("SUCCESS");
        long direct   = tracker.countByStatus("DIRECT_APPLY");
        long failed   = tracker.countByStatus("FAILED");
        long skipped  = tracker.countByStatus("SKIPPED");
        long redir    = tracker.countByStatus("REDIRECTED");
        long total    = success + direct + failed + skipped + redir;

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║          APPLYBOT — RUN COMPLETE         ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf( "║  Total processed   : %-20d  ║%n", total);
        System.out.printf( "║  Applied (chatbot) : %-20d  ║%n", success);
        System.out.printf( "║  Applied (direct)  : %-20d  ║%n", direct);
        System.out.printf( "║  Failed            : %-20d  ║%n", failed);
        System.out.printf( "║  Skipped           : %-20d  ║%n", skipped);
        System.out.printf( "║  Redirected        : %-20d  ║%n", redir);
        System.out.printf( "║  Total applied     : %-20d  ║%n", success + direct);
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Report: " + fitStr(tracker.getFilePath(), 34) + "  ║");
        System.out.println("╚══════════════════════════════════════════╝\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RECOMMENDED JOBS — naukri.com/recommendedjobs
    //
    // Naukri's recommended page renders job cards differently from search.
    // We dump ALL anchor hrefs that contain "job-listings" after full scroll.
    // ─────────────────────────────────────────────────────────────────────────
    private void processRecommendedPage(WebDriver driver, ApplicationTracker tracker,
                                        UserProfile profile) throws InterruptedException {
        driver.get(RECOMMENDED_URL);
        Thread.sleep(4000);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String mainWindow     = driver.getWindowHandle();

        // Scroll aggressively to trigger lazy-load of all cards
        System.out.println("[Recommended] Scrolling to load all cards...");
        long lastHeight = 0;
        for (int attempt = 0; attempt < 15; attempt++) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(1500);
            long newHeight = (Long) js.executeScript("return document.body.scrollHeight");
            if (newHeight == lastHeight) break;  // no more content loading
            lastHeight = newHeight;
        }
        // Scroll back to top so cards are all in DOM
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(1000);

        // ── Collect job links using every known Naukri pattern ────────────────
        List<String> hrefs  = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        // Try all selectors — recommended page has changed class names over time
        String[][] strategies = {
                // selector,  title-attr-or-text
                { "a.title[href*='job-listings']",              "text" },
                { "a[title][href*='job-listings']",             "title" },
                { "a[href*='job-listings'].jobTitle",           "text" },
                { "a[href*='job-listings'][class*='title']",    "text" },
                { "a[href*='job-listings'][class*='Title']",    "text" },
                { "a[href*='job-listings'][class*='jobTitle']", "text" },
                { "a[href*='job-listings']",                    "text" },  // broadest fallback
        };

        for (String[] s : strategies) {
            List<WebElement> els = driver.findElements(By.cssSelector(s[0]));
            if (!els.isEmpty()) {
                debug("Recommended: selector [" + s[0] + "] found " + els.size() + " elements");
                for (WebElement el : els) {
                    try {
                        String href  = el.getAttribute("href");
                        String title = "title".equals(s[1])
                                ? el.getAttribute("title")
                                : el.getText().trim();
                        if (href  == null || href.isBlank())  continue;
                        if (hrefs.contains(href))             continue;  // dedup
                        hrefs.add(href);
                        titles.add(title != null ? title.toLowerCase() : "unknown");
                    } catch (Exception ignored) {}
                }
                if (!hrefs.isEmpty()) break;  // found jobs — stop trying
            }
        }

        // Last-resort: extract via JS to pierce any shadow DOM or React hydration
        if (hrefs.isEmpty()) {
            debug("Recommended: trying JS extraction...");
            @SuppressWarnings("unchecked")
            List<String> jsLinks = (List<String>) js.executeScript(
                    "return Array.from(document.querySelectorAll('a[href]'))" +
                            "  .map(a => a.href)" +
                            "  .filter(h => h.includes('job-listings'));"
            );
            if (jsLinks != null) {
                for (String href : jsLinks) {
                    if (!hrefs.contains(href)) { hrefs.add(href); titles.add("unknown"); }
                }
            }
        }

        System.out.println("[Recommended] Jobs found: " + hrefs.size());

        if (hrefs.isEmpty()) {
            debug("Recommended: NO jobs found. Dumping page state for diagnosis...");
            dumpPageState(driver, js);
            return;
        }

        processJobList(driver, js, mainWindow, hrefs, titles, profile, tracker);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH — paginated
    // ─────────────────────────────────────────────────────────────────────────
    private void processSearchPages(WebDriver driver, ApplicationTracker tracker,
                                    UserProfile profile, String startUrl)
            throws InterruptedException {

        driver.get(startUrl);
        Thread.sleep(3000);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String mainWindow     = driver.getWindowHandle();
        int page              = 1;
        int maxPages          = profile.getMaxPages() > 0 ? profile.getMaxPages() : 10;
        String prevPageUrl    = "";  // used to detect if pagination actually moved

        while (page <= maxPages) {
            System.out.println("===== PAGE " + page + " =====");

            for (int i = 0; i < 3; i++) {
                js.executeScript("window.scrollBy(0,1000)");
                Thread.sleep(1000);
            }

            List<String> hrefs  = collectSearchLinks(driver);
            List<String> titles = collectSearchTitles(driver);

            if (hrefs.isEmpty()) {
                System.out.println("No jobs found on page " + page + " — stopping pagination.");
                break;
            }
            System.out.println("Found " + hrefs.size() + " jobs on page " + page);

            processJobList(driver, js, mainWindow, hrefs, titles, profile, tracker);

            // ── PAGINATION ────────────────────────────────────────────────────
            String currentUrl = driver.getCurrentUrl();
            boolean moved     = goToNextPage(driver, js, page);

            if (!moved) {
                System.out.println("No page " + (page + 1) + " — end of search results.");
                break;
            }

            Thread.sleep(3000);

            // Verify the URL actually changed — prevents infinite loop on same page
            String newUrl = driver.getCurrentUrl();
            if (newUrl.equals(currentUrl) || newUrl.equals(prevPageUrl)) {
                System.out.println("Pagination did not navigate to a new page — stopping.");
                break;
            }
            prevPageUrl = currentUrl;
            page++;

            if (!isSessionAlive(driver)) { System.out.println("Session lost."); break; }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Process a list of job hrefs
    // Title filter is COMMENTED OUT — uncomment block below to re-enable
    // ─────────────────────────────────────────────────────────────────────────
    private void processJobList(WebDriver driver, JavascriptExecutor js,
                                String mainWindow, List<String> hrefs, List<String> titles,
                                UserProfile profile, ApplicationTracker tracker)
            throws InterruptedException {

        for (int i = 0; i < hrefs.size(); i++) {
            String title = (titles.size() > i) ? titles.get(i) : "unknown";
            String link  = hrefs.get(i);

            /* ── TITLE FILTER (uncomment to enable) ───────────────────────
            String roleFilter = profile.getRole() != null
                    ? profile.getRole().toLowerCase().replace("-"," ") : "java";
            if (!matchesRole(title, roleFilter)) {
                System.out.println("Skipping (filter): " + title);
                continue;
            }
            ─────────────────────────────────────────────────────────────── */

            System.out.println("Processing [" + (i+1) + "/" + hrefs.size() + "]: " + title);

            if (!isSessionAlive(driver)) { System.out.println("Session lost."); return; }

            applyAndTrack(driver, js, mainWindow, title, link, profile, tracker);
            Thread.sleep(1500);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Collect search-page job links (standard Naukri search result)
    // ─────────────────────────────────────────────────────────────────────────
    private List<String> collectSearchLinks(WebDriver driver) {
        List<String> links = new ArrayList<>();
        // Primary search result selector
        List<WebElement> els = driver.findElements(
                By.xpath("//a[contains(@href,'job-listings') and contains(@class,'title')]"));
        if (els.isEmpty()) {
            // Fallback
            els = driver.findElements(By.cssSelector("a[href*='job-listings']"));
        }
        for (WebElement el : els) {
            try { String h = el.getAttribute("href"); if (h!=null && !h.isBlank()) links.add(h); }
            catch (Exception ignored) {}
        }
        return dedup(links);
    }

    private List<String> collectSearchTitles(WebDriver driver) {
        List<String> titles = new ArrayList<>();
        List<WebElement> els = driver.findElements(
                By.xpath("//a[contains(@href,'job-listings') and contains(@class,'title')]"));
        if (els.isEmpty()) els = driver.findElements(By.cssSelector("a[href*='job-listings']"));
        for (WebElement el : els) {
            try { titles.add(el.getText().toLowerCase().trim()); }
            catch (Exception ignored) { titles.add("unknown"); }
        }
        return titles;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Apply to one job — track result
    // ─────────────────────────────────────────────────────────────────────────
    private void applyAndTrack(WebDriver driver, JavascriptExecutor js,
                               String mainWindow, String title, String jobUrl,
                               UserProfile profile, ApplicationTracker tracker) {
        try {
            js.executeScript("window.open(arguments[0], '_blank');", jobUrl);
            Thread.sleep(2000);

            String jobTab = null;
            for (String h : driver.getWindowHandles()) { if (!h.equals(mainWindow)) jobTab = h; }
            if (jobTab == null) {
                tracker.add(new ApplicationResult(profile.getFullName(), title, "Unknown",
                        jobUrl, null, Status.SKIPPED, "Tab did not open"));
                return;
            }

            driver.switchTo().window(jobTab);
            String company = extractCompany(driver);
            ApplicationResult result = doApply(driver, title, company, jobUrl, profile);
            tracker.add(result);

        } catch (Exception e) {
            System.out.println("Error on [" + title + "]: " + e.getMessage());
            tracker.add(new ApplicationResult(profile.getFullName(), title, "Unknown",
                    jobUrl, null, Status.FAILED,
                    e.getClass().getSimpleName() + ": " + truncate(e.getMessage(), 120)));
        } finally {
            safeCleanupTabs(driver, mainWindow);
        }
    }

    private ApplicationResult doApply(WebDriver driver, String title, String company,
                                      String jobUrl, UserProfile profile)
            throws InterruptedException {

        String username = profile.getFullName();

        if (!driver.findElements(By.xpath(
                        "//*[contains(text(),'Already Applied') or contains(text(),'already applied')]"))
                .isEmpty()) {
            System.out.println("Already applied — skipping.");
            return new ApplicationResult(username, title, company, jobUrl, null,
                    Status.SKIPPED, "Already applied");
        }

        WebElement applyBtn;
        try {
            applyBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(.,'Apply')]")));
        } catch (TimeoutException e) {
            System.out.println("Apply button not found.");
            return new ApplicationResult(username, title, company, jobUrl, null,
                    Status.SKIPPED, "Apply button not found");
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyBtn);
        System.out.println("Apply clicked.");
        Thread.sleep(3000);

        // Same-tab redirect
        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.contains("naukri.com")) {
            System.out.println("Redirected: " + currentUrl);
            return new ApplicationResult(username, title, company, jobUrl, currentUrl,
                    Status.REDIRECTED, "Redirected: " + currentUrl);
        }

        // External tab check
        String externalUrl = null, externalTab = null, jobTabHandle = null;
        for (String handle : driver.getWindowHandles()) {
            try {
                driver.switchTo().window(handle);
                String tabUrl = driver.getCurrentUrl();
                if (!tabUrl.contains("naukri.com")) { externalUrl = tabUrl; externalTab = handle; }
                else                                 { jobTabHandle = handle; }
            } catch (Exception ignored) {}
        }
        if (externalUrl != null && !externalUrl.isBlank()) {
            System.out.println("External tab: " + externalUrl);
            if (externalTab != null) {
                try { driver.switchTo().window(externalTab); driver.close(); }
                catch (Exception ignored) {}
            }
            if (jobTabHandle != null) {
                try { driver.switchTo().window(jobTabHandle); } catch (Exception ignored) {}
            }
            return new ApplicationResult(username, title, company, jobUrl, externalUrl,
                    Status.REDIRECTED, "External tab: " + externalUrl);
        }
        if (jobTabHandle != null) {
            try { driver.switchTo().window(jobTabHandle); } catch (Exception ignored) {}
        }

        boolean isChatBot = !driver.findElements(By.cssSelector(".chatbot_Drawer")).isEmpty();
        boolean isPopup   = !driver.findElements(
                By.cssSelector(".apply-layer-container,.question-title")).isEmpty();
        try {
            if (isChatBot) {
                System.out.println("-> ChatBot flow");
                ChatBotHandler.handle(driver);
                return new ApplicationResult(username, title, company, jobUrl, null,
                        Status.SUCCESS, "ChatBot answered");
            } else if (isPopup) {
                System.out.println("-> Popup flow");
                PopupHandler.handle(driver);
                return new ApplicationResult(username, title, company, jobUrl, null,
                        Status.SUCCESS, "Popup answered");
            } else {
                System.out.println("-> Direct apply");
                return new ApplicationResult(username, title, company, jobUrl, null,
                        Status.DIRECT_APPLY, "No questions asked");
            }
        } catch (Exception e) {
            return new ApplicationResult(username, title, company, jobUrl, null,
                    Status.FAILED, "Flow error: " + truncate(e.getMessage(), 120));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PAGINATION — fixed to prevent infinite loop
    //
    // Key fix: after clicking, we verify the URL changed in processSearchPages.
    // This method only clicks — the caller checks if navigation happened.
    // ─────────────────────────────────────────────────────────────────────────
    private boolean goToNextPage(WebDriver driver, JavascriptExecutor js, int currentPage) {
        int next = currentPage + 1;
        System.out.println("[Pagination] Looking for page " + next);

        // Scroll pagination area into view first
        try {
            WebElement paginationArea = driver.findElement(By.xpath(
                    "//*[contains(@class,'pagination') or contains(@class,'page-nav') or contains(@class,'Pagination')]"));
            js.executeScript("arguments[0].scrollIntoView({block:'center'})", paginationArea);
            Thread.sleep(500);
        } catch (Exception ignored) {}

        // Try all known patterns
        String[] xpaths = {
                "//a[normalize-space(text())='" + next + "']",
                "//a[@data-page='" + next + "']",
                "//span[normalize-space(text())='" + next + "']",
                "//button[normalize-space(text())='" + next + "']",
                "//*[contains(@class,'pagination')]//a[normalize-space(text())='" + next + "']",
                "//*[contains(@class,'page')]//a[normalize-space(text())='" + next + "']",
                "//a[contains(@class,'next') and not(contains(@class,'disabled'))]",
                "//a[@aria-label='Next page']",
                "//a[normalize-space(text())='Next']",
                "//a[normalize-space(text())='>']",
        };

        for (String xp : xpaths) {
            try {
                List<WebElement> found = driver.findElements(By.xpath(xp));
                for (WebElement el : found) {
                    if (el.isDisplayed()) {
                        js.executeScript("arguments[0].scrollIntoView({block:'center'})", el);
                        Thread.sleep(300);
                        js.executeScript("arguments[0].click()", el);
                        System.out.println("[Pagination] Clicked: " + el.getText().trim()
                                + " (xpath: " + xp.substring(0, Math.min(50, xp.length())) + ")");
                        return true;
                    }
                }
            } catch (Exception ignored) {}
        }

        System.out.println("[Pagination] No navigation element found for page " + next);
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void safeCleanupTabs(WebDriver driver, String mainWindow) {
        try {
            for (String tab : new ArrayList<>(driver.getWindowHandles())) {
                if (!tab.equals(mainWindow)) {
                    try { driver.switchTo().window(tab); driver.close(); }
                    catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
        try { driver.switchTo().window(mainWindow); } catch (Exception ignored) {}
    }

    private boolean isSessionAlive(WebDriver driver) {
        try { driver.getWindowHandles(); return true; } catch (Exception e) { return false; }
    }

    private String extractCompany(WebDriver driver) {
        String[] selectors = {
                ".jd-header-comp-name a", "[class*='comp-name']",
                ".company-name", ".orgName", "[class*='companyName']"
        };
        for (String sel : selectors) {
            try {
                List<WebElement> els = driver.findElements(By.cssSelector(sel));
                if (!els.isEmpty()) {
                    String text = els.get(0).getText().trim();
                    if (!text.isBlank()) return text;
                }
            } catch (Exception ignored) {}
        }
        return "Unknown";
    }

    // Diagnose why recommended page found 0 jobs
    private void dumpPageState(WebDriver driver, JavascriptExecutor js) {
        System.out.println("[DEBUG] Current URL: " + driver.getCurrentUrl());
        System.out.println("[DEBUG] Page title: " + driver.getTitle());
        // Count all anchors with job-listings
        try {
            Long count = (Long) js.executeScript(
                    "return document.querySelectorAll('a[href*=\"job-listings\"]').length;");
            System.out.println("[DEBUG] a[href*=job-listings] count: " + count);
        } catch (Exception ignored) {}
        // Print first 5 anchor hrefs
        try {
            @SuppressWarnings("unchecked")
            List<String> sample = (List<String>) js.executeScript(
                    "return Array.from(document.querySelectorAll('a[href]'))" +
                            "  .slice(0,20).map(a=>a.href+' | '+a.className);");
            System.out.println("[DEBUG] First 20 anchors:");
            if (sample != null) sample.forEach(s -> System.out.println("  " + s));
        } catch (Exception ignored) {}
    }

    // commented-out helper — keep for when title filter is re-enabled
    // private boolean matchesRole(String title, String roleFilter) {
    //     String[] parts = roleFilter.split("[\\s-]+");
    //     for (String p : parts) { if (p.length() > 2 && title.contains(p)) return true; }
    //     return false;
    // }

    private List<String> dedup(List<String> list) {
        List<String> result = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (String s : list) { if (seen.add(s)) result.add(s); }
        return result;
    }

    private String fitStr(String s, int len) {
        if (s == null) return " ".repeat(len);
        if (s.length() <= len) return s + " ".repeat(len - s.length());
        return "..." + s.substring(s.length() - (len - 3));
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    private void debug(String msg) { System.out.println("[Bot] " + msg); }

    private void login(WebDriver driver, UserProfile profile) throws InterruptedException {
        driver.get("https://www.naukri.com/nlogin/login");
        Thread.sleep(2000);
        driver.findElement(By.id("usernameField")).sendKeys(profile.getNaukriEmail());
        driver.findElement(By.id("passwordField")).sendKeys(profile.getNaukriPassword());
        driver.findElement(By.xpath("//button[text()='Login']")).click();
        Thread.sleep(4000);
        System.out.println("Login done: " + profile.getNaukriEmail());
    }
}