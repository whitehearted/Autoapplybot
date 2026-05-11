//package com.Job.applybot.Service;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * AnswerEngine — handles all Naukri chatbot question types.
// *
// * SKILL EXPERIENCE:
// *   Questions like "How many years of experience do you have in Java?"
// *   are matched against SKILL_EXPERIENCE_MAP first.
// *   If the skill is not in the map, DEFAULT_EXPERIENCE is returned.
// *
// *   Edit SKILL_EXPERIENCE_MAP to set your real experience per technology.
// */
//public class AnswerEngine {
//
//    // ── Edit your personal details ────────────────────────────────────────────────
//    private static final String FULL_NAME      = "Manoj Kumar";
//    private static final String FIRST_NAME     = "Manoj";
//    private static final String LAST_NAME      = "Kumar";
//    private static final String EMAIL          = "manojkumarmariappanmk@gmail.com";
//    private static final String PHONE          = "9876543210";
//    private static final String DOB            = "02/04/2000";
//    private static final String GENDER         = "Male";
//    private static final String NATIONALITY    = "Indian";
//    private static final String MARITAL        = "Single";
//    private static final String LOCATION       = "Chennai";
//    private static final String ADDRESS        = "Chennai, Tamil Nadu";
//    private static final String SKILLS         = "Java, Spring Boot, Selenium";
//    private static final String DEGREE         = "B.Tech";
//    private static final String SPECIALIZATION = "Computer Science";
//    private static final String COLLEGE        = "Anna University";
//    private static final String PASSOUT_YEAR   = "2024";
//    private static final String CGPA           = "8.0";
//    private static final String PERCENTAGE     = "80";
//    private static final String CURRENT_CTC    = "0";
//    private static final String EXPECTED_CTC   = "5";       // in Lacs
//    private static final String NOTICE         = "30 days";
//    private static final String COMPANY        = "Fresher";
//    private static final String DESIGNATION    = "Fresher";
//    private static final String LINKEDIN       = "linkedin.com/in/manojkumar";
//    private static final String GITHUB         = "github.com/manojkumar";
//    private static final String ROLE           = "Java Developer";
//    private static final String INDUSTRY       = "IT/Software";
//    private static final String FUNC_AREA      = "IT Software";
//    private static final String CURRENCY       = "INR";
//    private static final String JOIN_DATE      = "Immediately";
//    private static final String TENTH_PERCENT  = "85";
//    private static final String TENTH_YEAR     = "2016";
//    private static final String TWELFTH_PERCENT= "80";
//    private static final String TWELFTH_YEAR   = "2018";
//
//    // ── DEFAULT experience for unknown skills ─────────────────────────────────────
//    private static final String DEFAULT_EXPERIENCE = "0";
//
//    // ── SKILL-SPECIFIC EXPERIENCE MAP ─────────────────────────────────────────────
//    // Key   : lowercase skill name (substring match against question)
//    // Value : years as a string
//    //
//    // HOW TO EDIT:
//    //   Change the value next to the skill you know.
//    //   e.g. "java" -> "2"  means you answer "2" for any question about Java experience.
//    //   Skills you have NO experience in → leave as "0".
//    //   Unknown skills not listed here → DEFAULT_EXPERIENCE ("0") is returned.
//    // ─────────────────────────────────────────────────────────────────────────────
//    private static final Map<String, String> SKILL_EXPERIENCE = new LinkedHashMap<>();
//
//    static {
//        // ── Core Java ─────────────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("java developer",      "1");   // "experience in Java Developer"
//        SKILL_EXPERIENCE.put("java",                "2");   // "experience in Java"
//
//        // ── Spring Ecosystem ──────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("spring boot",         "3");
//        SKILL_EXPERIENCE.put("spring mvc",          "3");
//        SKILL_EXPERIENCE.put("spring security",     "1");
//        SKILL_EXPERIENCE.put("spring cloud",        "2");
//        SKILL_EXPERIENCE.put("spring",              "1");   // catch-all for Spring
//        SKILL_EXPERIENCE.put("spring framework",    "4");
//
//        // ── ORM / Database ────────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("hibernate",           "5");
//        SKILL_EXPERIENCE.put("jpa",                 "5");
//        SKILL_EXPERIENCE.put("jdbc",                "0");
//        SKILL_EXPERIENCE.put("sql",                 "0");
//        SKILL_EXPERIENCE.put("mysql",               "0");
//        SKILL_EXPERIENCE.put("postgresql",          "0");
//        SKILL_EXPERIENCE.put("oracle",              "0");
//        SKILL_EXPERIENCE.put("mongodb",             "0");
//        SKILL_EXPERIENCE.put("redis",               "0");
//        SKILL_EXPERIENCE.put("cassandra",           "0");
//
//        // ── Testing ───────────────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("selenium",            "0");
//        SKILL_EXPERIENCE.put("junit",               "0");
//        SKILL_EXPERIENCE.put("mockito",             "0");
//        SKILL_EXPERIENCE.put("testng",              "0");
//
//        // ── Architecture ──────────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("microservices",       "0");
//        SKILL_EXPERIENCE.put("mvc framework",       "0");
//        SKILL_EXPERIENCE.put("mvc",                 "0");
//        SKILL_EXPERIENCE.put("rest api",            "0");
//        SKILL_EXPERIENCE.put("restful",             "0");
//        SKILL_EXPERIENCE.put("soap",                "0");
//        SKILL_EXPERIENCE.put("graphql",             "0");
//
//        // ── Frontend ──────────────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("react",               "0");
//        SKILL_EXPERIENCE.put("angular",             "0");
//        SKILL_EXPERIENCE.put("javascript",          "0");
//        SKILL_EXPERIENCE.put("typescript",          "0");
//        SKILL_EXPERIENCE.put("node",                "0");
//        SKILL_EXPERIENCE.put("html",                "0");
//        SKILL_EXPERIENCE.put("css",                 "0");
//
//        // ── DevOps / Cloud ────────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("docker",              "2");
//        SKILL_EXPERIENCE.put("kubernetes",          "0");
//        SKILL_EXPERIENCE.put("jenkins",             "0");
//        SKILL_EXPERIENCE.put("aws",                 "0");
//        SKILL_EXPERIENCE.put("azure",               "0");
//        SKILL_EXPERIENCE.put("gcp",                 "0");
//        SKILL_EXPERIENCE.put("kafka",               "0");
//        SKILL_EXPERIENCE.put("rabbitmq",            "0");
//
//        // ── Tools ─────────────────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("git",                 "0");
//        SKILL_EXPERIENCE.put("maven",               "0");
//        SKILL_EXPERIENCE.put("gradle",              "0");
//        SKILL_EXPERIENCE.put("jira",                "0");
//        SKILL_EXPERIENCE.put("linux",               "0");
//
//        // ── Python ───────────────────────────────────────────────────────────────
//        SKILL_EXPERIENCE.put("python",              "0");
//        SKILL_EXPERIENCE.put("django",              "0");
//        SKILL_EXPERIENCE.put("flask",               "0");
//
//        // ── Other technologies (domain specific) ──────────────────────────────────
//        SKILL_EXPERIENCE.put("t24",                 "0");
//        SKILL_EXPERIENCE.put("temenos",             "0");
//        SKILL_EXPERIENCE.put("sap",                 "0");
//        SKILL_EXPERIENCE.put("salesforce",          "0");
//        SKILL_EXPERIENCE.put("servicenow",          "0");
//        SKILL_EXPERIENCE.put("cobol",               "0");
//        SKILL_EXPERIENCE.put("mainframe",           "0");
//        SKILL_EXPERIENCE.put("ab initio",           "0");
//        SKILL_EXPERIENCE.put("informatica",         "0");
//        SKILL_EXPERIENCE.put("tableau",             "0");
//        SKILL_EXPERIENCE.put("power bi",            "0");
//        SKILL_EXPERIENCE.put("machine learning",    "0");
//        SKILL_EXPERIENCE.put("artificial intelligence", "0");
//        SKILL_EXPERIENCE.put("deep learning",       "0");
//        SKILL_EXPERIENCE.put("data science",        "0");
//        SKILL_EXPERIENCE.put("android",             "0");
//        SKILL_EXPERIENCE.put("ios",                 "0");
//        SKILL_EXPERIENCE.put("flutter",             "0");
//        SKILL_EXPERIENCE.put("react native",        "0");
//        SKILL_EXPERIENCE.put("c#",                  "0");
//        SKILL_EXPERIENCE.put("c++",                 "0");
//        SKILL_EXPERIENCE.put("dotnet",              "0");
//        SKILL_EXPERIENCE.put(".net",                "0");
//        SKILL_EXPERIENCE.put("php",                 "0");
//        SKILL_EXPERIENCE.put("ruby",                "0");
//        SKILL_EXPERIENCE.put("scala",               "0");
//        SKILL_EXPERIENCE.put("golang",              "0");
//        SKILL_EXPERIENCE.put("rust",                "0");
//    }
//
//    // ── General Q&A rules ─────────────────────────────────────────────────────────
//    private static final Map<String, String> RULES = new LinkedHashMap<>();
//
//    static {
//        // Name
//        RULES.put("full name",           FULL_NAME);
//        RULES.put("first name",          FIRST_NAME);
//        RULES.put("last name",           LAST_NAME);
//        RULES.put("candidate name",      FULL_NAME);
//        RULES.put("your name",           FULL_NAME);
//
//        // Contact
//        RULES.put("email id",            EMAIL);
//        RULES.put("alternate email",     EMAIL);
//        RULES.put("email",               EMAIL);
//        RULES.put("e-mail",              EMAIL);
//        RULES.put("mobile number",       PHONE);
//        RULES.put("alternate phone",     PHONE);
//        RULES.put("phone number",        PHONE);
//        RULES.put("contact number",      PHONE);
//        RULES.put("emergency contact number", PHONE);
//        RULES.put("emergency contact name",   FULL_NAME);
//        RULES.put("mobile",              PHONE);
//        RULES.put("phone",               PHONE);
//        RULES.put("contact",             PHONE);
//        RULES.put("linkedin",            LINKEDIN);
//        RULES.put("github",              GITHUB);
//        RULES.put("portfolio",           GITHUB);
//
//        // Personal
//        RULES.put("date of birth",       DOB);
//        RULES.put("birth date",          DOB);
//        RULES.put("dob",                 DOB);
//        RULES.put("gender",              GENDER);
//        RULES.put("marital status",      MARITAL);
//        RULES.put("nationality",         NATIONALITY);
//        RULES.put("relationship with",   "Parent");
//
//        // Location
//        RULES.put("current city",        LOCATION);
//        RULES.put("current address",     ADDRESS);
//        RULES.put("permanent address",   ADDRESS);
//        RULES.put("current location",    LOCATION);
//        RULES.put("preferred location",  LOCATION);
//        RULES.put("preferred locations", LOCATION);
//        RULES.put("present location",    LOCATION);
//        RULES.put("reside",              LOCATION);
//        RULES.put("where do you",        LOCATION);
//        RULES.put("location",            LOCATION);
//        RULES.put("city",                LOCATION);
//        RULES.put("pin code",            "600001");
//        RULES.put("pincode",             "600001");
//
//        // Relocation / Travel
//        RULES.put("willing to relocate",      "Yes");
//        RULES.put("open to relocate",         "Yes");
//        RULES.put("open to relocation",       "Yes");
//        RULES.put("relocate",                 "Yes");
//        RULES.put("willing to travel",        "Yes");
//        RULES.put("open to travel",           "Yes");
//        RULES.put("travel",                   "Yes");
//
//        // CTC / Salary
//        RULES.put("current ctc",         CURRENT_CTC);
//        RULES.put("current salary",      CURRENT_CTC);
//        RULES.put("expected ctc",        EXPECTED_CTC);
//        RULES.put("expected salary",     EXPECTED_CTC);
//        RULES.put("minimum expected",    EXPECTED_CTC);
//        RULES.put("maximum expected",    EXPECTED_CTC);
//        RULES.put("preferred salary",    EXPECTED_CTC);
//        RULES.put("annual ctc",          EXPECTED_CTC);
//        RULES.put("annual salary",       EXPECTED_CTC);
//        RULES.put("currency",            CURRENCY);
//        RULES.put("salary",              EXPECTED_CTC);
//        RULES.put("ctc",                 EXPECTED_CTC);
//
//        // Notice
//        RULES.put("notice period",       NOTICE);
//        RULES.put("serving notice",      NOTICE);
//        RULES.put("notice",              NOTICE);
//        RULES.put("last working day",    "Immediate");
//        RULES.put("joining date",        JOIN_DATE);
//        RULES.put("expected joining",    JOIN_DATE);
//        RULES.put("available immediately","Yes");
//        RULES.put("immediate joiner",    "Yes");
//
//        // Experience (generic — skill-specific is handled separately)
//        RULES.put("total years of experience", DEFAULT_EXPERIENCE);
//        RULES.put("relevant experience",       DEFAULT_EXPERIENCE);
//        RULES.put("work experience",           DEFAULT_EXPERIENCE);
//        RULES.put("total experience",          DEFAULT_EXPERIENCE);
//        RULES.put("fresher",                   "Yes");
//
//        // Employment status
//        RULES.put("currently working",    "No");
//        RULES.put("currently employed",   "No");
//        RULES.put("are you currently",    "No");
//        RULES.put("employment type",      "Full Time");
//        RULES.put("internship experience","No");
//        RULES.put("internship company",   "N/A");
//        RULES.put("internship duration",  "N/A");
//        RULES.put("internship role",      "N/A");
//        RULES.put("freelance",            "No");
//        RULES.put("number of projects",   "2");
//
//        // Company / Designation
//        RULES.put("current organization", COMPANY);
//        RULES.put("current company",      COMPANY);
//        RULES.put("company name",         COMPANY);
//        RULES.put("previous company",     "N/A");
//        RULES.put("previous designation", "N/A");
//        RULES.put("designation at",       DESIGNATION);
//        RULES.put("your designation",     DESIGNATION);
//        RULES.put("designation",          DESIGNATION);
//        RULES.put("organization",         COMPANY);
//        RULES.put("employer",             COMPANY);
//
//        // Role / Job preferences
//        RULES.put("role are you applying", ROLE);
//        RULES.put("preferred job role",    ROLE);
//        RULES.put("what role",             ROLE);
//        RULES.put("functional area",       FUNC_AREA);
//        RULES.put("preferred industry",    INDUSTRY);
//        RULES.put("industry",              INDUSTRY);
//        RULES.put("preferred company type","Product/IT Services");
//        RULES.put("preferred shift",       "Day Shift");
//        RULES.put("preferred work mode",   "Hybrid");
//        RULES.put("preferred job type",    "Full Time");
//        RULES.put("open to contract",      "Yes");
//        RULES.put("open to full-time",     "Yes");
//        RULES.put("open to internship",    "No");
//
//        // Shift / Work mode
//        RULES.put("work from office",      "Yes");
//        RULES.put("working from office",   "Yes");
//        RULES.put("work from home",        "Yes");
//        RULES.put("work onsite",           "Yes");
//        RULES.put("work remotely",         "Yes");
//        RULES.put("night shift",           "Yes");
//        RULES.put("open to night",         "Yes");
//        RULES.put("rotational shift",      "Yes");
//        RULES.put("open to rotational",    "Yes");
//        RULES.put("rotational",            "Yes");
//        RULES.put("shift",                 "Yes");
//        RULES.put("overtime",              "Yes");
//
//        // Skills
//        RULES.put("keyskills",             SKILLS);
//        RULES.put("key skills",            SKILLS);
//        RULES.put("technical skills",      SKILLS);
//        RULES.put("primary skills",        SKILLS);
//        RULES.put("skills",                SKILLS);
//        RULES.put("coding languages",      SKILLS);
//        RULES.put("frameworks you know",   SKILLS);
//
//        // Education
//        RULES.put("highest qualification", DEGREE);
//        RULES.put("undergraduate course",  DEGREE);
//        RULES.put("name of your undergraduate", DEGREE);
//        RULES.put("degree",                DEGREE);
//        RULES.put("specialization",        SPECIALIZATION);
//        RULES.put("stream",                SPECIALIZATION);
//        RULES.put("college name",          COLLEGE);
//        RULES.put("university or college", COLLEGE);
//        RULES.put("university name",       COLLEGE);
//        RULES.put("college",               COLLEGE);
//        RULES.put("university",            COLLEGE);
//        RULES.put("year of passout",       PASSOUT_YEAR);
//        RULES.put("year of passing",       PASSOUT_YEAR);
//        RULES.put("passing year",          PASSOUT_YEAR);
//        RULES.put("graduation year",       PASSOUT_YEAR);
//        RULES.put("complete your b.tech",  PASSOUT_YEAR);
//        RULES.put("complete your b.e",     PASSOUT_YEAR);
//        RULES.put("passout",               PASSOUT_YEAR);
//        RULES.put("cgpa",                  CGPA);
//        RULES.put("percentage",            PERCENTAGE);
//        RULES.put("10th board",            "CBSE");
//        RULES.put("10th percentage",       TENTH_PERCENT);
//        RULES.put("10th passing",          TENTH_YEAR);
//        RULES.put("12th board",            "CBSE");
//        RULES.put("12th percentage",       TWELFTH_PERCENT);
//        RULES.put("12th passing",          TWELFTH_YEAR);
//        RULES.put("diploma",               "No");
//        RULES.put("certification",         "No");
//        RULES.put("currently studying",    "No");
//        RULES.put("backlogs",              "No");
//        RULES.put("cleared all subjects",  "Yes");
//        RULES.put("education gap",         "No");
//        RULES.put("academic achievements", "Yes");
//        RULES.put("medium of instruction", "English");
//
//        // Career break
//        RULES.put("career break",          "No");
//        RULES.put("career status",         "No");
//        RULES.put("career gap",            "No");
//        RULES.put("taken career break",    "No");
//        RULES.put("duration of break",     "N/A");
//        RULES.put("reason for break",      "N/A");
//        RULES.put("reason for job change", "Better Opportunity");
//
//        // Legal
//        RULES.put("legally authorized",    "Yes");
//        RULES.put("work authorization",    "Yes");
//        RULES.put("authorized to work",    "Yes");
//        RULES.put("visa sponsorship",      "No");
//        RULES.put("require sponsorship",   "No");
//        RULES.put("government agency",     "No");
//        RULES.put("government",            "No");
//        RULES.put("armed forces",          "No");
//        RULES.put("veteran",               "No");
//        RULES.put("ex-serviceman",         "No");
//
//        // Documents
//        RULES.put("pan card",              "Yes");
//        RULES.put("aadhaar",               "Yes");
//        RULES.put("passport valid",        "Yes");
//        RULES.put("passport",              "Yes");
//        RULES.put("driving license",       "Yes");
//        RULES.put("voter id",              "Yes");
//        RULES.put("documents verified",    "Yes");
//        RULES.put("valid id proof",        "Yes");
//        RULES.put("pan number",            "ABCDE1234F");
//        RULES.put("aadhaar number",        "1234 5678 9012");
//        RULES.put("passport number",       "A1234567");
//
//        // Policies / Background
//        RULES.put("background verification","Yes");
//        RULES.put("agree to company",      "Yes");
//        RULES.put("agree to terms",        "Yes");
//        RULES.put("agree to privacy",      "Yes");
//        RULES.put("terminated",            "No");
//        RULES.put("criminal record",       "No");
//        RULES.put("blacklisted",           "No");
//        RULES.put("disability",            "No");
//        RULES.put("differently abled",     "No");
//        RULES.put("medical condition",     "No");
//
//        // Work preferences
//        RULES.put("comfortable working under targets","Yes");
//        RULES.put("comfortable working in shifts",   "Yes");
//        RULES.put("handled clients",       "No");
//        RULES.put("team handling",         "No");
//        RULES.put("worked in startups",    "No");
//        RULES.put("worked in mnc",         "No");
//
//        // Interview
//        RULES.put("face to face",          "Yes");
//        RULES.put("f2f interview",         "Yes");
//        RULES.put("attend",                "Yes");
//        RULES.put("interview",             "Yes");
//        RULES.put("comfortable",           "Yes");
//
//        // Bond
//        RULES.put("service bond",          "No");
//        RULES.put("bond",                  "No");
//
//        // Resume
//        RULES.put("upload resume",         "skip");
//        RULES.put("please upload",         "skip");
//        RULES.put("resume",                "skip");
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────────
//    public static String getAnswer(String question) {
//        String q = question.toLowerCase().trim();
//        System.out.println("[AnswerEngine] Q: [" + q + "]");
//
//        // ── PRIORITY 1: Skill-specific experience questions ───────────────────────
//        // Pattern: "how many years of experience do you have in <SKILL>"
//        //          "years of experience in <SKILL>"
//        //          "experience do you have in <SKILL>"
//        if (q.contains("how many years") || q.contains("years of experience") ||
//                q.contains("experience do you have in") || q.contains("experience in")) {
//
//            String skillExp = matchSkillExperience(q);
//            if (skillExp != null) {
//                System.out.println("[AnswerEngine] Skill experience matched: [" + skillExp + "]");
//                return log(skillExp);
//            }
//            // No specific skill matched — return default experience
//            System.out.println("[AnswerEngine] Generic experience question — default: " + DEFAULT_EXPERIENCE);
//            return log(DEFAULT_EXPERIENCE);
//        }
//
//        // ── PRIORITY 2: Fast-path keyword rules ───────────────────────────────────
//        if (q.contains("career break") || q.contains("career status") || q.contains("career gap"))
//            return log("No");
//        if (q.contains("current ctc") || q.contains("current salary"))
//            return log(CURRENT_CTC);
//        if (q.contains("expected ctc") || q.contains("expected salary"))
//            return log(EXPECTED_CTC);
//        if (q.contains("currency of your salary") || q.startsWith("currency") ||
//                q.contains("what is the currency"))
//            return log(CURRENCY);
//        if (q.contains("ctc") || q.contains("salary"))
//            return log(EXPECTED_CTC);
//        if (q.contains("notice"))
//            return log(NOTICE);
//        if (q.contains("experience"))
//            return log(DEFAULT_EXPERIENCE);
//        if (q.contains("dob") || q.contains("date of birth") || q.contains("birth date"))
//            return log(DOB);
//        if (q.contains("gender"))
//            return log(GENDER);
//        if (q.contains("relocat"))
//            return log("Yes");
//        if (q.contains("bond"))
//            return log("No");
//        if (q.contains("shift"))
//            return log("Yes");
//        if (q.contains("differently abled") || q.contains("disability"))
//            return log("No");
//        if (q.contains("passout") || q.contains("passing year") || q.contains("graduation year"))
//            return log(PASSOUT_YEAR);
//        if (q.contains("complete your b.tech") || q.contains("complete your b.e"))
//            return log(PASSOUT_YEAR);
//        if (q.contains("which year"))
//            return log(PASSOUT_YEAR);
//        if (q.contains("reside") || q.contains("where do you"))
//            return log(LOCATION);
//        if (q.contains("current location") || q.contains("current city"))
//            return log(LOCATION);
//        if (q.contains("preferred location") || q.contains("preferred locations"))
//            return log(LOCATION);
//        if (q.contains("location") || q.contains("city"))
//            return log(LOCATION);
//        if (q.contains("keyskill") || q.contains("key skill"))
//            return log(SKILLS);
//        if (q.contains("skill") && (q.contains("what") || q.contains("coding") || q.contains("technical")))
//            return log(SKILLS);
//        if (q.contains("phone") || q.contains("mobile"))
//            return log(PHONE);
//        if (q.contains("email") || q.contains("e-mail"))
//            return log(EMAIL);
//        if (q.contains("current company") || q.contains("current organization") || q.contains("company name"))
//            return log(COMPANY);
//        if (q.contains("designation at") || q.contains("your designation"))
//            return log(DESIGNATION);
//        if (q.contains("functional area"))
//            return log(FUNC_AREA);
//        if (q.contains("currently working") || q.contains("currently employed"))
//            return log("No");
//        if (q.contains("undergraduate course") || q.contains("name of your undergraduate"))
//            return log(DEGREE);
//        if (q.contains("specialization"))
//            return log(SPECIALIZATION);
//        if (q.contains("university or college") || q.contains("college name") || q.contains("which university"))
//            return log(COLLEGE);
//        if (q.contains("cgpa"))
//            return log(CGPA);
//        if (q.contains("percentage"))
//            return log(PERCENTAGE);
//        if (q.contains("name"))
//            return log(FULL_NAME);
//        if (q.contains("resume") || q.contains("upload"))
//            return log("skip");
//        if (q.contains("interview") || q.contains("comfortable") || q.contains("face to face") || q.contains("f2f"))
//            return log("Yes");
//        if (q.contains("join in") || q.contains("joining"))
//            return log("Yes");
//        if (q.contains("visa") || q.contains("sponsorship"))
//            return log("No");
//        if (q.contains("legally authorized") || q.contains("work authorization"))
//            return log("Yes");
//        if (q.contains("government"))
//            return log("No");
//        if (q.contains("pan card") || q.contains("aadhaar") || q.contains("passport") ||
//                q.contains("driving license") || q.contains("voter id"))
//            return log("Yes");
//        if (q.contains("background verification"))
//            return log("Yes");
//        if (q.contains("agree to") || q.contains("do you agree"))
//            return log("Yes");
//        if (q.contains("terminated") || q.contains("criminal") || q.contains("blacklisted"))
//            return log("No");
//        if (q.contains("linkedin"))
//            return log(LINKEDIN);
//        if (q.contains("github"))
//            return log(GITHUB);
//
//        // ── PRIORITY 3: Map lookup ────────────────────────────────────────────────
//        for (Map.Entry<String, String> e : RULES.entrySet()) {
//            if (q.contains(e.getKey())) {
//                System.out.println("[AnswerEngine] Rule matched: [" + e.getKey() + "]");
//                return log(e.getValue());
//            }
//        }
//
//        System.out.println("[AnswerEngine] No match — default: Yes");
//        return "Yes";
//    }
//
//    /**
//     * Extracts the skill name from an experience question and looks it up
//     * in SKILL_EXPERIENCE_MAP.
//     *
//     * Examples handled:
//     *   "how many years of experience do you have in spring boot?" → skill = "spring boot"
//     *   "how many years of experience do you have in java developer?" → skill = "java developer"
//     *   "years of experience in t24?" → skill = "t24"
//     */
//    private static String matchSkillExperience(String q) {
//        // Extract everything after "in " as the candidate skill
//        String skillPart = null;
//
//        if (q.contains(" in ")) {
//            // Get the part after the last " in "
//            int idx = q.lastIndexOf(" in ");
//            skillPart = q.substring(idx + 4).trim();
//            // Remove trailing punctuation
//            skillPart = skillPart.replaceAll("[?!.,;:]+$", "").trim();
//            System.out.println("[AnswerEngine] Extracted skill candidate: [" + skillPart + "]");
//        }
//
//        if (skillPart == null || skillPart.isBlank()) return null;
//
//        // Match against SKILL_EXPERIENCE map — longest match wins
//        String bestKey = null;
//        int bestLen = 0;
//        for (String key : SKILL_EXPERIENCE.keySet()) {
//            if (skillPart.contains(key) || key.contains(skillPart)) {
//                if (key.length() > bestLen) {
//                    bestLen = key.length();
//                    bestKey = key;
//                }
//            }
//        }
//
//        if (bestKey != null) {
//            System.out.println("[AnswerEngine] Skill map match: [" + bestKey + "] = [" + SKILL_EXPERIENCE.get(bestKey) + "]");
//            return SKILL_EXPERIENCE.get(bestKey);
//        }
//
//        // No match in map — return default
//        System.out.println("[AnswerEngine] Skill [" + skillPart + "] not in map — using default: " + DEFAULT_EXPERIENCE);
//        return DEFAULT_EXPERIENCE;
//    }
//
//    private static String log(String answer) {
//        System.out.println("[AnswerEngine] Answer: [" + answer + "]");
//        return answer;
//    }
//}

