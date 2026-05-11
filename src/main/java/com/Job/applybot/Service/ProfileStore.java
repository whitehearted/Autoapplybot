package com.Job.applybot.Service;

import com.Job.applybot.model.UserProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ProfileStore — saves every UserProfile submitted to the bot into
 * ~/applybot-reports/profiles.xlsx so the user can reload it next time
 * without re-entering all their details.
 *
 * API:
 *   ProfileStore.save(profile)          — append / overwrite profile row by email
 *   ProfileStore.loadAll()              — return all saved profiles as List<UserProfile>
 *   ProfileStore.loadByEmail(email)     — return one saved profile or null
 *
 * The profiles.xlsx has one row per Naukri account (email = unique key).
 * If the same email is submitted again, the row is UPDATED not duplicated.
 */
public class ProfileStore {

    private static final String FILE_NAME = "profiles.xlsx";
    private static final String SHEET     = "Profiles";
    private static final String DIR       = System.getProperty("user.home")
            + File.separator + "applybot-reports";

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Column order in the sheet
    private static final String[] COLS = {
            "Email (key)", "Full Name", "Password", "Phone", "DOB",
            "Gender", "City", "Preferred Location", "Current CTC", "Expected CTC",
            "Notice Period", "Degree", "Specialization", "College", "Passout Year",
            "CGPA", "Percentage", "Company", "Designation",
            "Relocate", "Shift", "WFO", "Career Break", "Employed",
            "Skill Experience (JSON)", "Last Updated"
    };

    private static final ObjectMapper JSON = new ObjectMapper();

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Save a profile — if a row with this email already exists it is
     * overwritten; otherwise a new row is appended.
     */
    public static void save(UserProfile p) {
        try {
            Files.createDirectories(Paths.get(DIR));
            String path = DIR + File.separator + FILE_NAME;
            File file   = new File(path);

            XSSFWorkbook wb;
            XSSFSheet    sheet;

            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) { wb = new XSSFWorkbook(fis); }
                sheet = wb.getSheet(SHEET);
                if (sheet == null) { sheet = wb.createSheet(SHEET); wb.setSheetOrder(SHEET, 0); writeHeader(wb, sheet); }
            } else {
                wb    = new XSSFWorkbook();
                sheet = wb.createSheet(SHEET);
                writeHeader(wb, sheet);
            }

            // Find existing row for this email (update) or use next row (insert)
            int targetRow = findRowByEmail(sheet, p.getNaukriEmail());
            if (targetRow == -1) targetRow = sheet.getLastRowNum() + 1;

            writeProfileRow(wb, sheet, targetRow, p);

