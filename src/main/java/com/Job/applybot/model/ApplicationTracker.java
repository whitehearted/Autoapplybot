//package com.Job.applybot.model;
//
//import com.Job.applybot.Service.ApplicationResult;
//import org.apache.poi.common.usermodel.HyperlinkType;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.*;
//
//import java.io.*;
//import java.nio.file.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
///**
// * ApplicationTracker — writes every job result to a SINGLE shared Excel file
// * immediately after each application attempt (no data is lost on crash).
// *
// * Behaviour:
// *  - File: <outputDir>/applybot_results.xlsx  (one file for everyone, forever)
// *  - If the file does NOT exist → creates it with header row
// *  - If the file DOES exist     → opens it and appends a new row
// *  - All users / all runs share the same sheet, sorted by timestamp
// *  - The Summary sheet auto-recounts from the data on each save
// *
// * Usage in Bot.java:
// *   tracker.add(result);   ← call this — it saves to disk immediately
// *   tracker.finish();      ← call at end of run to refresh the Summary sheet
// */
//public class ApplicationTracker {
//
//    // ── Single shared file for all users and all runs ─────────────────────────
//    private static final String FILE_NAME  = "applybot_results.xlsx";
//    private static final String SHEET_DATA = "Applications";
//    private static final String SHEET_SUM  = "Summary";
//
//    // Column layout — DO NOT change order without updating COL_* constants
//    private static final String[] HEADERS = {
//            "#", "Timestamp", "Username", "Job Title", "Company",
//            "Status", "Job URL", "Final URL (Link Used)", "Notes", "Run ID"
//    };
//    private static final int[] COL_WIDTHS = {
//            5,   22,   20,   38,   22,
//            16,   52,   52,   32,   22
//    };
//
//    // Column indexes (0-based)
//    private static final int COL_SEQ     = 0;
//    private static final int COL_TS      = 1;
//    private static final int COL_USER    = 2;
//    private static final int COL_TITLE   = 3;
//    private static final int COL_COMPANY = 4;
//    private static final int COL_STATUS  = 5;
//    private static final int COL_JOBURL  = 6;
//    private static final int COL_FINALURL= 7;
//    private static final int COL_NOTES   = 8;
//    private static final int COL_RUN     = 9;
//
//    // ── Colours ───────────────────────────────────────────────────────────────
//    private static final String C_HEADER  = "1A1A2E";
//    private static final String C_TITLE   = "16213E";
//    private static final String C_ALT     = "F7F7F7";
//    private static final String C_WHITE   = "FFFFFF";
//    private static final String C_LINK    = "0563C1";
//    private static final String C_BORDER  = "CCCCCC";
//    private static final String C_SUM_BG  = "0F3460";
//
//    private static final String C_SUCCESS  = "27AE60";
//    private static final String C_DIRECT   = "2980B9";
//    private static final String C_FAILED   = "C0392B";
//    private static final String C_SKIPPED  = "95A5A6";
//    private static final String C_REDIR    = "D68910";
//
//    // ─────────────────────────────────────────────────────────────────────────
//    private final String outputDir;
//    private final String runId;          // identifies this bot run in the Run ID column
//
//    private static final DateTimeFormatter DT_FMT =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//    private static final DateTimeFormatter RUN_FMT =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//    public ApplicationTracker(String username) {
//        this.outputDir = System.getProperty("user.home") + File.separator + "applybot-reports";
//        this.runId     = username + " @ " + LocalDateTime.now().format(RUN_FMT);
//    }
//
//    public ApplicationTracker(String username, String outputDir) {
//        this.outputDir = outputDir;
//        this.runId     = username + " @ " + LocalDateTime.now().format(RUN_FMT);
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    /**
//     * Appends one result row to the Excel file and saves immediately.
//     * Called after EVERY job attempt so data is never lost on crash.
//     */
//    public void add(ApplicationResult result) {
//        try {
//            Files.createDirectories(Paths.get(outputDir));
//            String filePath = outputDir + File.separator + FILE_NAME;
//            File   file     = new File(filePath);
//
//            XSSFWorkbook wb;
//            XSSFSheet    dataSheet;
//
//            if (file.exists()) {
//                // ── Open existing file and append ─────────────────────────────
//                try (FileInputStream fis = new FileInputStream(file)) {
//                    wb = new XSSFWorkbook(fis);
//                }
//                dataSheet = wb.getSheet(SHEET_DATA);
//                if (dataSheet == null) {
//                    // Sheet missing (corrupted?) — recreate it
//                    dataSheet = wb.createSheet(SHEET_DATA);
//                    wb.setSheetOrder(SHEET_DATA, 0);
//                    writeHeaderRow(wb, dataSheet);
//                }
//            } else {
//                // ── Create brand-new file ─────────────────────────────────────
//                wb = new XSSFWorkbook();
//                dataSheet = wb.createSheet(SHEET_DATA);
//                wb.createSheet(SHEET_SUM);   // placeholder, rebuilt on finish()
//                writeHeaderRow(wb, dataSheet);
//            }
//
//            // ── Append the new data row ───────────────────────────────────────
//            int nextRow    = dataSheet.getLastRowNum() + 1;  // 0-based; row 0=title, 1=header, 2+=data
//            int dataRowNum = nextRow - 1;                    // sequential # (1, 2, 3…)
//            appendDataRow(wb, dataSheet, nextRow, dataRowNum, result);
//
//            // ── Save immediately ──────────────────────────────────────────────
//            try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                wb.write(fos);
//            }
//            wb.close();
//
//            System.out.println("[Tracker] Saved row #" + dataRowNum
//                    + " | " + result.getStatusLabel()
//                    + " | " + truncate(result.getJobTitle(), 50));
//
//        } catch (Exception e) {
//            System.out.println("[Tracker] ERROR saving result: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    /**
//     * Rebuilds the Summary sheet with up-to-date counts.
//     * Call once at the end of a bot run.
//     */
//    public void finish() {
//        try {
//            String filePath = outputDir + File.separator + FILE_NAME;
//            File   file     = new File(filePath);
//            if (!file.exists()) return;
//
//            XSSFWorkbook wb;
//            try (FileInputStream fis = new FileInputStream(file)) {
//                wb = new XSSFWorkbook(fis);
//            }
//
//            // Remove old summary sheet and rebuild
//            int idx = wb.getSheetIndex(SHEET_SUM);
//            if (idx >= 0) wb.removeSheetAt(idx);
//            XSSFSheet sumSheet = wb.createSheet(SHEET_SUM);
//            wb.setSheetOrder(SHEET_SUM, 1);
//
//            buildSummarySheet(wb, sumSheet, wb.getSheet(SHEET_DATA));
//
//            try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                wb.write(fos);
//            }
//            wb.close();
//            System.out.println("[Tracker] Summary refreshed → " + filePath);
//
//        } catch (Exception e) {
//            System.out.println("[Tracker] ERROR refreshing summary: " + e.getMessage());
//        }
//    }
//
//    public String getFilePath() {
//        return outputDir + File.separator + FILE_NAME;
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Title row (row 0) + Header row (row 1)
//    // ─────────────────────────────────────────────────────────────────────────
//    private void writeHeaderRow(XSSFWorkbook wb, XSSFSheet sheet) {
//        // Row 0 — title banner
//        Row titleRow = sheet.createRow(0);
//        titleRow.setHeightInPoints(28);
//        Cell tc = titleRow.createCell(0);
//        tc.setCellValue("  Applybot — All Applications  (all users · all runs)");
//        tc.setCellStyle(titleStyle(wb));
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));
//
//        // Row 1 — column headers
//        Row hRow = hRow(wb, sheet);
//        for (int i = 0; i < HEADERS.length; i++) {
//            Cell c = hRow.createCell(i);
//            c.setCellValue(HEADERS[i]);
//            c.setCellStyle(headerStyle(wb));
//            sheet.setColumnWidth(i, COL_WIDTHS[i] * 256);
//        }
//
//        // Freeze panes so title + header stay visible while scrolling
//        sheet.createFreezePane(0, 2);
//    }
//
//    private Row hRow(XSSFWorkbook wb, XSSFSheet sheet) {
//        Row r = sheet.createRow(1);
//        r.setHeightInPoints(20);
//        return r;
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Append one data row
//    // ─────────────────────────────────────────────────────────────────────────
//    private void appendDataRow(XSSFWorkbook wb, XSSFSheet sheet,
//                               int rowIndex, int seqNum, ApplicationResult r) {
//        Row row = sheet.createRow(rowIndex);
//        row.setHeightInPoints(16);
//
//        boolean alt = (rowIndex % 2 == 0);   // alternating row background
//
//        CellStyle base = alt ? altRowStyle(wb) : baseRowStyle(wb);
//
//        // # (sequence)
//        setCell(row, COL_SEQ,     String.valueOf(seqNum), base);
//        // Timestamp
//        setCell(row, COL_TS,      r.getTimestamp(),       base);
//        // Username
//        setCell(row, COL_USER,    r.getUsername(),        base);
//        // Job title
//        setCell(row, COL_TITLE,   r.getJobTitle(),        base);
//        // Company
//        setCell(row, COL_COMPANY, r.getCompany(),         base);
//        // Status — coloured badge
//        Cell statusCell = row.createCell(COL_STATUS);
//        statusCell.setCellValue(r.getStatusLabel());
//        statusCell.setCellStyle(statusStyle(wb, r.getStatus()));
//        // Job URL
//        setUrlCell(wb, row, COL_JOBURL,   r.getJobUrl(),   base);
//        // Final URL
//        setUrlCell(wb, row, COL_FINALURL, r.getFinalUrl(), base);
//        // Notes
//        setCell(row, COL_NOTES, r.getNotes(), base);
//        // Run ID
//        setCell(row, COL_RUN,   runId,        base);
//
//        // Re-apply auto-filter to cover all data rows including the new one
//        sheet.setAutoFilter(new CellRangeAddress(1, rowIndex, 0, HEADERS.length - 1));
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Summary sheet — rebuilt from scratch on finish()
//    // ─────────────────────────────────────────────────────────────────────────
//    private void buildSummarySheet(XSSFWorkbook wb, XSSFSheet sum, XSSFSheet data) {
//        // Count statuses across ALL rows (skip title row 0 and header row 1)
//        int total = 0, success = 0, direct = 0, failed = 0, skipped = 0, redirected = 0;
//        int lastRow = data.getLastRowNum();
//        for (int ri = 2; ri <= lastRow; ri++) {
//            Row row = data.getRow(ri);
//            if (row == null) continue;
//            Cell sc = row.getCell(COL_STATUS);
//            if (sc == null) continue;
//            String st = sc.getStringCellValue().trim();
//            total++;
//            switch (st) {
//                case "SUCCESS"      -> success++;
//                case "DIRECT_APPLY" -> direct++;
//                case "FAILED"       -> failed++;
//                case "SKIPPED"      -> skipped++;
//                case "REDIRECTED"   -> redirected++;
//            }
//        }
//        int applied = success + direct;
//        double rate = total > 0 ? (double) applied / total * 100.0 : 0.0;
//
//        // Title
//        sum.setColumnWidth(0, 38 * 256);
//        sum.setColumnWidth(1, 16 * 256);
//        sum.setColumnWidth(2, 14 * 256);
//
//        Row t = sum.createRow(0); t.setHeightInPoints(28);
//        Cell tc = t.createCell(0);
//        tc.setCellValue("  Applybot — Application Summary  (all users · all runs)");
//        tc.setCellStyle(titleStyle(wb));
//        sum.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
//
//        // Stats table
//        Object[][] rows = {
//                { "Total applications scanned",          total      },
//                { "Applied — chatbot / popup",            success    },
//                { "Applied — direct (no questions)",      direct     },
//                { "Total applied",                        applied    },
//                { "Failed",                               failed     },
//                { "Skipped (already applied / not found)",skipped    },
//                { "Redirected to company site",           redirected },
//                { "Overall success rate",                 String.format("%.1f%%", rate) },
//        };
//
//        CellStyle labelSt = sumLabelStyle(wb);
//        CellStyle valueSt = sumValueStyle(wb);
//
//        for (int i = 0; i < rows.length; i++) {
//            Row row = sum.createRow(i + 2);
//            row.setHeightInPoints(20);
//            Cell lc = row.createCell(0);
//            lc.setCellValue(rows[i][0].toString());
//            lc.setCellStyle(labelSt);
//
//            Cell vc = row.createCell(1);
//            if (rows[i][1] instanceof Integer n) vc.setCellValue(n);
//            else vc.setCellValue(rows[i][1].toString());
//            vc.setCellStyle(valueSt);
//        }
//
//        // Status legend
//        Row sep = sum.createRow(11); sep.setHeightInPoints(10);
//        Row bh  = sum.createRow(12); bh.setHeightInPoints(20);
//        Cell bhc = bh.createCell(0);
//        bhc.setCellValue("Status legend");
//        bhc.setCellStyle(headerStyle(wb));
//        sum.addMergedRegion(new CellRangeAddress(12, 12, 0, 2));
//
//        Object[][] legend = {
//                { "SUCCESS",      "ChatBot or Popup flow completed",    C_SUCCESS  },
//                { "DIRECT_APPLY", "Applied directly — no questions",    C_DIRECT   },
//                { "FAILED",       "Apply attempted but error occurred", C_FAILED   },
//                { "SKIPPED",      "Already applied / button not found", C_SKIPPED  },
//                { "REDIRECTED",   "Naukri sent to company own site",    C_REDIR    },
//        };
//        for (int i = 0; i < legend.length; i++) {
//            Row lr = sum.createRow(13 + i); lr.setHeightInPoints(18);
//            Cell badge = lr.createCell(0);
//            badge.setCellValue(legend[i][0].toString());
//            badge.setCellStyle(statusStyleByHex(wb, legend[i][2].toString()));
//            Cell desc = lr.createCell(1);
//            desc.setCellValue(legend[i][1].toString());
//            desc.setCellStyle(baseRowStyle(wb));
//            sum.addMergedRegion(new CellRangeAddress(13 + i, 13 + i, 1, 2));
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Cell helpers
//    // ─────────────────────────────────────────────────────────────────────────
//    private void setCell(Row row, int col, String value, CellStyle style) {
//        Cell c = row.createCell(col);
//        c.setCellValue(value != null ? value : "");
//        c.setCellStyle(style);
//    }
//
//    private void setUrlCell(XSSFWorkbook wb, Row row, int col,
//                            String url, CellStyle fallback) {
//        Cell c = row.createCell(col);
//        if (url != null && !url.isBlank() && url.startsWith("http")) {
//            c.setCellValue(url);
//            XSSFHyperlink link = wb.getCreationHelper().createHyperlink(HyperlinkType.URL);
//            link.setAddress(url);
//            c.setHyperlink(link);
//            c.setCellStyle(hyperlinkStyle(wb));
//        } else {
//            c.setCellValue(url != null ? url : "—");
//            c.setCellStyle(fallback);
//        }
//    }
//
//    private String truncate(String s, int max) {
//        if (s == null) return "";
//        return s.length() > max ? s.substring(0, max) + "…" : s;
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // Style factories — recreated fresh each call (XSSFWorkbook caches them)
//    // ─────────────────────────────────────────────────────────────────────────
//    private XSSFCellStyle titleStyle(XSSFWorkbook wb) {
//        XSSFCellStyle s = wb.createCellStyle();
//        s.setFillForegroundColor(hex(C_TITLE));
//        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        s.setAlignment(HorizontalAlignment.LEFT);
//        s.setVerticalAlignment(VerticalAlignment.CENTER);
//        XSSFFont f = wb.createFont();
//        f.setBold(true); f.setFontHeightInPoints((short) 13);
//        f.setColor(hex(C_WHITE)); f.setFontName("Arial");
//        s.setFont(f); borders(s); return s;
//    }
//
//    private XSSFCellStyle headerStyle(XSSFWorkbook wb) {
//        XSSFCellStyle s = wb.createCellStyle();
//        s.setFillForegroundColor(hex(C_HEADER));
//        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        s.setAlignment(HorizontalAlignment.CENTER);
//        s.setVerticalAlignment(VerticalAlignment.CENTER);
//        XSSFFont f = wb.createFont();
//        f.setBold(true); f.setFontHeightInPoints((short) 10);
//        f.setColor(hex(C_WHITE)); f.setFontName("Arial");
//        s.setFont(f); borders(s); return s;
//    }
//
//    private XSSFCellStyle baseRowStyle(XSSFWorkbook wb) {
//        XSSFCellStyle s = wb.createCellStyle();
//        s.setVerticalAlignment(VerticalAlignment.CENTER);
//        XSSFFont f = wb.createFont(); f.setFontName("Arial"); f.setFontHeightInPoints((short) 10);
//        s.setFont(f); borders(s); return s;
//    }
//
//    private XSSFCellStyle altRowStyle(XSSFWorkbook wb) {
//        XSSFCellStyle s = wb.createCellStyle();
//        s.setFillForegroundColor(hex(C_ALT));
//        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        s.setVerticalAlignment(VerticalAlignment.CENTER);
//        XSSFFont f = wb.createFont(); f.setFontName("Arial"); f.setFontHeightInPoints((short) 10);
//        s.setFont(f); borders(s); return s;
//    }
//
//    private XSSFCellStyle hyperlinkStyle(XSSFWorkbook wb) {
//        XSSFCellStyle s = wb.createCellStyle();
//        s.setVerticalAlignment(VerticalAlignment.CENTER);
//        XSSFFont f = wb.createFont();
//        f.setFontName("Arial"); f.setFontHeightInPoints((short) 10);
//        f.setUnderline(FontUnderline.SINGLE);
//        f.setColor(hex(C_LINK));
//        s.setFont(f); borders(s); return s;
//    }
//
//    private XSSFCellStyle statusStyle(XSSFWorkbook wb, ApplicationResult.Status status) {
//        return statusStyleByHex(wb, colorOf(status));
//    }
//
//    private XSSFCellStyle statusStyleByHex(XSSFWorkbook wb, String hexColor) {
//        XSSFCellStyle s = wb.createCellStyle();
//        s.setFillForegroundColor(hex(hexColor));
//        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        s.setAlignment(HorizontalAlignment.CENTER);
//        s.setVerticalAlignment(VerticalAlignment.CENTER);
//        XSSFFont f = wb.createFont();
//        f.setBold(true); f.setFontHeightInPoints((short) 9);
//        f.setColor(hex(C_WHITE)); f.setFontName("Arial");
//        s.setFont(f); borders(s); return s;
//    }
//
//    private XSSFCellStyle sumLabelStyle(XSSFWorkbook wb) {
//        XSSFCellStyle s = wb.createCellStyle();
//        s.setFillForegroundColor(hex(C_SUM_BG));
//        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        s.setVerticalAlignment(VerticalAlignment.CENTER);
//        XSSFFont f = wb.createFont();
//        f.setFontName("Arial"); f.setFontHeightInPoints((short) 11);
//        f.setColor(hex(C_WHITE));
//        s.setFont(f); borders(s); return s;
//    }
//
//    private XSSFCellStyle sumValueStyle(XSSFWorkbook wb) {
//        XSSFCellStyle s = wb.createCellStyle();
//        s.setAlignment(HorizontalAlignment.CENTER);
//        s.setVerticalAlignment(VerticalAlignment.CENTER);
//        XSSFFont f = wb.createFont();
//        f.setBold(true); f.setFontName("Arial"); f.setFontHeightInPoints((short) 13);
//        s.setFont(f); borders(s); return s;
//    }
//
//    // ── Helpers ───────────────────────────────────────────────────────────────
//    private void borders(XSSFCellStyle s) {
//        XSSFColor bc = hex(C_BORDER);
//        s.setBorderTop(BorderStyle.THIN);    s.setTopBorderColor(bc);
//        s.setBorderBottom(BorderStyle.THIN); s.setBottomBorderColor(bc);
//        s.setBorderLeft(BorderStyle.THIN);   s.setLeftBorderColor(bc);
//        s.setBorderRight(BorderStyle.THIN);  s.setRightBorderColor(bc);
//    }
//
//    private XSSFColor hex(String rgb6) {
//        // accepts 6-char RGB "1A1A2E"
//        int r = Integer.parseInt(rgb6.substring(0, 2), 16);
//        int g = Integer.parseInt(rgb6.substring(2, 4), 16);
//        int b = Integer.parseInt(rgb6.substring(4, 6), 16);
//        return new XSSFColor(new byte[]{ (byte) r, (byte) g, (byte) b }, null);
//    }
//
//    private String colorOf(ApplicationResult.Status status) {
//        return switch (status) {
//            case SUCCESS      -> C_SUCCESS;
//            case DIRECT_APPLY -> C_DIRECT;
//            case FAILED       -> C_FAILED;
//            case SKIPPED      -> C_SKIPPED;
//            case REDIRECTED   -> C_REDIR;
//        };
//    }
//}


