package com.Job.applybot.bot;

import com.Job.applybot.Service.AnswerEngine;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

/**
 * ChatBotHandler — Final version with all fixes:
 *
 * FIX 1: Skip chip logic — "Skip this question" chip is ONLY clicked when
 *         the answer from AnswerEngine is explicitly "skip". Otherwise the
 *         chip list is treated as having no real options and falls through
 *         to text input.
 *
 * FIX 2: Stale element in tryTextInput — always re-finds input by fresh
 *         selector call before every JS/Selenium operation. Never holds
 *         a WebElement reference across sleeps.
 *
 * FIX 3: Bot acknowledgment detection — "Thank you", "Got it!", "Great!",
 *         "Nice!", "Awesome!", "Amazing!" etc. are bot closure messages.
 *         The code now detects these and waits for the drawer to close
 *         instead of trying to answer them.
 *
 * FIX 4: "element click intercepted" — uses JS click for text input instead
 *         of Selenium .click() which fails when another element overlaps.
 *
 * FIX 5: Checkbox preferred location — tries to match location chips first
 *         before falling back to first option.
 */
public class ChatBotHandler {

    // Skills we know — used to match checkboxes
    private static final String[] KNOWN_SKILLS = {
            "java", "spring boot", "spring", "hibernate", "selenium",
            "react", "angular", "javascript", "python", "sql", "mysql",
            "mongodb", "rest api", "microservices", "docker", "git",
            "node", "typescript", "junit", "maven", "gradle", "aws",
            "kubernetes", "kafka", "redis", "jenkins"
    };

    // Bot acknowledgment messages — these are not questions, bot is wrapping up
    private static final String[] ACK_PHRASES = {
            "thank you for your responses", "thank you for",
            "got it!", "got it.", "great!", "nice!", "awesome!", "amazing!",
            "perfect!", "wonderful!", "noted!", "sure!", "okay!", "alright!",
            "you can say something like"
    };

