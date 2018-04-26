package com.timunas.core;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.*;

public class ExcelLoader {

    public static List<Race> load(Path file) throws IOException {

        List<Race> races = new ArrayList<>();

        FileInputStream excelFile;

        excelFile = new FileInputStream(new File(file.toAbsolutePath().toString()));

        Workbook workbook = new HSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = datatypeSheet.iterator();

        Race currentRace = null;

        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();

            if (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                // Race row
                if (cell.getCellTypeEnum().equals(CellType.STRING)) {
                    if (currentRace != null) {
                        races.add(currentRace);
                    }
                    currentRace = raceHeader(cell, cellIterator);
                    // Discard next headers row
                    if (rowIterator.hasNext()) {
                        rowIterator.next();
                    }
                } else if (currentRace != null) { // Competitor row
                    currentRace.addCompetitor(competitorRow(cell, cellIterator));
                }
            }
        }

        if (currentRace != null) {
            races.add(currentRace);
        }

        return races;
    }

    private static Race raceHeader(Cell firstCell, Iterator<Cell> cellIterator) throws NoSuchElementException {
        String name = cellIterator.next().getStringCellValue();
        // Iterate the two merged cells
        cellIterator.next();
        cellIterator.next();
        String rawTime = cellIterator.next().getStringCellValue();

        int number = Integer.valueOf(firstCell.getStringCellValue().replace("Race ", "").trim());
        return new Race(number, name, LocalTime.parse(rawTime));
    }


    private static Competitor competitorRow(Cell firstCell, Iterator<Cell> cellIterator) {
        int nbr = (int) firstCell.getNumericCellValue();
        String name = cellIterator.next().getStringCellValue();
        String club = cellIterator.next().getStringCellValue();
        String rawTime = cellIterator.next().getStringCellValue();

        Optional<CompetitorResult> competitorResult;
        if (rawTime.equals("\u2014")) {
            competitorResult = Optional.ofNullable(CompetitorResult.fromString(cellIterator.next().getStringCellValue()));
        } else {
            competitorResult = Optional.empty();
        }

        return competitorResult
                .map(r -> new Competitor(nbr, name, club, r))
                .orElseGet(() -> new Competitor(nbr, name, club, LocalTime.parse(rawTime)));
    }

}