package com.Job.applybot.model;

import com.Job.applybot.Service.ApplicationResult;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ApplicationTracker — writes every job result to a SINGLE shared Excel file
 * immediately after each application attempt (no data is lost on crash).
 *
 * Behaviour:
 *  - File: <outputDir>/applybot_results.xlsx  (one file for everyone, forever)
 *  - If the file does NOT exist → creates it with header row
 *  - If the file DOES exist     → opens it and appends a new row
 *  - All users / all runs share the same sheet, sorted by timestamp
 *  - The Summary sheet auto-recounts from the data on each save
 *
 * Usage in Bot.java:
 *   tracker.add(result);   ← call this — it saves to disk immediately
 *   tracker.finish();      ← call at end of run to refresh the Summary sheet
 */
public class ApplicationTracker {

    // ── Single shared file for all users and all runs ─────────────────────────
    private static final String FILE_NAME  = "applybot_results.xlsx";
    private static final String SHEET_DATA = "Applications";
    private static final String SHEET_SUM  = "Summary";

    // Column layout — DO NOT change order without updating COL_* constants
    private static final String[] HEADERS = {
            "#", "Timestamp", "Username", "Job Title", "Company",
            "Status", "Job URL", "Final URL (Link Used)", "Notes", "Run ID"
    };
    private static final int[] COL_WIDTHS = {
            5,   22,   20,   38,   22,
            16,   52,   52,   32,   22
    };

