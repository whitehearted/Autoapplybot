package com.Job.applybot.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ApplicationResult — stores the outcome of one job application attempt.
 * Collected by Bot.java and written to Excel by ApplicationTracker.
 */
public class ApplicationResult {

    public enum Status {
        SUCCESS,            // Applied successfully — chatbot/popup completed
        DIRECT_APPLY,       // Applied directly — no questions asked
        FAILED,             // Apply button clicked but something went wrong
        SKIPPED,            // Already applied / Apply button not found
        REDIRECTED          // Naukri redirected to company's own site
    }

    private final String        username;
    private final String        timestamp;
    private final String        jobTitle;
    private final String        company;
    private final String        jobUrl;
    private final String        redirectUrl;    // company site URL if redirected, else ""
    private final String        finalUrl;       // redirectUrl if redirected, else jobUrl
    private final Status        status;
    private final String        notes;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ApplicationResult(String username, String jobTitle, String company,
                             String jobUrl, String redirectUrl, Status status, String notes) {
        this.username    = username;
        this.timestamp   = LocalDateTime.now().format(FMT);
        this.jobTitle    = jobTitle;
        this.company     = company;
        this.jobUrl      = jobUrl;
        this.redirectUrl = redirectUrl != null ? redirectUrl : "";
        this.finalUrl    = (redirectUrl != null && !redirectUrl.isBlank()) ? redirectUrl : jobUrl;
        this.status      = status;
        this.notes       = notes != null ? notes : "";
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String   getUsername()    { return username; }
    public String   getTimestamp()   { return timestamp; }
    public String   getJobTitle()    { return jobTitle; }
    public String   getCompany()     { return company; }
    public String   getJobUrl()      { return jobUrl; }
    public String   getRedirectUrl() { return redirectUrl; }
    public String   getFinalUrl()    { return finalUrl; }
    public Status   getStatus()      { return status; }
    public String   getStatusLabel() { return status.name(); }
    public String   getNotes()       { return notes; }
}
