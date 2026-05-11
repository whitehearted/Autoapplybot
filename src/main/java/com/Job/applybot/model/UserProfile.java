//package com.Job.applybot.model;
//
//import java.util.Map;
//
///**
// * UserProfile — receives all data POSTed from the frontend form.
// * The frontend sends one JSON object to POST /start.
// * This DTO is passed to AnswerEngine and Bot at runtime.
// */
//public class UserProfile {
//
//    // ── Naukri login ──────────────────────────────────────────────────────────
//    private String naukriEmail;
//    private String naukriPassword;
//
//    // ── Job search ────────────────────────────────────────────────────────────
//    private String  role;
//    private String  location;
//    private String  exp;
//    private boolean wfh;
//    private int     maxPages;
//
//    // ── Personal ──────────────────────────────────────────────────────────────
//    private String fullName;
//    private String dob;
//    private String gender;
//    private String nationality;
//    private String email;
//    private String phone;
//    private String city;
//    private String preferredLocation;
//
//    // ── Employment ────────────────────────────────────────────────────────────
//    private String  company;
//    private String  designation;
//    private double  currentCtc;
//    private double  expectedCtc;
//    private String  notice;
//
//    // ── Education ─────────────────────────────────────────────────────────────
//    private String degree;
//    private String specialization;
//    private String college;
//    private String passoutYear;
//    private String cgpa;
//    private String percentage;
//
//    // ── Preferences (toggles) ─────────────────────────────────────────────────
//    private boolean relocate;
//    private boolean shift;
//    private boolean wfo;
//    private boolean careerBreak;
//    private boolean employed;
//
//    // ── Skill experience map ──────────────────────────────────────────────────
//    // Key: skill name (lowercase), Value: years (0.0 = no experience)
//    private Map<String, Double> skillExperience;
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Getters & Setters
//    // ─────────────────────────────────────────────────────────────────────────
//    public String getNaukriEmail()               { return naukriEmail; }
//    public void setNaukriEmail(String v)         { this.naukriEmail = v; }
//
//    public String getNaukriPassword()            { return naukriPassword; }
//    public void setNaukriPassword(String v)      { this.naukriPassword = v; }
//
//    public String getRole()                      { return role; }
//    public void setRole(String v)                { this.role = v; }
//
//    public String getLocation()                  { return location; }
//    public void setLocation(String v)            { this.location = v; }
//
//    public String getExp()                       { return exp; }
//    public void setExp(String v)                 { this.exp = v; }
//
//    public boolean isWfh()                       { return wfh; }
//    public void setWfh(boolean v)                { this.wfh = v; }
//
//    public int getMaxPages()                     { return maxPages; }
//    public void setMaxPages(int v)               { this.maxPages = v; }
//
//    public String getFullName()                  { return fullName; }
//    public void setFullName(String v)            { this.fullName = v; }
//
//    public String getDob()                       { return dob; }
//    public void setDob(String v)                 { this.dob = v; }
//
//    public String getGender()                    { return gender; }
//    public void setGender(String v)              { this.gender = v; }
//
//    public String getNationality()               { return nationality; }
//    public void setNationality(String v)         { this.nationality = v; }
//
//    public String getEmail()                     { return email; }
//    public void setEmail(String v)               { this.email = v; }
//
//    public String getPhone()                     { return phone; }
//    public void setPhone(String v)               { this.phone = v; }
//
//    public String getCity()                      { return city; }
//    public void setCity(String v)                { this.city = v; }
//
//    public String getPreferredLocation()         { return preferredLocation; }
//    public void setPreferredLocation(String v)   { this.preferredLocation = v; }
//
//    public String getCompany()                   { return company; }
//    public void setCompany(String v)             { this.company = v; }
//
//    public String getDesignation()               { return designation; }
//    public void setDesignation(String v)         { this.designation = v; }
//
//    public double getCurrentCtc()                { return currentCtc; }
//    public void setCurrentCtc(double v)          { this.currentCtc = v; }
//
//    public double getExpectedCtc()               { return expectedCtc; }
//    public void setExpectedCtc(double v)         { this.expectedCtc = v; }
//
//    public String getNotice()                    { return notice; }
//    public void setNotice(String v)              { this.notice = v; }
//
//    public String getDegree()                    { return degree; }
//    public void setDegree(String v)              { this.degree = v; }
//
//    public String getSpecialization()            { return specialization; }
//    public void setSpecialization(String v)      { this.specialization = v; }
//
//    public String getCollege()                   { return college; }
//    public void setCollege(String v)             { this.college = v; }
//
//    public String getPassoutYear()               { return passoutYear; }
//    public void setPassoutYear(String v)         { this.passoutYear = v; }
//
//    public String getCgpa()                      { return cgpa; }
//    public void setCgpa(String v)                { this.cgpa = v; }
//
//    public String getPercentage()                { return percentage; }
//    public void setPercentage(String v)          { this.percentage = v; }
//
//    public boolean isRelocate()                  { return relocate; }
//    public void setRelocate(boolean v)           { this.relocate = v; }
//
//    public boolean isShift()                     { return shift; }
//    public void setShift(boolean v)              { this.shift = v; }
//
//    public boolean isWfo()                       { return wfo; }
//    public void setWfo(boolean v)                { this.wfo = v; }
//
//    public boolean isCareerBreak()               { return careerBreak; }
//    public void setCareerBreak(boolean v)        { this.careerBreak = v; }
//
//    public boolean isEmployed()                  { return employed; }
//    public void setEmployed(boolean v)           { this.employed = v; }
//
//    public Map<String, Double> getSkillExperience()          { return skillExperience; }
//    public void setSkillExperience(Map<String, Double> v)    { this.skillExperience = v; }
//}