    // Column indexes (0-based)
    private static final int COL_SEQ     = 0;
    private static final int COL_TS      = 1;
    private static final int COL_USER    = 2;
    private static final int COL_TITLE   = 3;
    private static final int COL_COMPANY = 4;
    private static final int COL_STATUS  = 5;
    private static final int COL_JOBURL  = 6;
    private static final int COL_FINALURL= 7;
    private static final int COL_NOTES   = 8;
    private static final int COL_RUN     = 9;

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final String C_HEADER  = "1A1A2E";
    private static final String C_TITLE   = "16213E";
    private static final String C_ALT     = "F7F7F7";
    private static final String C_WHITE   = "FFFFFF";
    private static final String C_LINK    = "0563C1";
    private static final String C_BORDER  = "CCCCCC";
    private static final String C_SUM_BG  = "0F3460";

    private static final String C_SUCCESS  = "27AE60";
    private static final String C_DIRECT   = "2980B9";
    private static final String C_FAILED   = "C0392B";
    private static final String C_SKIPPED  = "95A5A6";
    private static final String C_REDIR    = "D68910";

    // ─────────────────────────────────────────────────────────────────────────
    private final String outputDir;
    private final String runId;          // identifies this bot run in the Run ID column

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter RUN_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ApplicationTracker(String username) {
        this.outputDir = System.getProperty("user.home") + File.separator + "applybot-reports";
        this.runId     = username + " @ " + LocalDateTime.now().format(RUN_FMT);
    }