            try (FileOutputStream fos = new FileOutputStream(path)) { wb.write(fos); }
            wb.close();
            System.out.println("[ProfileStore] Saved profile: " + p.getNaukriEmail());
        } catch (Exception e) {
            System.out.println("[ProfileStore] Save error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    /** Load all saved profiles. */
    public static List<UserProfile> loadAll() {
        List<UserProfile> list = new ArrayList<>();
        try {
            String path = DIR + File.separator + FILE_NAME;
            if (!new File(path).exists()) return list;

            XSSFWorkbook wb;
            try (FileInputStream fis = new FileInputStream(path)) { wb = new XSSFWorkbook(fis); }
            XSSFSheet sheet = wb.getSheet(SHEET);
            if (sheet == null) { wb.close(); return list; }

            for (int ri = 2; ri <= sheet.getLastRowNum(); ri++) {
                Row row = sheet.getRow(ri);
                if (row == null) continue;
                UserProfile p = rowToProfile(row);
                if (p != null) list.add(p);
            }
            wb.close();
        } catch (Exception e) {
            System.out.println("[ProfileStore] Load error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    /** Load one profile by email, returns null if not found. */
    public static UserProfile loadByEmail(String email) {
        return loadAll().stream()
                .filter(p -> email.equalsIgnoreCase(p.getNaukriEmail()))
                .findFirst().orElse(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────
    private static void writeHeader(XSSFWorkbook wb, XSSFSheet sheet) {
        // Title row
        Row title = sheet.createRow(0);
        title.setHeightInPoints(26);
        Cell tc = title.createCell(0);
        tc.setCellValue("  Applybot — Saved Profiles  (one row per Naukri account)");
        tc.setCellStyle(titleStyle(wb));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, COLS.length - 1));

        // Header row
        Row hr = sheet.createRow(1);
        hr.setHeightInPoints(18);
        for (int i = 0; i < COLS.length; i++) {
            Cell c = hr.createCell(i);
            c.setCellValue(COLS[i]);
            c.setCellStyle(headerStyle(wb));
            sheet.setColumnWidth(i, colWidth(i) * 256);
        }
        sheet.createFreezePane(0, 2);
    }

    private static void writeProfileRow(XSSFWorkbook wb, XSSFSheet sheet,
                                        int rowIndex, UserProfile p) {
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(16);
        CellStyle st = dataStyle(wb);

        String skillJson = "{}";
        try { skillJson = JSON.writeValueAsString(p.getSkillExperience() != null
                ? p.getSkillExperience() : Collections.emptyMap()); }
        catch (Exception ignored) {}

        Object[] vals = {
                p.getNaukriEmail(),
                p.getFullName(),
                p.getNaukriPassword(),
                p.getPhone(),
                p.getDob(),
                p.getGender(),
                p.getCity(),
                p.getPreferredLocation(),
                p.getCurrentCtc(),
                p.getExpectedCtc(),
                p.getNotice(),
                p.getDegree(),
                p.getSpecialization(),
                p.getCollege(),
                p.getPassoutYear(),
                p.getCgpa(),
                p.getPercentage(),
                p.getCompany(),
                p.getDesignation(),
                p.isRelocate()     ? "Yes" : "No",
                p.isShift()        ? "Yes" : "No",
                p.isWfo()          ? "Yes" : "No",
                p.isCareerBreak()  ? "Yes" : "No",
                p.isEmployed()     ? "Yes" : "No",
                skillJson,
                LocalDateTime.now().format(FMT)
        };

        for (int i = 0; i < vals.length; i++) {
            Cell c = row.createCell(i);
            if (vals[i] instanceof Double d) c.setCellValue(d);
            else c.setCellValue(vals[i] != null ? vals[i].toString() : "");
            c.setCellStyle(st);
        }
    }

    private static UserProfile rowToProfile(Row row) {
        try {
            UserProfile p = new UserProfile();
            p.setNaukriEmail(str(row, 0));
            p.setFullName(str(row, 1));
            p.setNaukriPassword(str(row, 2));
            p.setPhone(str(row, 3));
            p.setDob(str(row, 4));
            p.setGender(str(row, 5));
            p.setCity(str(row, 6));
            p.setPreferredLocation(str(row, 7));
            p.setCurrentCtc(dbl(row, 8));
            p.setExpectedCtc(dbl(row, 9));
            p.setNotice(str(row, 10));
            p.setDegree(str(row, 11));
            p.setSpecialization(str(row, 12));
            p.setCollege(str(row, 13));
            p.setPassoutYear(str(row, 14));
            p.setCgpa(str(row, 15));
            p.setPercentage(str(row, 16));
            p.setCompany(str(row, 17));
            p.setDesignation(str(row, 18));
            p.setRelocate("Yes".equalsIgnoreCase(str(row, 19)));
            p.setShift("Yes".equalsIgnoreCase(str(row, 20)));
            p.setWfo("Yes".equalsIgnoreCase(str(row, 21)));
            p.setCareerBreak("Yes".equalsIgnoreCase(str(row, 22)));
            p.setEmployed("Yes".equalsIgnoreCase(str(row, 23)));
            String skillJson = str(row, 24);
            if (!skillJson.isBlank() && !skillJson.equals("{}")) {
                Map<String, Double> skills = JSON.readValue(skillJson,
                        new TypeReference<Map<String, Double>>() {});
                p.setSkillExperience(skills);
            }
            return p;
        } catch (Exception e) {
            System.out.println("[ProfileStore] Row parse error: " + e.getMessage());
            return null;
        }
    }

    private static int findRowByEmail(XSSFSheet sheet, String email) {
        if (email == null) return -1;
        for (int ri = 2; ri <= sheet.getLastRowNum(); ri++) {
            Row row = sheet.getRow(ri);
            if (row == null) continue;
            Cell c = row.getCell(0);
            if (c != null && email.equalsIgnoreCase(c.getStringCellValue().trim()))
                return ri;
        }
        return -1;
    }

    private static String str(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null) return "";
        return switch (c.getCellType()) {
            case STRING  -> c.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) c.getNumericCellValue());
            case BOOLEAN -> c.getBooleanCellValue() ? "true" : "false";
            default      -> "";
        };
    }

    private static double dbl(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null) return 0;
        try { return c.getNumericCellValue(); } catch (Exception e) { return 0; }
    }

    private static int colWidth(int i) {
        return switch (i) {
            case 0  -> 30;  // email
            case 1  -> 20;  // name
            case 2  -> 18;  // password
            case 24 -> 60;  // skill JSON
            default -> 16;
        };
    }

    // ── Styles ────────────────────────────────────────────────────────────────
    private static XSSFCellStyle titleStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(rgb(22, 33, 62)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT); s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 12);
        f.setColor(rgb(255,255,255)); f.setFontName("Arial"); s.setFont(f); bdr(s); return s;
    }

    private static XSSFCellStyle headerStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(rgb(26,26,46)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
        f.setColor(rgb(255,255,255)); f.setFontName("Arial"); s.setFont(f); bdr(s); return s;
    }

    private static XSSFCellStyle dataStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setFontName("Arial"); f.setFontHeightInPoints((short) 10);
        s.setFont(f); bdr(s); return s;
    }

    private static void bdr(XSSFCellStyle s) {
        XSSFColor c = rgb(200,200,200);
        s.setBorderTop(BorderStyle.THIN); s.setTopBorderColor(c);
        s.setBorderBottom(BorderStyle.THIN); s.setBottomBorderColor(c);
        s.setBorderLeft(BorderStyle.THIN); s.setLeftBorderColor(c);
        s.setBorderRight(BorderStyle.THIN); s.setRightBorderColor(c);
    }

    private static XSSFColor rgb(int r, int g, int b) {
        return new XSSFColor(new byte[]{ (byte) r, (byte) g, (byte) b }, null);
    }
}