//package com.Job.applybot.model;
//
//import java.util.Map;
//
///**
// * UserProfile — receives all data POSTed from the frontend form.
// * The frontend sends one JSON object to POST /start.
// * This DTO is passed to AnswerEngine and Bot at runtime.
// */
//public class UserProfile {
//
//    // ── Naukri login ──────────────────────────────────────────────────────────
//    private String naukriEmail;
//    private String naukriPassword;
//
//    // ── Job search ────────────────────────────────────────────────────────────
//    private String  role;
//    private String  location;
//    private String  exp;
//    private boolean wfh;
//    private int     maxPages;
//    private boolean recommendedOnly;    // apply to recommended jobs only
//    private boolean includeRecommended; // also apply to recommended after search
//
//    // ── Personal ──────────────────────────────────────────────────────────────
//    private String fullName;
//    private String dob;
//    private String gender;
//    private String nationality;
//    private String email;
//    private String phone;
//    private String city;
//    private String preferredLocation;
//
//    // ── Employment ────────────────────────────────────────────────────────────
//    private String  company;
//    private String  designation;
//    private double  currentCtc;
//    private double  expectedCtc;
//    private String  notice;
//
//    // ── Education ─────────────────────────────────────────────────────────────
//    private String degree;
//    private String specialization;
//    private String college;
//    private String passoutYear;
//    private String cgpa;
//    private String percentage;
//
//    // ── Preferences (toggles) ─────────────────────────────────────────────────
//    private boolean relocate;
//    private boolean shift;
//    private boolean wfo;
//    private boolean careerBreak;
//    private boolean employed;
//
//    // ── Skill experience map ──────────────────────────────────────────────────
//    // Key: skill name (lowercase), Value: years (0.0 = no experience)
//    private Map<String, Double> skillExperience;
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Getters & Setters
//    // ─────────────────────────────────────────────────────────────────────────
//    public String getNaukriEmail()               { return naukriEmail; }
//    public void setNaukriEmail(String v)         { this.naukriEmail = v; }
//
//    public String getNaukriPassword()            { return naukriPassword; }
//    public void setNaukriPassword(String v)      { this.naukriPassword = v; }
//
//    public String getRole()                      { return role; }
//    public void setRole(String v)                { this.role = v; }
//
//    public String getLocation()                  { return location; }
//    public void setLocation(String v)            { this.location = v; }
//
//    public String getExp()                       { return exp; }
//    public void setExp(String v)                 { this.exp = v; }
//
//    public boolean isWfh()                       { return wfh; }
//    public void setWfh(boolean v)                { this.wfh = v; }
//
//    public int getMaxPages()                     { return maxPages; }
//    public void setMaxPages(int v)               { this.maxPages = v; }
//
//    public String getFullName()                  { return fullName; }
//    public void setFullName(String v)            { this.fullName = v; }
//
//    public String getDob()                       { return dob; }
//    public void setDob(String v)                 { this.dob = v; }
//
//    public String getGender()                    { return gender; }
//    public void setGender(String v)              { this.gender = v; }
//
//    public String getNationality()               { return nationality; }
//    public void setNationality(String v)         { this.nationality = v; }
//
//    public String getEmail()                     { return email; }
//    public void setEmail(String v)               { this.email = v; }
//
//    public String getPhone()                     { return phone; }
//    public void setPhone(String v)               { this.phone = v; }
//
//    public String getCity()                      { return city; }
//    public void setCity(String v)                { this.city = v; }
//
//    public String getPreferredLocation()         { return preferredLocation; }
//    public void setPreferredLocation(String v)   { this.preferredLocation = v; }
//
//    public String getCompany()                   { return company; }
//    public void setCompany(String v)             { this.company = v; }
//
//    public String getDesignation()               { return designation; }
//    public void setDesignation(String v)         { this.designation = v; }
//
//    public double getCurrentCtc()                { return currentCtc; }
//    public void setCurrentCtc(double v)          { this.currentCtc = v; }
//
//    public double getExpectedCtc()               { return expectedCtc; }
//    public void setExpectedCtc(double v)         { this.expectedCtc = v; }
//
//    public String getNotice()                    { return notice; }
//    public void setNotice(String v)              { this.notice = v; }
//
//    public String getDegree()                    { return degree; }
//    public void setDegree(String v)              { this.degree = v; }
//
//    public String getSpecialization()            { return specialization; }
//    public void setSpecialization(String v)      { this.specialization = v; }
//
//    public String getCollege()                   { return college; }
//    public void setCollege(String v)             { this.college = v; }
//
//    public String getPassoutYear()               { return passoutYear; }
//    public void setPassoutYear(String v)         { this.passoutYear = v; }
//
//    public String getCgpa()                      { return cgpa; }
//    public void setCgpa(String v)                { this.cgpa = v; }
//
//    public String getPercentage()                { return percentage; }
//    public void setPercentage(String v)          { this.percentage = v; }
//
//    public boolean isRelocate()                  { return relocate; }
//    public void setRelocate(boolean v)           { this.relocate = v; }
//
//    public boolean isShift()                     { return shift; }
//    public void setShift(boolean v)              { this.shift = v; }
//
//    public boolean isWfo()                       { return wfo; }
//    public void setWfo(boolean v)                { this.wfo = v; }
//
//    public boolean isCareerBreak()               { return careerBreak; }
//    public void setCareerBreak(boolean v)        { this.careerBreak = v; }
//
//    public boolean isEmployed()                  { return employed; }
//    public void setEmployed(boolean v)           { this.employed = v; }
//
//    public boolean isRecommendedOnly()              { return recommendedOnly; }
//    public void setRecommendedOnly(boolean v)       { this.recommendedOnly = v; }
//
//    public boolean isIncludeRecommended()           { return includeRecommended; }
//    public void setIncludeRecommended(boolean v)    { this.includeRecommended = v; }
//
//    public Map<String, Double> getSkillExperience()          { return skillExperience; }
//    public void setSkillExperience(Map<String, Double> v)    { this.skillExperience = v; }
//}