    public ApplicationTracker(String username, String outputDir) {
        this.outputDir = outputDir;
        this.runId     = username + " @ " + LocalDateTime.now().format(RUN_FMT);
    }

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Appends one result row to the Excel file and saves immediately.
     * Called after EVERY job attempt so data is never lost on crash.
     */
    public void add(ApplicationResult result) {
        try {
            Files.createDirectories(Paths.get(outputDir));
            String filePath = outputDir + File.separator + FILE_NAME;
            File   file     = new File(filePath);

            XSSFWorkbook wb;
            XSSFSheet    dataSheet;

            if (file.exists()) {
                // ── Open existing file and append ─────────────────────────────
                try (FileInputStream fis = new FileInputStream(file)) {
                    wb = new XSSFWorkbook(fis);
                }
                dataSheet = wb.getSheet(SHEET_DATA);
                if (dataSheet == null) {
                    // Sheet missing (corrupted?) — recreate it
                    dataSheet = wb.createSheet(SHEET_DATA);
                    wb.setSheetOrder(SHEET_DATA, 0);
                    writeHeaderRow(wb, dataSheet);
                }
            } else {
                // ── Create brand-new file ─────────────────────────────────────
                wb = new XSSFWorkbook();
                dataSheet = wb.createSheet(SHEET_DATA);
                wb.createSheet(SHEET_SUM);   // placeholder, rebuilt on finish()
                writeHeaderRow(wb, dataSheet);
            }

            // ── Append the new data row ───────────────────────────────────────
            int nextRow    = dataSheet.getLastRowNum() + 1;  // 0-based; row 0=title, 1=header, 2+=data
            int dataRowNum = nextRow - 1;                    // sequential # (1, 2, 3…)
            appendDataRow(wb, dataSheet, nextRow, dataRowNum, result);

            // ── Save immediately ──────────────────────────────────────────────
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
            wb.close();

            System.out.println("[Tracker] Saved row #" + dataRowNum
                    + " | " + result.getStatusLabel()
                    + " | " + truncate(result.getJobTitle(), 50));

        } catch (Exception e) {
            System.out.println("[Tracker] ERROR saving result: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Rebuilds the Summary sheet with up-to-date counts.
     * Call once at the end of a bot run.
     */
    public void finish() {
        try {
            String filePath = outputDir + File.separator + FILE_NAME;
            File   file     = new File(filePath);
            if (!file.exists()) return;

            XSSFWorkbook wb;
            try (FileInputStream fis = new FileInputStream(file)) {
                wb = new XSSFWorkbook(fis);
            }

            // Remove old summary sheet and rebuild
            int idx = wb.getSheetIndex(SHEET_SUM);
            if (idx >= 0) wb.removeSheetAt(idx);
            XSSFSheet sumSheet = wb.createSheet(SHEET_SUM);
            wb.setSheetOrder(SHEET_SUM, 1);

            buildSummarySheet(wb, sumSheet, wb.getSheet(SHEET_DATA));

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
            wb.close();
            System.out.println("[Tracker] Summary refreshed → " + filePath);

        } catch (Exception e) {
            System.out.println("[Tracker] ERROR refreshing summary: " + e.getMessage());
        }
    }

    public long countByStatus(String statusLabel) {
        long count = 0;
        try {
            java.io.File file = new java.io.File(outputDir + java.io.File.separator + FILE_NAME);
            if (!file.exists()) return 0;
            try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
                 XSSFWorkbook wb = new XSSFWorkbook(fis)) {
                XSSFSheet sheet = wb.getSheet(SHEET_DATA);
                if (sheet == null) return 0;
                for (int ri = 2; ri <= sheet.getLastRowNum(); ri++) {
                    Row row = sheet.getRow(ri);
                    if (row == null) continue;
                    Cell sc = row.getCell(COL_STATUS);
                    if (sc != null && statusLabel.equals(sc.getStringCellValue().trim())) count++;
                }
            }
        } catch (Exception ignored) {}
        return count;
    }

    public String getFilePath() {
        return outputDir + File.separator + FILE_NAME;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Title row (row 0) + Header row (row 1)
    // ─────────────────────────────────────────────────────────────────────────
    private void writeHeaderRow(XSSFWorkbook wb, XSSFSheet sheet) {
        // Row 0 — title banner
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(28);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("  Applybot — All Applications  (all users · all runs)");
        tc.setCellStyle(titleStyle(wb));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));

        // Row 1 — column headers
        Row hRow = hRow(wb, sheet);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell c = hRow.createCell(i);
            c.setCellValue(HEADERS[i]);
            c.setCellStyle(headerStyle(wb));
            sheet.setColumnWidth(i, COL_WIDTHS[i] * 256);
        }

        // Freeze panes so title + header stay visible while scrolling
        sheet.createFreezePane(0, 2);
    }