//package com.Job.applybot.Service;
//
//import com.Job.applybot.model.UserProfile;
//import java.util.Map;
//
//public class AnswerEngine {
//
//    private static UserProfile profile;
//
//    public static void setProfile(UserProfile p) {
//        profile = p;
//        System.out.println("[AnswerEngine] Profile loaded for: " + p.getFullName());
//    }
//
//    public static String getAnswer(String question) {
//        if (profile == null) { System.out.println("[AnswerEngine] No profile — returning 0"); return "0"; }
//
//        String q = question.toLowerCase().trim();
//        System.out.println("[AnswerEngine] Q: [" + q + "]");
//
//        // Priority 1: skill-specific experience
//        if (q.contains("how many years") || q.contains("years of experience") ||
//                q.contains("experience do you have in") || q.contains("experience in")) {
//            String exp = matchSkillExperience(q);
//            return log(exp != null ? exp : "0");
//        }
//
//        // Priority 2: fast-path rules
//        if (q.contains("career break") || q.contains("career status") || q.contains("career gap"))
//            return log(bool(!profile.isCareerBreak()));
//        if (q.contains("current ctc") || q.contains("current salary"))
//            return log(ctcStr(profile.getCurrentCtc()));
//        if (q.contains("expected ctc") || q.contains("expected salary"))
//            return log(ctcStr(profile.getExpectedCtc()));
//        if (q.contains("currency"))
//            return log("INR");
//        if (q.contains("ctc") || q.contains("salary"))
//            return log(ctcStr(profile.getExpectedCtc()));
//        if (q.contains("notice"))
//            return log(profile.getNotice());
//        if (q.contains("experience"))
//            return log("0");
//        if (q.contains("dob") || q.contains("date of birth") || q.contains("birth date"))
//            return log(profile.getDob());
//        if (q.contains("gender"))
//            return log(profile.getGender());
//        if (q.contains("relocat"))
//            return log(bool(profile.isRelocate()));
//        if (q.contains("bond"))
//            return log("No");
//        if (q.contains("shift") || q.contains("rotational"))
//            return log(bool(profile.isShift()));
//        if (q.contains("work from office") || q.contains("working from office") || q.contains("work onsite"))
//            return log(bool(profile.isWfo()));
//        if (q.contains("differently abled") || q.contains("disability"))
//            return log("No");
//        if (q.contains("passout") || q.contains("passing year") || q.contains("graduation year") ||
//                q.contains("complete your b.tech") || q.contains("which year"))
//            return log(profile.getPassoutYear());
//        if (q.contains("reside") || q.contains("where do you") || q.contains("current city"))
//            return log(profile.getCity());
//        if (q.contains("preferred location") || q.contains("preferred locations"))
//            return log(profile.getPreferredLocation());
//        if (q.contains("location") || q.contains("city"))
//            return log(profile.getCity());
//        if (q.contains("keyskill") || q.contains("key skill"))
//            return log("Java, Spring Boot, Selenium");
//        if (q.contains("phone") || q.contains("mobile"))
//            return log(profile.getPhone());
//        if (q.contains("email") || q.contains("e-mail"))
//            return log(profile.getEmail());
//        if (q.contains("current company") || q.contains("current organization") || q.contains("company name"))
//            return log(profile.getCompany());
//        if (q.contains("designation at") || q.contains("your designation") || q.contains("designation"))
//            return log(profile.getDesignation());
//        if (q.contains("functional area"))
//            return log("IT Software");
//        if (q.contains("currently working") || q.contains("currently employed"))
//            return log(bool(profile.isEmployed()));
//        if (q.contains("undergraduate course") || q.contains("name of your undergraduate"))
//            return log(profile.getDegree());
//        if (q.contains("specialization") || q.contains("stream"))
//            return log(profile.getSpecialization());
//        if (q.contains("university or college") || q.contains("college name") || q.contains("which university"))
//            return log(profile.getCollege());
//        if (q.contains("cgpa"))
//            return log(orDefault(profile.getCgpa(), "8.0"));
//        if (q.contains("percentage"))
//            return log(orDefault(profile.getPercentage(), "80"));
//        if (q.contains("full name"))
//            return log(profile.getFullName());
//        if (q.contains("name") && !q.contains("company") && !q.contains("college") &&
//                !q.contains("university") && !q.contains("organization"))
//            return log(profile.getFullName());
//        if (q.contains("nationality"))
//            return log(orDefault(profile.getNationality(), "Indian"));
//        if (q.contains("resume") || q.contains("upload"))
//            return log("skip");
//        if (q.contains("interview") || q.contains("comfortable") || q.contains("face to face") || q.contains("f2f"))
//            return log("Yes");
//        if (q.contains("join in") || q.contains("joining"))
//            return log("Yes");
//        if (q.contains("visa") || q.contains("sponsorship"))
//            return log("No");
//        if (q.contains("legally authorized") || q.contains("work authorization"))
//            return log("Yes");
//        if (q.contains("government"))
//            return log("No");
//        if (q.contains("pan card") || q.contains("aadhaar") || q.contains("passport") ||
//                q.contains("driving license") || q.contains("voter id"))
//            return log("Yes");
//        if (q.contains("background verification") || q.contains("agree to") || q.contains("do you agree"))
//            return log("Yes");
//        if (q.contains("terminated") || q.contains("criminal") || q.contains("blacklisted"))
//            return log("No");
//
//        System.out.println("[AnswerEngine] No match — default: Yes");
//        return "Yes";
//    }
//
//    private static String matchSkillExperience(String q) {
//        Map<String, Double> skillMap = profile.getSkillExperience();
//        if (skillMap == null || skillMap.isEmpty()) return "0";
//        String skillPart = null;
//        if (q.contains(" in ")) {
//            int idx = q.lastIndexOf(" in ");
//            skillPart = q.substring(idx + 4).trim().replaceAll("[?!.,;:]+$", "").trim();
//            System.out.println("[AnswerEngine] Skill candidate: [" + skillPart + "]");
//        }
//        if (skillPart == null || skillPart.isBlank()) return "0";
//        String bestKey = null; int bestLen = 0;
//        for (String key : skillMap.keySet()) {
//            if (skillPart.contains(key) || key.contains(skillPart)) {
//                if (key.length() > bestLen) { bestLen = key.length(); bestKey = key; }
//            }
//        }
//        if (bestKey != null) {
//            double y = skillMap.getOrDefault(bestKey, 0.0);
//            String r = y == Math.floor(y) ? String.valueOf((int) y) : String.valueOf(y);
//            System.out.println("[AnswerEngine] [" + bestKey + "] = " + r + " yrs");
//            return r;
//        }
//        return "0";
//    }
//
//    private static String bool(boolean b)         { return b ? "Yes" : "No"; }
//    private static String ctcStr(double c)        { return c == Math.floor(c) ? String.valueOf((int)c) : String.valueOf(c); }
//    private static String orDefault(String v, String d) { return (v != null && !v.isBlank()) ? v : d; }
//    private static String log(String a)           { System.out.println("[AnswerEngine] Answer: [" + a + "]"); return a; }
//}