package com.Job.applybot.model;

import java.util.Map;

/**
 * UserProfile — receives all data POSTed from the frontend form.
 * The frontend sends one JSON object to POST /start.
 * This DTO is passed to AnswerEngine and Bot at runtime.
 */
public class UserProfile {

    // ── Naukri login ──────────────────────────────────────────────────────────
    private String naukriEmail;
    private String naukriPassword;

    // ── Job search ────────────────────────────────────────────────────────────
    private String  role;
    private String  location;
    private String  exp;
    private boolean wfh;
    private int     maxPages;
    private String  customUrl;          // custom Naukri search URL
    private boolean recommendedOnly;    // apply to recommended jobs only
    private boolean includeRecommended; // also apply to recommended after search

    // ── Personal ──────────────────────────────────────────────────────────────
    private String fullName;
    private String dob;
    private String gender;
    private String nationality;
    private String email;
    private String phone;
    private String city;
    private String preferredLocation;

    // ── Employment ────────────────────────────────────────────────────────────
    private String  company;
    private String  designation;
    private double  currentCtc;
    private double  expectedCtc;
    private String  notice;

    // ── Education ─────────────────────────────────────────────────────────────
    private String degree;
    private String specialization;
    private String college;
    private String passoutYear;
    private String cgpa;
    private String percentage;

