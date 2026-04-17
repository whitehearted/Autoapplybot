//package com.Job.applybot.Controller;
//
//import com.Job.applybot.Service.AnswerEngine;
//import com.Job.applybot.bot.Bot;
//import com.Job.applybot.model.UserProfile;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@CrossOrigin(origins = "*")   // allow the HTML file opened from file:// or any origin
//public class JobController {
//
//    private final Bot bot = new Bot();
//
//    /**
//     * POST /start
//     * Receives the full UserProfile JSON from the frontend form.
//     * Builds the Naukri search URL dynamically from the profile, then launches the bot.
//     */
//    @PostMapping("/start")
//    public String start(@RequestBody UserProfile profile) {
//
//        // Push profile into AnswerEngine so it's available for all chatbot questions
//        AnswerEngine.setProfile(profile);
//
//        // Build Naukri search URL from form fields
//        // Example: https://www.naukri.com/java-developer-jobs-in-chennai?experience=0
//        String url = buildNaukriUrl(profile);
//        System.out.println("[Controller] Starting bot for: " + profile.getFullName());
//        System.out.println("[Controller] URL: " + url);
//
//        // Run the bot on a background thread so the HTTP response returns immediately
//        Thread botThread = new Thread(() -> {
//            try {
//                bot.searchjob(url, profile);
//            } catch (Exception e) {
//                System.out.println("[Controller] Bot error: " + e.getMessage());
//            }
//        });
//        botThread.setDaemon(true);
//        botThread.start();
//
//        return "Bot started for " + profile.getFullName() + " — searching: " + url;
//    }
//
//    private String buildNaukriUrl(UserProfile p) {
//        // Naukri URL pattern:
//        //   https://www.naukri.com/<role>-jobs-in-<location>?experience=<exp>&wfhType=2
//        String role     = sanitize(p.getRole(),     "java-developer");
//        String location = sanitize(p.getLocation(), "india");
//        String exp      = sanitize(p.getExp(),      "0");
//
//        String url = "https://www.naukri.com/" + role + "-jobs-in-" + location
//                + "?experience=" + exp;
//
//        if (p.isWfh()) url += "&wfhType=2";
//
//        return url;
//    }
//
//    private String sanitize(String input, String fallback) {
//        if (input == null || input.isBlank()) return fallback;
//        return input.trim().toLowerCase()
//                .replaceAll("\\s+", "-")
//                .replaceAll("[^a-z0-9-]", "");
//    }
//}


package com.Job.applybot.Controller;

import com.Job.applybot.Service.AnswerEngine;
import com.Job.applybot.Service.ProfileStore;
import com.Job.applybot.bot.Bot;
import com.Job.applybot.model.UserProfile;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")   // allow the HTML file opened from file:// or any origin
public class JobController {

    private final Bot bot = new Bot();

    /**
     * POST /start
     * Receives the full UserProfile JSON from the frontend form.
     * Builds the Naukri search URL dynamically from the profile, then launches the bot.
     */
    @PostMapping("/start")
    public String start(@RequestBody UserProfile profile) {

        // Push profile into AnswerEngine so it's available for all chatbot questions
        AnswerEngine.setProfile(profile);
        ProfileStore.save(profile);  // persist profile to Excel for next run

        // Build Naukri search URL from form fields
        // Example: https://www.naukri.com/java-developer-jobs-in-chennai?experience=0
        // Use custom URL if provided, otherwise build from role+location
        String customUrl = profile.getCustomUrl();
        String url = (customUrl != null && !customUrl.isBlank())
                ? customUrl
                : buildNaukriUrl(profile);
        System.out.println("[Controller] Starting bot for: " + profile.getFullName());
        System.out.println("[Controller] URL: " + url);

        // Run the bot on a background thread so the HTTP response returns immediately
        Thread botThread = new Thread(() -> {
            try {
                bot.searchjob(url, profile);
            } catch (Exception e) {
                System.out.println("[Controller] Bot error: " + e.getMessage());
            }
        });
        botThread.setDaemon(true);
        botThread.start();

        return "Bot started for " + profile.getFullName() + " — searching: " + url;
    }

    private String buildNaukriUrl(UserProfile p) {
        // Naukri URL pattern:
        //   https://www.naukri.com/<role>-jobs-in-<location>?experience=<exp>&wfhType=remote
        String role     = sanitize(p.getRole(),     "java-developer");
        String location = sanitize(p.getLocation(), "india");
        String exp      = sanitize(p.getExp(),      "0");

        String url = "https://www.naukri.com/" + role + "-jobs-in-" + location
                + "?experience=" + exp;

        if (p.isWfh()) url += "&wfhType=2";

        return url;
    }

    private String sanitize(String input, String fallback) {
        if (input == null || input.isBlank()) return fallback;
        return input.trim().toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");
    }
}