package com.Job.applybot.Service;

import com.Job.applybot.model.UserProfile;
import java.util.Map;

public class AnswerEngine {

    private static UserProfile profile;

    public static void setProfile(UserProfile p) {
        profile = p;
        System.out.println("[AnswerEngine] Profile loaded for: " + p.getFullName());
    }

    public static String getAnswer(String question) {
        if (profile == null) { System.out.println("[AnswerEngine] No profile — returning 0"); return "0"; }

        String q = question.toLowerCase().trim();
        System.out.println("[AnswerEngine] Q: [" + q + "]");

        // ── PRIORITY 1: Skill-specific experience questions ───────────────────
        if (q.contains("how many years") || q.contains("years of experience") ||
                q.contains("experience do you have in") || q.contains("experience in")) {
            String exp = matchSkillExperience(q);
            return log(exp != null ? exp : "0");
        }

        // ── PRIORITY 2: Fast-path rules ───────────────────────────────────────
        if (q.contains("career break") || q.contains("career status") || q.contains("career gap"))
            return log(bool(!profile.isCareerBreak()));
        if (q.contains("current ctc") || q.contains("current salary"))
            return log(ctcStr(profile.getCurrentCtc()));
        if (q.contains("expected ctc") || q.contains("expected salary"))
            return log(ctcStr(profile.getExpectedCtc()));
        if (q.contains("currency of your salary") || q.contains("what is the currency"))
            return log("INR");
        if (q.contains("ctc") || q.contains("salary"))
            return log(ctcStr(profile.getExpectedCtc()));
        if (q.contains("notice"))
            return log(profile.getNotice());
        if (q.contains("experience"))
            return log("0");
        if (q.contains("dob") || q.contains("date of birth") || q.contains("birth date"))
            return log(profile.getDob());
        if (q.contains("gender"))
            return log(profile.getGender());
        if (q.contains("relocat"))
            return log(bool(profile.isRelocate()));
        if (q.contains("bond"))
            return log("No");
        if (q.contains("shift") || q.contains("rotational"))
            return log(bool(profile.isShift()));
        if (q.contains("work from office") || q.contains("working from office") || q.contains("work onsite"))
            return log(bool(profile.isWfo()));
        if (q.contains("differently abled") || q.contains("disability"))
            return log("No");
        if (q.contains("passout") || q.contains("passing year") || q.contains("graduation year")
                || q.contains("complete your b.tech") || q.contains("which year"))
            return log(nvl(profile.getPassoutYear(), "2024"));
        if (q.contains("reside") || q.contains("where do you") || q.contains("current city"))
            return log(nvl(profile.getCity(), "Chennai"));
        if (q.contains("preferred location") || q.contains("preferred locations"))
            return log(nvl(profile.getPreferredLocation(), "Chennai"));
        if (q.contains("location") || q.contains("city"))
            return log(nvl(profile.getCity(), "Chennai"));
        if (q.contains("keyskill") || q.contains("key skill"))
            return log("Java, Spring Boot, Selenium");
        if (q.contains("phone") || q.contains("mobile"))
            return log(profile.getPhone());
        if (q.contains("email") || q.contains("e-mail"))
            return log(profile.getEmail());
        if (q.contains("current company") || q.contains("current organization") || q.contains("company name"))
            return log(nvl(profile.getCompany(), "Fresher"));
        if (q.contains("designation at") || q.contains("your designation") || q.contains("designation"))
            return log(nvl(profile.getDesignation(), "Fresher"));
        if (q.contains("functional area"))
            return log("IT Software");
        if (q.contains("currently working") || q.contains("currently employed"))
            return log(bool(profile.isEmployed()));
        if (q.contains("undergraduate course") || q.contains("name of your undergraduate"))
            return log(nvl(profile.getDegree(), "B.Tech"));
        if (q.contains("specialization") || q.contains("stream"))
            return log(nvl(profile.getSpecialization(), "Computer Science"));
        if (q.contains("university or college") || q.contains("college name") || q.contains("which university"))
            return log(nvl(profile.getCollege(), "Anna University"));
        if (q.contains("cgpa"))
            return log(nvl(profile.getCgpa(), "8.0"));
        if (q.contains("percentage"))
            return log(nvl(profile.getPercentage(), "80"));
        if (q.contains("full name"))
            return log(profile.getFullName());
        if (q.contains("name") && !q.contains("company") && !q.contains("college")
                && !q.contains("university") && !q.contains("organization"))
            return log(profile.getFullName());
        if (q.contains("nationality"))
            return log("Indian");
        if (q.contains("resume") || q.contains("upload"))
            return log("skip");
        if (q.contains("interview") || q.contains("comfortable") || q.contains("face to face") || q.contains("f2f"))
            return log("Yes");
        if (q.contains("join in") || q.contains("joining"))
            return log("Yes");
        if (q.contains("visa") || q.contains("sponsorship"))
            return log("No");
        if (q.contains("legally authorized") || q.contains("work authorization"))
            return log("Yes");
        if (q.contains("government"))
            return log("No");
        if (q.contains("pan card") || q.contains("aadhaar") || q.contains("passport")
                || q.contains("driving license") || q.contains("voter id"))
            return log("Yes");
        if (q.contains("background verification") || q.contains("agree to") || q.contains("do you agree"))
            return log("Yes");
        if (q.contains("terminated") || q.contains("criminal") || q.contains("blacklisted"))
            return log("No");

        System.out.println("[AnswerEngine] No match — default: Yes");
        return "Yes";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Skill experience — extracts skill name from question, looks up in profile
    //
    // Also handles composite/alias skills:
    //   fullstack / full stack / full-stack → max(frontend + backend skills)
    //   mern / mean / lamp → component skills
    //   j2ee / java ee → java
    // ─────────────────────────────────────────────────────────────────────────
    private static String matchSkillExperience(String q) {
        Map<String, Double> skillMap = profile.getSkillExperience();

        // Extract skill name from "experience in <skill>"
        String skillPart = null;
        if (q.contains(" in ")) {
            int idx = q.lastIndexOf(" in ");
            skillPart = q.substring(idx + 4).trim().replaceAll("[?!.,;:]+$", "").trim();
            System.out.println("[AnswerEngine] Skill candidate: [" + skillPart + "]");
        }
        if (skillPart == null || skillPart.isBlank()) {
            return skillMap != null ? "0" : null;
        }

        // ── Alias / composite skill resolution ───────────────────────────────
        String resolved = resolveAlias(skillPart, skillMap);
        if (resolved != null) return resolved;

        // ── Direct map lookup — longest key match wins ────────────────────────
        if (skillMap != null) {
            String bestKey = null; int bestLen = 0;
            for (String key : skillMap.keySet()) {
                if (skillPart.contains(key) || key.contains(skillPart)) {
                    if (key.length() > bestLen) { bestLen = key.length(); bestKey = key; }
                }
            }
            if (bestKey != null) {
                double y = skillMap.getOrDefault(bestKey, 0.0);
                String r = fmt(y);
                System.out.println("[AnswerEngine] [" + bestKey + "] = " + r + " yrs");
                return r;
            }
        }

        System.out.println("[AnswerEngine] Skill [" + skillPart + "] not in map — returning 0");
        return "0";
    }

    /**
     * Handles skills that are composites, aliases, or stacks not explicitly in the map.
     * Returns the answer string, or null if no alias matched.
     */
    private static String resolveAlias(String skill, Map<String, Double> m) {
        if (m == null) return null;
        String s = skill.toLowerCase().trim();

        // Fullstack / full-stack / full stack → max of frontend + backend experience
        if (s.contains("fullstack") || s.contains("full stack") || s.contains("full-stack")) {
            double max = maxOf(m, "java","spring boot","spring","react","angular",
                    "node","javascript","python","django","flask",
                    ".net","php","ruby");
            System.out.println("[AnswerEngine] fullstack alias → " + fmt(max));
            return fmt(max);
        }

        // MERN stack
        if (s.contains("mern")) {
            double avg = avgOf(m, "mongodb","express","react","node");
            return fmt(avg);
        }

        // MEAN stack
        if (s.contains("mean")) {
            double avg = avgOf(m, "mongodb","express","angular","node");
            return fmt(avg);
        }

        // J2EE / Java EE → java experience
        if (s.contains("j2ee") || s.contains("java ee") || s.contains("javaee")) {
            return fmt(get(m, "java"));
        }

        // Core Java → java
        if (s.equals("core java") || s.equals("core-java")) {
            return fmt(get(m, "java"));
        }

        // Backend → max of backend skills
        if (s.equals("backend") || s.equals("back end") || s.equals("back-end")) {
            return fmt(maxOf(m, "java","spring boot","spring","python","django",
                    "flask","node","php","ruby",".net"));
        }

        // Frontend → max of frontend skills
        if (s.equals("frontend") || s.equals("front end") || s.equals("front-end")) {
            return fmt(maxOf(m, "react","angular","javascript","typescript",
                    "html","css","vue","nextjs"));
        }

        // OOP / Object Oriented → java
        if (s.contains("object oriented") || s.contains("oop")) {
            return fmt(get(m, "java"));
        }

        // Design patterns → java (closest proxy)
        if (s.contains("design pattern")) {
            return fmt(get(m, "java"));
        }

        // DevOps → max of devops tools
        if (s.contains("devops")) {
            return fmt(maxOf(m, "docker","kubernetes","jenkins","aws","azure","gcp"));
        }

        // Cloud → max of cloud
        if (s.equals("cloud") || s.contains("cloud computing")) {
            return fmt(maxOf(m, "aws","azure","gcp","docker","kubernetes"));
        }

        // Testing / QA / automation
        if (s.contains("testing") || s.contains("automation testing") || s.contains("qa")) {
            return fmt(maxOf(m, "selenium","junit","mockito","testng"));
        }

        // Web services / API
        if (s.contains("web service") || s.contains("web services") || s.contains("api development")) {
            return fmt(maxOf(m, "rest api","restful","soap","graphql","spring boot"));
        }

        // Data structures / algorithms → java
        if (s.contains("data structure") || s.contains("algorithm")) {
            return fmt(get(m, "java"));
        }

        // No alias matched
        return null;
    }

    private static double get(Map<String, Double> m, String key) {
        return m.getOrDefault(key, 0.0);
    }

    private static double maxOf(Map<String, Double> m, String... keys) {
        double max = 0;
        for (String k : keys) max = Math.max(max, m.getOrDefault(k, 0.0));
        return max;
    }

    private static double avgOf(Map<String, Double> m, String... keys) {
        double sum = 0; int cnt = 0;
        for (String k : keys) { sum += m.getOrDefault(k, 0.0); cnt++; }
        return cnt > 0 ? Math.round(sum / cnt * 10.0) / 10.0 : 0;
    }

    private static String fmt(double y) {
        return y == Math.floor(y) ? String.valueOf((int) y) : String.valueOf(y);
    }

    private static String bool(boolean b)        { return b ? "Yes" : "No"; }
    private static String nvl(String v, String d){ return (v != null && !v.isBlank()) ? v : d; }
    private static String ctcStr(double c)       { return c == Math.floor(c) ? String.valueOf((int)c) : String.valueOf(c); }
    private static String log(String a)          { System.out.println("[AnswerEngine] Answer: [" + a + "]"); return a; }
}