    // ── Preferences (toggles) ─────────────────────────────────────────────────
    private boolean relocate;
    private boolean shift;
    private boolean wfo;
    private boolean careerBreak;
    private boolean employed;

    // ── Skill experience map ──────────────────────────────────────────────────
    // Key: skill name (lowercase), Value: years (0.0 = no experience)
    private Map<String, Double> skillExperience;

    // ─────────────────────────────────────────────────────────────────────────
    // Getters & Setters
    // ─────────────────────────────────────────────────────────────────────────
    public String getNaukriEmail()               { return naukriEmail; }
    public void setNaukriEmail(String v)         { this.naukriEmail = v; }

    public String getNaukriPassword()            { return naukriPassword; }
    public void setNaukriPassword(String v)      { this.naukriPassword = v; }

    public String getRole()                      { return role; }
    public void setRole(String v)                { this.role = v; }

    public String getLocation()                  { return location; }
    public void setLocation(String v)            { this.location = v; }

    public String getExp()                       { return exp; }
    public void setExp(String v)                 { this.exp = v; }

    public boolean isWfh()                       { return wfh; }
    public void setWfh(boolean v)                { this.wfh = v; }

    public int getMaxPages()                     { return maxPages; }
    public void setMaxPages(int v)               { this.maxPages = v; }

    public String getFullName()                  { return fullName; }
    public void setFullName(String v)            { this.fullName = v; }

    public String getDob()                       { return dob; }
    public void setDob(String v)                 { this.dob = v; }

    public String getGender()                    { return gender; }
    public void setGender(String v)              { this.gender = v; }

    public String getNationality()               { return nationality; }
    public void setNationality(String v)         { this.nationality = v; }

    public String getEmail()                     { return email; }
    public void setEmail(String v)               { this.email = v; }

    public String getPhone()                     { return phone; }
    public void setPhone(String v)               { this.phone = v; }

    public String getCity()                      { return city; }
    public void setCity(String v)                { this.city = v; }

    public String getPreferredLocation()         { return preferredLocation; }
    public void setPreferredLocation(String v)   { this.preferredLocation = v; }

    public String getCompany()                   { return company; }
    public void setCompany(String v)             { this.company = v; }

    public String getDesignation()               { return designation; }
    public void setDesignation(String v)         { this.designation = v; }

    public double getCurrentCtc()                { return currentCtc; }
    public void setCurrentCtc(double v)          { this.currentCtc = v; }

    public double getExpectedCtc()               { return expectedCtc; }
    public void setExpectedCtc(double v)         { this.expectedCtc = v; }

    public String getNotice()                    { return notice; }
    public void setNotice(String v)              { this.notice = v; }

    public String getDegree()                    { return degree; }
    public void setDegree(String v)              { this.degree = v; }

    public String getSpecialization()            { return specialization; }
    public void setSpecialization(String v)      { this.specialization = v; }

    public String getCollege()                   { return college; }
    public void setCollege(String v)             { this.college = v; }

    public String getPassoutYear()               { return passoutYear; }
    public void setPassoutYear(String v)         { this.passoutYear = v; }

    public String getCgpa()                      { return cgpa; }
    public void setCgpa(String v)                { this.cgpa = v; }

    public String getPercentage()                { return percentage; }
    public void setPercentage(String v)          { this.percentage = v; }

    public boolean isRelocate()                  { return relocate; }
    public void setRelocate(boolean v)           { this.relocate = v; }

    public boolean isShift()                     { return shift; }
    public void setShift(boolean v)              { this.shift = v; }

    public boolean isWfo()                       { return wfo; }
    public void setWfo(boolean v)                { this.wfo = v; }

    public boolean isCareerBreak()               { return careerBreak; }
    public void setCareerBreak(boolean v)        { this.careerBreak = v; }

    public boolean isEmployed()                  { return employed; }
    public void setEmployed(boolean v)           { this.employed = v; }

    public String getCustomUrl()                  { return customUrl; }
    public void setCustomUrl(String v)            { this.customUrl = v; }

    public boolean isRecommendedOnly()              { return recommendedOnly; }
    public void setRecommendedOnly(boolean v)       { this.recommendedOnly = v; }

    public boolean isIncludeRecommended()           { return includeRecommended; }
    public void setIncludeRecommended(boolean v)    { this.includeRecommended = v; }

    public Map<String, Double> getSkillExperience()          { return skillExperience; }
    public void setSkillExperience(Map<String, Double> v)    { this.skillExperience = v; }
}