    public static void handle(WebDriver driver) {
        JavascriptExecutor js      = (JavascriptExecutor) driver;
        Actions            actions = new Actions(driver);
        String             lastQ   = "";
        int                stuckCount = 0;
        int                lastMsgCount = 0;

        debug("=== ChatBotHandler START ===");

        try {
            switchToChatbotFrameIfPresent(driver);

            for (int turn = 0; turn < 30; turn++) {
                debug("--- Turn " + turn + " ---");
                waitForBotIdle(driver);

                // ── Read current question ─────────────────────────────────────────
                String currentQ = getLatestQuestion(driver);
                debug("Question: [" + currentQ + "]");

                if (currentQ == null || currentQ.isBlank()) { debug("No question — ending."); break; }
                if (isChatDone(currentQ))                    { debug("Chat done.");             break; }

                // Bot acknowledgment — wait for closure, don't try to answer
                if (isAcknowledgment(currentQ)) {
                    debug("Bot acknowledgment detected — waiting for closure or next question...");
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                    // Check if drawer is closing
                    if (isDrawerClosing(driver)) { debug("Drawer closing — ending."); break; }
                    continue; // wait for the real next question
                }

                // Stuck detection
                if (currentQ.equalsIgnoreCase(lastQ)) {
                    if (++stuckCount >= 3) { debug("Stuck 3x — breaking."); break; }
                } else {
                    stuckCount = 0;
                }
                lastQ = currentQ;

                lastMsgCount = getBotMessageCount(driver);
                debug("Bot message count before answer: " + lastMsgCount);

                String answer = AnswerEngine.getAnswer(currentQ.toLowerCase());
                debug("Answer: [" + answer + "]");

                // ── Widget detection ──────────────────────────────────────────────
                boolean handled = false;
                if (!handled) handled = tryChipClick(driver, js, currentQ, answer);
                if (!handled) handled = tryCheckboxes(driver, js, currentQ, answer);
                if (!handled) handled = tryRadioClick(driver, js, answer);
                if (!handled) handled = trySuggestionChip(driver, js, answer, actions);
                if (!handled) handled = tryTextInput(driver, js, actions, answer);

                if (!handled) {
                    debug("WARNING: Nothing handled this turn.");
                    dumpPageState(driver);
                }

                waitForNewBotMessage(driver, lastMsgCount);
            }

            waitForDrawerClose(driver);

        } catch (Exception e) {
            debug("ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            driver.switchTo().defaultContent();
            debug("=== ChatBotHandler END ===");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // TYPE 1: CHIP BUTTONS — div.chatbot_Chip
    //
    // KEY FIX: If all chips are just "Skip this question" and answer != "skip",
    // return false so text input can handle it instead.
    // ─────────────────────────────────────────────────────────────────────────────
    private static boolean tryChipClick(WebDriver driver, JavascriptExecutor js,
                                        String question, String answer) {
        List<WebElement> chips = driver.findElements(By.cssSelector("div.chatbot_Chip"));
        if (chips.isEmpty()) return false;

        // Collect chip texts
        String[] chipTexts = new String[chips.size()];
        for (int i = 0; i < chips.size(); i++) {
            try { chipTexts[i] = chips.get(i).getText().trim().toLowerCase(); }
            catch (StaleElementReferenceException e) { chipTexts[i] = ""; }
        }

        debug("Found " + chips.size() + " chip(s):");
        for (int i = 0; i < chipTexts.length; i++) debug("  chip[" + i + "]: [" + chipTexts[i] + "]");

        // Check if ALL chips are just "skip this question"
        boolean allSkip = true;
        for (String t : chipTexts) {
            if (!t.contains("skip")) { allSkip = false; break; }
        }

        // If only skip chip exists and answer is NOT "skip", fall through to text input
        if (allSkip && !answer.equalsIgnoreCase("skip")) {
            debug("Only skip chip present but answer is not skip — falling through to text input.");
            return false;
        }

        // Resume question → always click "later" or last chip
        if (question.toLowerCase().contains("resume") || question.toLowerCase().contains("upload")) {
            debug("Resume question — looking for 'later' chip");
            for (int i = 0; i < chipTexts.length; i++) {
                if (chipTexts[i].contains("later") || chipTexts[i].contains("skip")) {
                    return clickChipByIndex(driver, js, i);
                }
            }
            return clickChipByIndex(driver, js, chips.size() - 1);
        }

        // General matching
        String[] candidates = buildCandidates(answer);
        debug("Chip candidates: " + java.util.Arrays.toString(candidates));

        // Pass 1: exact/contains
        for (int i = 0; i < chipTexts.length; i++) {
            if (chipTexts[i].contains("skip")) continue; // never match skip on pass 1/2
            for (String c : candidates) {
                if (chipTexts[i].equals(c.toLowerCase()) || chipTexts[i].contains(c.toLowerCase())) {
                    debug("Chip matched [" + chipTexts[i] + "]");
                    return clickChipByIndex(driver, js, i);
                }
            }
        }

        // Pass 2: prefix
        for (int i = 0; i < chipTexts.length; i++) {
            if (chipTexts[i].contains("skip")) continue;
            for (String c : candidates) {
                if (chipTexts[i].startsWith(c.toLowerCase())) {
                    debug("Chip prefix match [" + chipTexts[i] + "]");
                    return clickChipByIndex(driver, js, i);
                }
            }
        }

        // If answer is "skip", click skip chip
        if (answer.equalsIgnoreCase("skip")) {
            for (int i = 0; i < chipTexts.length; i++) {
                if (chipTexts[i].contains("skip")) return clickChipByIndex(driver, js, i);
            }
        }

        // Default: "No" → last non-skip chip, else → first non-skip chip
        // Find first/last non-skip index
        int firstNonSkip = -1, lastNonSkip = -1;
        for (int i = 0; i < chipTexts.length; i++) {
            if (!chipTexts[i].contains("skip")) {
                if (firstNonSkip == -1) firstNonSkip = i;
                lastNonSkip = i;
            }
        }
        if (firstNonSkip == -1) {
            // Only skip chips — if answer is skip, click it; else fall through
            if (answer.equalsIgnoreCase("skip")) return clickChipByIndex(driver, js, 0);
            return false;
        }
        int idx = answer.equalsIgnoreCase("No") ? lastNonSkip : firstNonSkip;
        debug("Chip default idx=" + idx + ": [" + chipTexts[idx] + "]");
        return clickChipByIndex(driver, js, idx);
    }

    private static boolean clickChipByIndex(WebDriver driver, JavascriptExecutor js, int idx) {
        try {
            List<WebElement> fresh = driver.findElements(By.cssSelector("div.chatbot_Chip"));
            if (idx >= fresh.size()) return false;
            String text = "";
            try { text = fresh.get(idx).getText().trim(); } catch (Exception ignored) {}
            js.executeScript("arguments[0].click();", fresh.get(idx));
            debug("Clicked chip[" + idx + "]: [" + text + "] ✓");
            return true;
        } catch (StaleElementReferenceException e) {
            try {
                Thread.sleep(400);
                List<WebElement> fresh = driver.findElements(By.cssSelector("div.chatbot_Chip"));
                if (idx < fresh.size()) { js.executeScript("arguments[0].click();", fresh.get(idx)); return true; }
            } catch (Exception ignored) {}
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // TYPE 2: CHECKBOX LIST — input[type='checkbox']
    // FIX: For location questions, try to match preferred location first.
    //      For skill questions, match KNOWN_SKILLS.
    // ─────────────────────────────────────────────────────────────────────────────
    private static boolean tryCheckboxes(WebDriver driver, JavascriptExecutor js,
                                         String question, String answer) {
        List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
        if (checkboxes.isEmpty()) return false;

        debug("Found " + checkboxes.size() + " checkbox(es):");

        String[] labelTexts = new String[checkboxes.size()];
        for (int i = 0; i < checkboxes.size(); i++) {
            labelTexts[i] = getCheckboxLabel(driver, checkboxes.get(i)).toLowerCase().trim();
            debug("  checkbox[" + i + "] label: [" + labelTexts[i] + "]");
        }

        boolean anyChecked = false;
        String qLower = question.toLowerCase();

        // Location questions — try to match answer (city name) in checkboxes
        if (qLower.contains("location") || qLower.contains("city") || qLower.contains("cities")) {
            String[] locationCandidates = buildCandidates(answer);
            for (int i = 0; i < labelTexts.length; i++) {
                for (String c : locationCandidates) {
                    if (labelTexts[i].contains(c.toLowerCase())) {
                        debug("  → Location match [" + c + "] at idx=" + i);
                        anyChecked |= clickCheckboxByIndex(driver, js, i);
                        break;
                    }
                }
            }
            if (!anyChecked) {
                debug("No location match — checking first");
                anyChecked = clickCheckboxByIndex(driver, js, 0);
            }
        } else {
            // Skill/general questions — match KNOWN_SKILLS
            for (int i = 0; i < labelTexts.length; i++) {
                for (String skill : KNOWN_SKILLS) {
                    if (labelTexts[i].contains(skill.toLowerCase())) {
                        debug("  → Skill match [" + skill + "] at idx=" + i);
                        anyChecked |= clickCheckboxByIndex(driver, js, i);
                        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                        break;
                    }
                }
            }
            if (!anyChecked) {
                debug("No skill match — checking first");
                anyChecked = clickCheckboxByIndex(driver, js, 0);
            }
        }

        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        clickSaveButton(driver, js);
        return true;
    }

    private static boolean clickCheckboxByIndex(WebDriver driver, JavascriptExecutor js, int idx) {
        try {
            List<WebElement> fresh = driver.findElements(By.cssSelector("input[type='checkbox']"));
            if (idx < fresh.size()) { js.executeScript("arguments[0].click();", fresh.get(idx)); return true; }
        } catch (Exception e) { debug("Checkbox click error at idx=" + idx + ": " + e.getMessage()); }
        return false;
    }

    private static String getCheckboxLabel(WebDriver driver, WebElement cb) {
        try {
            String id = cb.getAttribute("id");
            if (id != null && !id.isBlank()) {
                List<WebElement> labels = driver.findElements(By.cssSelector("label[for='" + id + "']"));
                if (!labels.isEmpty()) return labels.get(0).getText();
            }
        } catch (Exception ignored) {}
        try {
            Object text = ((JavascriptExecutor) driver).executeScript(
                    "var el=arguments[0];" +
                            "var next=el.nextElementSibling;" +
                            "if(next && next.tagName==='LABEL') return next.textContent;" +
                            "var parent=el.parentElement;" +
                            "if(parent){var lbl=parent.querySelector('label');if(lbl)return lbl.textContent;return parent.textContent;}" +
                            "return '';", cb);
            if (text != null) return text.toString().trim();
        } catch (Exception ignored) {}
        return "";
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // TYPE 3: RADIO BUTTONS — input.ssrc__radio
    // ─────────────────────────────────────────────────────────────────────────────
    private static boolean tryRadioClick(WebDriver driver, JavascriptExecutor js, String answer) {
        List<WebElement> radios = driver.findElements(By.cssSelector("input.ssrc__radio"));
        if (radios.isEmpty()) return false;

        debug("Found " + radios.size() + " radio(s):");
        String[] ids  = new String[radios.size()];
        String[] vals = new String[radios.size()];
        for (int i = 0; i < radios.size(); i++) {
            try {
                ids[i]  = safeAttr(radios.get(i), "id").toLowerCase().trim();
                vals[i] = safeAttr(radios.get(i), "value").toLowerCase().trim();
            } catch (StaleElementReferenceException e) { ids[i] = vals[i] = ""; }
            debug("  radio[" + i + "]: id=[" + ids[i] + "]");
        }

        String[] candidates = buildCandidates(answer);
        debug("Radio candidates: " + java.util.Arrays.toString(candidates));

        for (int i = 0; i < ids.length; i++) {
            if (ids[i].contains("skip")) continue;
            for (String c : candidates) {
                String cl = c.toLowerCase().trim();
                if (ids[i].equals(cl) || vals[i].equals(cl) || ids[i].contains(cl) || vals[i].contains(cl)) {
                    debug("Radio matched at idx=" + i + ": [" + ids[i] + "]");
                    return clickRadioByIndex(driver, js, i);
                }
            }
        }

        // No match → FIRST non-skip option
        for (int i = 0; i < ids.length; i++) {
            if (!ids[i].contains("skip")) {
                debug("No radio match — clicking FIRST non-skip: [" + ids[i] + "]");
                return clickRadioByIndex(driver, js, i);
            }
        }
        return clickRadioByIndex(driver, js, 0);
    }

    private static boolean clickRadioByIndex(WebDriver driver, JavascriptExecutor js, int idx) {
        try {
            List<WebElement> fresh = driver.findElements(By.cssSelector("input.ssrc__radio"));
            if (idx >= fresh.size()) return false;
            WebElement radio = fresh.get(idx);
            String id = safeAttr(radio, "id");
            try {
                List<WebElement> labels = driver.findElements(By.cssSelector("label[for='" + id + "']"));
                if (!labels.isEmpty()) { js.executeScript("arguments[0].click();", labels.get(0)); debug("Clicked label[for='" + id + "'] ✓"); }
                else { js.executeScript("arguments[0].click();", radio); debug("Clicked radio[" + idx + "] ✓"); }
            } catch (StaleElementReferenceException e) {
                js.executeScript("arguments[0].click();", radio);
            }
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            clickSaveButton(driver, js);
            return true;
        } catch (Exception e) { debug("Radio click error: " + e.getMessage()); return false; }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // TYPE 4: SUGGESTION CHIP / d-none input handling
    // ─────────────────────────────────────────────────────────────────────────────
    private static boolean trySuggestionChip(WebDriver driver, JavascriptExecutor js,
                                             String answer, Actions actions) {
        List<WebElement> containers = driver.findElements(By.cssSelector("div.chatbot_SendMessageContainer"));
        boolean inputHidden = !containers.isEmpty() &&
                safeAttr(containers.get(0), "class").contains("d-none");
        if (!inputHidden) return false;

        List<WebElement> suggestions = driver.findElements(By.cssSelector(
                ".chatbot_SuggestionChip,.suggestion-chip,[class*='SuggestionChip']," +
                        ".chatbot_prefill,.prefillChip,.chatbot_EditChip,[class*='editChip']"));
        if (!suggestions.isEmpty()) {
            debug("Suggestion chip found: [" + suggestions.get(0).getText() + "]");
            js.executeScript("arguments[0].click();", suggestions.get(0));
            return true;
        }

        debug("d-none input, no suggestion chip — force-removing d-none");
        js.executeScript(
                "document.querySelectorAll('.chatbot_SendMessageContainer')" +
                        ".forEach(function(el){ el.classList.remove('d-none'); });");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // TYPE 5: TEXT INPUT — div.textArea[contenteditable='true']
    //
    // FIX: Every single operation re-finds the element fresh.
    // Never hold a WebElement reference across a sleep or JS call.
    // Use JS click instead of .click() to avoid "element click intercepted".
    // ─────────────────────────────────────────────────────────────────────────────
    private static boolean tryTextInput(WebDriver driver, JavascriptExecutor js,
                                        Actions actions, String answer) {
        // Check d-none
        List<WebElement> containers = driver.findElements(By.cssSelector("div.chatbot_SendMessageContainer"));
        if (!containers.isEmpty() && safeAttr(containers.get(0), "class").contains("d-none")) {
            debug("Text input still d-none — skipping."); return false;
        }

        // Verify input exists and is displayed
        List<WebElement> inputs = driver.findElements(By.cssSelector("div.textArea[contenteditable='true']"));
        if (inputs.isEmpty()) { debug("No text input found."); return false; }
        try { if (!inputs.get(0).isDisplayed()) { debug("Text input not displayed."); return false; } }
        catch (Exception e) { debug("Text input visibility check failed."); return false; }

        debug("Text input found: id=[" + safeAttr(inputs.get(0), "id") + "]");

        try {
            // Step 1: scroll + focus + clear — re-find fresh
            js.executeScript(
                    "var el = document.querySelector('div.textArea[contenteditable=\"true\"]');" +
                            "if(el){ el.scrollIntoView({block:'center'}); el.focus(); el.innerHTML=''; el.textContent=''; }"
            );
            Thread.sleep(300);

            // Step 2: JS click to place caret — avoids "element click intercepted"
            js.executeScript(
                    "var el = document.querySelector('div.textArea[contenteditable=\"true\"]');" +
                            "if(el){ el.click(); }"
            );
            Thread.sleep(200);

            // Step 3: Re-find fresh element and type with Actions
            List<WebElement> freshInputs = driver.findElements(
                    By.cssSelector("div.textArea[contenteditable='true']"));
            if (freshInputs.isEmpty()) { debug("Input disappeared before typing."); return false; }
            actions.moveToElement(freshInputs.get(0)).sendKeys(answer).perform();
            Thread.sleep(400);

            // Step 4: Dispatch events via pure JS — no element reference held
            js.executeScript(
                    "var el = document.querySelector('div.textArea[contenteditable=\"true\"]');" +
                            "if(el){" +
                            "  el.dispatchEvent(new InputEvent('input',{bubbles:true,data:'" + escapeJs(answer) + "',inputType:'insertText'}));" +
                            "  el.dispatchEvent(new Event('change',{bubbles:true}));" +
                            "  el.dispatchEvent(new Event('keyup',{bubbles:true}));" +
                            "}"
            );
            Thread.sleep(500);

            // Step 5: remove disabled + click save
            js.executeScript(
                    "document.querySelectorAll('div.send').forEach(function(el){el.classList.remove('disabled');});");
            Thread.sleep(200);

            // Step 6: Verify typed content
            Object typed = js.executeScript(
                    "var el = document.querySelector('div.textArea[contenteditable=\"true\"]');" +
                            "return el ? (el.textContent || el.innerText) : '';");
            debug("Content after typing: [" + typed + "]");

            clickSaveButton(driver, js);
            return true;

        } catch (Exception e) {
            debug("Text input ERROR: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Save button
    // ─────────────────────────────────────────────────────────────────────────────
    private static void clickSaveButton(WebDriver driver, JavascriptExecutor js) {
        debug("Clicking Save button...");
        try {
            js.executeScript("document.querySelectorAll('div.send').forEach(function(el){el.classList.remove('disabled');});");

            List<WebElement> divBtns = driver.findElements(By.cssSelector("div.sendMsg"));
            for (WebElement btn : divBtns) {
                try { if (btn.isDisplayed()) { js.executeScript("arguments[0].click();", btn); debug("Clicked div.sendMsg ✓"); return; } }
                catch (StaleElementReferenceException ignored) {}
            }

            List<WebElement> buttonSave = driver.findElements(By.xpath(
                    "//button[normalize-space(text())='Save' or normalize-space(.)='Save']"));
            for (WebElement btn : buttonSave) {
                try { if (btn.isDisplayed()) { js.executeScript("arguments[0].click();", btn); debug("Clicked <button>Save ✓"); return; } }
                catch (StaleElementReferenceException ignored) {}
            }

            debug("No visible Save button — bot may auto-advance.");
        } catch (Exception e) { debug("Save button error: " + e.getMessage()); }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────────
    private static boolean isAcknowledgment(String q) {
        String lower = q.toLowerCase().trim();
        for (String phrase : ACK_PHRASES) {
            if (lower.startsWith(phrase) || lower.equals(phrase)) return true;
        }
        return false;
    }

    private static boolean isDrawerClosing(WebDriver driver) {
        List<WebElement> drawers = driver.findElements(By.cssSelector(".chatbot_Drawer"));
        if (drawers.isEmpty()) return true;
        try {
            String display = drawers.get(0).getCssValue("display");
            return "none".equals(display);
        } catch (Exception e) { return true; }
    }

    private static String getLatestQuestion(WebDriver driver) {
        List<WebElement> spans = driver.findElements(By.cssSelector("li.botItem .botMsg span"));
        debug("li.botItem .botMsg span count: " + spans.size());
        for (int i = spans.size() - 1; i >= 0; i--) {
            try { String t = spans.get(i).getText().trim(); if (!t.isBlank()) return t; }
            catch (StaleElementReferenceException ignored) {}
        }
        return null;
    }

    private static void waitForBotIdle(WebDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8)).until(d ->
                    d.findElements(By.cssSelector(".botTyping,.typing-indicator,.bot-typing")).isEmpty());
        } catch (Exception ignored) {}
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
    }

    private static void waitForNewBotMessage(WebDriver driver, int prevCount) {
        debug("Waiting for new bot message (prev=" + prevCount + ")...");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8)).until(d ->
                    d.findElements(By.cssSelector("li.botItem .botMsg span")).size() > prevCount);
            debug("New bot message received ✓");
        } catch (TimeoutException e) { debug("No new message within 8s — continuing."); }
    }

    private static void waitForDrawerClose(WebDriver driver) {
        debug("Waiting for drawer to close...");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
                List<WebElement> drawers = d.findElements(By.cssSelector(".chatbot_Drawer"));
                if (drawers.isEmpty()) return true;
                try { return "none".equals(drawers.get(0).getCssValue("display")); }
                catch (Exception e) { return true; }
            });
            debug("Drawer closed ✓ — application submitted.");
        } catch (TimeoutException e) { debug("Drawer did not close within 15s — moving on."); }
    }

    private static int getBotMessageCount(WebDriver driver) {
        try { return driver.findElements(By.cssSelector("li.botItem .botMsg span")).size(); }
        catch (Exception e) { return 0; }
    }

    private static void switchToChatbotFrameIfPresent(WebDriver driver) {
        for (WebElement frame : driver.findElements(By.tagName("iframe"))) {
            try {
                String src = frame.getAttribute("src");
                if (src != null && src.toLowerCase().contains("chatbot")) {
                    driver.switchTo().frame(frame); debug("Switched into chatbot iframe."); return;
                }
            } catch (Exception ignored) {}
        }
        debug("No chatbot iframe — using main DOM.");
    }

    private static boolean isChatDone(String q) {
        String l = q.toLowerCase();
        return l.contains("applied successfully") || l.contains("your application has been");
    }

    private static String[] buildCandidates(String answer) {
        return switch (answer.toLowerCase().trim()) {
            case "yes"             -> new String[]{"Yes","yes"};
            case "no"              -> new String[]{"No","no"};
            case "male"            -> new String[]{"Male","male"};
            case "female"          -> new String[]{"Female","female"};
            case "30","30 days"    -> new String[]{"30 days","1 month","1 Month","30"};
            case "15","15 days"    -> new String[]{"15 days","15 Days or less","15"};
            case "45","45 days"    -> new String[]{"45 days","45"};
            case "60","60 days"    -> new String[]{"60 days","2 months","2 Months","60"};
            case "75","75 days"    -> new String[]{"75 days","3 months","3 Months","75"};
            case "0","immediately" -> new String[]{"Immediately","0 days","Immediate"};
            case "chennai"         -> new String[]{"Chennai","chennai","tamil nadu"};
            case "bengaluru"       -> new String[]{"Bengaluru","bangalore"};
            case "mumbai"          -> new String[]{"Mumbai","bombay"};
            case "hyderabad"       -> new String[]{"Hyderabad"};
            default                -> new String[]{answer};
        };
    }

    private static void dumpPageState(WebDriver driver) {
        debug("--- PAGE STATE DUMP ---");
        List<WebElement> chips = driver.findElements(By.cssSelector("div.chatbot_Chip"));
        debug("Chips: " + chips.size());
        for (WebElement c : chips) { try { debug("  chip: [" + c.getText().trim() + "]"); } catch (Exception ignored) {} }
        debug("Checkboxes: " + driver.findElements(By.cssSelector("input[type='checkbox']")).size());
        List<WebElement> radios = driver.findElements(By.cssSelector("input.ssrc__radio"));
        debug("Radios: " + radios.size());
        for (WebElement r : radios) debug("  radio: id=[" + safeAttr(r,"id") + "]");
        List<WebElement> containers = driver.findElements(By.cssSelector("div.chatbot_SendMessageContainer"));
        debug("SendMessageContainers: " + containers.size());
        for (WebElement c : containers) debug("  class=[" + safeAttr(c,"class") + "]");
        List<WebElement> inputs = driver.findElements(By.cssSelector("div.textArea[contenteditable='true']"));
        debug("Text inputs: " + inputs.size());
        for (WebElement i : inputs) { try { debug("  id=[" + safeAttr(i,"id") + "] displayed=[" + i.isDisplayed() + "]"); } catch (Exception ignored) {} }
        debug("--- END DUMP ---");
    }

    private static String safeAttr(WebElement el, String attr) {
        try { String v = el.getAttribute(attr); return v == null ? "" : v; }
        catch (Exception e) { return ""; }
    }

    // Escape answer string for safe JS string injection
    private static String escapeJs(String s) {
        return s.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static void debug(String msg) { System.out.println("[ChatBot] " + msg); }
}