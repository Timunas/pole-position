package com.timunas.core;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

public class ExcelGenerator {

    private static int nbrColumns = 5;

    public static boolean generate(List<Race> raceList, Path file, String sheetName) throws IOException {
        Workbook wb = new HSSFWorkbook();
        String safeName = WorkbookUtil.createSafeSheetName(sheetName);
        wb.createSheet(safeName);

        int rowIndex = 0;
        for (Race race: raceList) {
            // Create headers
            raceHeader(wb, race, ++rowIndex);
            header(wb, ++rowIndex);

            // Create competitors rows
            int classification = 0;
            for (Competitor competitor : race.sort()) {
                competitorRow(wb, ++rowIndex, competitor, ++classification);
            }

            // Create two empty rows to separate races
            wb.getSheet(safeName).createRow(++rowIndex);
            wb.getSheet(safeName).createRow(++rowIndex);
        }

        // Resize columns to fit text
        IntStream.range(0, nbrColumns).forEach(columnIndex -> wb.getSheet(safeName).autoSizeColumn(columnIndex));

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream(file.toFile())) {
            wb.write(fileOut);
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            wb.close();
        }

        return true;
    }

    private static void raceHeader(Workbook wb, Race race, int rowIndex) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        CreationHelper createHelper = wb.getCreationHelper();
        Row header = wb.getSheetAt(0).createRow(rowIndex);
        CellStyle defaultStyle = defaultStyle(wb);
        defaultStyle.setAlignment(HorizontalAlignment.CENTER);

        // Race number
        Cell raceNbr = header.createCell(0);
        raceNbr.setCellStyle(defaultStyle);
        raceNbr.setCellValue(createHelper.createRichTextString("Race "+ race.getNumber()));
        // Race Name
        Cell raceName = header.createCell(1);
        raceName.setCellStyle(defaultStyle);
        raceName.setCellValue(createHelper.createRichTextString(race.getName()));
        //TODO - Place something in these empty cells
        header.createCell(2).setCellStyle(defaultStyle);
        header.createCell(3).setCellStyle(defaultStyle);

        wb.getSheetAt(0).addMergedRegion(new CellRangeAddress(rowIndex, rowIndex,1,3));
        // Race Time
        Cell raceTime = header.createCell(4);
        raceTime.setCellStyle(defaultStyle);
        raceTime.setCellValue(createHelper.createRichTextString(race.getTime().format(dtf)));
    }

    private static void header(Workbook wb, int rowIndex) {
        CreationHelper createHelper = wb.getCreationHelper();
        Row header = wb.getSheetAt(0).createRow(rowIndex);
        CellStyle defaultStyle = defaultStyle(wb);
        defaultStyle.setAlignment(HorizontalAlignment.CENTER);

        //Number
        Cell number = header.createCell(0);
        number.setCellStyle(defaultStyle);
        number.setCellValue(createHelper.createRichTextString("Nbr"));
        // Name
        Cell name = header.createCell(1);
        name.setCellStyle(defaultStyle);
        name.setCellValue(createHelper.createRichTextString("Name"));
        // Club
        Cell club = header.createCell(2);
        club.setCellStyle(defaultStyle);
        club.setCellValue(createHelper.createRichTextString("Club"));
        // Time
        Cell time = header.createCell(3);
        time.setCellStyle(defaultStyle);
        time.setCellValue(createHelper.createRichTextString("Time"));
        // Classification
        Cell raceTime = header.createCell(4);
        raceTime.setCellStyle(defaultStyle);
        raceTime.setCellValue(createHelper.createRichTextString("Class."));
    }

    private static void competitorRow(Workbook wb, int rowIndex, Competitor competitor, int classification) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SS");
        CreationHelper createHelper = wb.getCreationHelper();
        Row header = wb.getSheetAt(0).createRow(rowIndex);
        CellStyle defaultStyle = defaultStyle(wb);
        defaultStyle.setAlignment(HorizontalAlignment.CENTER);

        //Number
        Cell number = header.createCell(0);
        number.setCellStyle(defaultStyle);
        number.setCellValue(competitor.getNumber());
        // Name
        Cell name = header.createCell(1);
        CellStyle leftStyle = defaultStyle(wb);
        leftStyle.setAlignment(HorizontalAlignment.LEFT);
        name.setCellStyle(leftStyle);
        name.setCellValue(createHelper.createRichTextString(competitor.getName()));
        // Club
        Cell club = header.createCell(2);
        club.setCellStyle(defaultStyle);
        club.setCellValue(createHelper.createRichTextString(competitor.getClub()));
        // Time
        Cell time = header.createCell(3);
        time.setCellStyle(defaultStyle);
        time.setCellValue(createHelper.createRichTextString(competitor.getTime().format(dtf)));
        // Classification
        Cell raceTime = header.createCell(4);
        raceTime.setCellStyle(defaultStyle);
        raceTime.setCellValue(classification);
    }

    private static CellStyle defaultStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        return style;
    }
}