    private Row hRow(XSSFWorkbook wb, XSSFSheet sheet) {
        Row r = sheet.createRow(1);
        r.setHeightInPoints(20);
        return r;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Append one data row
    // ─────────────────────────────────────────────────────────────────────────
    private void appendDataRow(XSSFWorkbook wb, XSSFSheet sheet,
                               int rowIndex, int seqNum, ApplicationResult r) {
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(16);

        boolean alt = (rowIndex % 2 == 0);   // alternating row background

        CellStyle base = alt ? altRowStyle(wb) : baseRowStyle(wb);

        // # (sequence)
        setCell(row, COL_SEQ,     String.valueOf(seqNum), base);
        // Timestamp
        setCell(row, COL_TS,      r.getTimestamp(),       base);
        // Username
        setCell(row, COL_USER,    r.getUsername(),        base);
        // Job title
        setCell(row, COL_TITLE,   r.getJobTitle(),        base);
        // Company
        setCell(row, COL_COMPANY, r.getCompany(),         base);
        // Status — coloured badge
        Cell statusCell = row.createCell(COL_STATUS);
        statusCell.setCellValue(r.getStatusLabel());
        statusCell.setCellStyle(statusStyle(wb, r.getStatus()));
        // Job URL
        setUrlCell(wb, row, COL_JOBURL,   r.getJobUrl(),   base);
        // Final URL
        setUrlCell(wb, row, COL_FINALURL, r.getFinalUrl(), base);
        // Notes
        setCell(row, COL_NOTES, r.getNotes(), base);
        // Run ID
        setCell(row, COL_RUN,   runId,        base);

        // Re-apply auto-filter to cover all data rows including the new one
        sheet.setAutoFilter(new CellRangeAddress(1, rowIndex, 0, HEADERS.length - 1));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Summary sheet — rebuilt from scratch on finish()
    // ─────────────────────────────────────────────────────────────────────────
    private void buildSummarySheet(XSSFWorkbook wb, XSSFSheet sum, XSSFSheet data) {
        // Count statuses across ALL rows (skip title row 0 and header row 1)
        int total = 0, success = 0, direct = 0, failed = 0, skipped = 0, redirected = 0;
        int lastRow = data.getLastRowNum();
        for (int ri = 2; ri <= lastRow; ri++) {
            Row row = data.getRow(ri);
            if (row == null) continue;
            Cell sc = row.getCell(COL_STATUS);
            if (sc == null) continue;
            String st = sc.getStringCellValue().trim();
            total++;
            switch (st) {
                case "SUCCESS"      -> success++;
                case "DIRECT_APPLY" -> direct++;
                case "FAILED"       -> failed++;
                case "SKIPPED"      -> skipped++;
                case "REDIRECTED"   -> redirected++;
            }
        }
        int applied = success + direct;
        double rate = total > 0 ? (double) applied / total * 100.0 : 0.0;

        // Title
        sum.setColumnWidth(0, 38 * 256);
        sum.setColumnWidth(1, 16 * 256);
        sum.setColumnWidth(2, 14 * 256);

        Row t = sum.createRow(0); t.setHeightInPoints(28);
        Cell tc = t.createCell(0);
        tc.setCellValue("  Applybot — Application Summary  (all users · all runs)");
        tc.setCellStyle(titleStyle(wb));
        sum.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        // Stats table
        Object[][] rows = {
                { "Total applications scanned",          total      },
                { "Applied — chatbot / popup",            success    },
                { "Applied — direct (no questions)",      direct     },
                { "Total applied",                        applied    },
                { "Failed",                               failed     },
                { "Skipped (already applied / not found)",skipped    },
                { "Redirected to company site",           redirected },
                { "Overall success rate",                 String.format("%.1f%%", rate) },
        };

        CellStyle labelSt = sumLabelStyle(wb);
        CellStyle valueSt = sumValueStyle(wb);

        for (int i = 0; i < rows.length; i++) {
            Row row = sum.createRow(i + 2);
            row.setHeightInPoints(20);
            Cell lc = row.createCell(0);
            lc.setCellValue(rows[i][0].toString());
            lc.setCellStyle(labelSt);

            Cell vc = row.createCell(1);
            if (rows[i][1] instanceof Integer n) vc.setCellValue(n);
            else vc.setCellValue(rows[i][1].toString());
            vc.setCellStyle(valueSt);
        }

        // Status legend
        Row sep = sum.createRow(11); sep.setHeightInPoints(10);
        Row bh  = sum.createRow(12); bh.setHeightInPoints(20);
        Cell bhc = bh.createCell(0);
        bhc.setCellValue("Status legend");
        bhc.setCellStyle(headerStyle(wb));
        sum.addMergedRegion(new CellRangeAddress(12, 12, 0, 2));

        Object[][] legend = {
                { "SUCCESS",      "ChatBot or Popup flow completed",    C_SUCCESS  },
                { "DIRECT_APPLY", "Applied directly — no questions",    C_DIRECT   },
                { "FAILED",       "Apply attempted but error occurred", C_FAILED   },
                { "SKIPPED",      "Already applied / button not found", C_SKIPPED  },
                { "REDIRECTED",   "Naukri sent to company own site",    C_REDIR    },
        };
        for (int i = 0; i < legend.length; i++) {
            Row lr = sum.createRow(13 + i); lr.setHeightInPoints(18);
            Cell badge = lr.createCell(0);
            badge.setCellValue(legend[i][0].toString());
            badge.setCellStyle(statusStyleByHex(wb, legend[i][2].toString()));
            Cell desc = lr.createCell(1);
            desc.setCellValue(legend[i][1].toString());
            desc.setCellStyle(baseRowStyle(wb));
            sum.addMergedRegion(new CellRangeAddress(13 + i, 13 + i, 1, 2));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cell helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private void setUrlCell(XSSFWorkbook wb, Row row, int col,
                            String url, CellStyle fallback) {
        Cell c = row.createCell(col);
        if (url != null && !url.isBlank() && url.startsWith("http")) {
            c.setCellValue(url);
            XSSFHyperlink link = wb.getCreationHelper().createHyperlink(HyperlinkType.URL);
            link.setAddress(url);
            c.setHyperlink(link);
            c.setCellStyle(hyperlinkStyle(wb));
        } else {
            c.setCellValue(url != null ? url : "—");
            c.setCellStyle(fallback);
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Style factories — recreated fresh each call (XSSFWorkbook caches them)
    // ─────────────────────────────────────────────────────────────────────────
    private XSSFCellStyle titleStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(hex(C_TITLE));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 13);
        f.setColor(hex(C_WHITE)); f.setFontName("Arial");
        s.setFont(f); borders(s); return s;
    }

    private XSSFCellStyle headerStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(hex(C_HEADER));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10);
        f.setColor(hex(C_WHITE)); f.setFontName("Arial");
        s.setFont(f); borders(s); return s;
    }

    private XSSFCellStyle baseRowStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setFontName("Arial"); f.setFontHeightInPoints((short) 10);
        s.setFont(f); borders(s); return s;
    }

    private XSSFCellStyle altRowStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(hex(C_ALT));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setFontName("Arial"); f.setFontHeightInPoints((short) 10);
        s.setFont(f); borders(s); return s;
    }

    private XSSFCellStyle hyperlinkStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont();
        f.setFontName("Arial"); f.setFontHeightInPoints((short) 10);
        f.setUnderline(FontUnderline.SINGLE);
        f.setColor(hex(C_LINK));
        s.setFont(f); borders(s); return s;
    }

    private XSSFCellStyle statusStyle(XSSFWorkbook wb, ApplicationResult.Status status) {
        return statusStyleByHex(wb, colorOf(status));
    }

    private XSSFCellStyle statusStyleByHex(XSSFWorkbook wb, String hexColor) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(hex(hexColor));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 9);
        f.setColor(hex(C_WHITE)); f.setFontName("Arial");
        s.setFont(f); borders(s); return s;
    }

    private XSSFCellStyle sumLabelStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(hex(C_SUM_BG));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont();
        f.setFontName("Arial"); f.setFontHeightInPoints((short) 11);
        f.setColor(hex(C_WHITE));
        s.setFont(f); borders(s); return s;
    }

    private XSSFCellStyle sumValueStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontName("Arial"); f.setFontHeightInPoints((short) 13);
        s.setFont(f); borders(s); return s;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void borders(XSSFCellStyle s) {
        XSSFColor bc = hex(C_BORDER);
        s.setBorderTop(BorderStyle.THIN);    s.setTopBorderColor(bc);
        s.setBorderBottom(BorderStyle.THIN); s.setBottomBorderColor(bc);
        s.setBorderLeft(BorderStyle.THIN);   s.setLeftBorderColor(bc);
        s.setBorderRight(BorderStyle.THIN);  s.setRightBorderColor(bc);
    }

    private XSSFColor hex(String rgb6) {
        // accepts 6-char RGB "1A1A2E"
        int r = Integer.parseInt(rgb6.substring(0, 2), 16);
        int g = Integer.parseInt(rgb6.substring(2, 4), 16);
        int b = Integer.parseInt(rgb6.substring(4, 6), 16);
        return new XSSFColor(new byte[]{ (byte) r, (byte) g, (byte) b }, null);
    }

    private String colorOf(ApplicationResult.Status status) {
        return switch (status) {
            case SUCCESS      -> C_SUCCESS;
            case DIRECT_APPLY -> C_DIRECT;
            case FAILED       -> C_FAILED;
            case SKIPPED      -> C_SKIPPED;
            case REDIRECTED   -> C_REDIR;
        };
    }
}