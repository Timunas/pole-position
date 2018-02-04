package com.timunas.core;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
                String firstCell = cellIterator.next().getStringCellValue();
                // Race row
                if (!firstCell.isEmpty()) {
                    if (currentRace != null) {
                        races.add(currentRace);
                    }
                    currentRace = raceHeader(firstCell, cellIterator);
                    // Discard next headers row
                    if (rowIterator.hasNext()) {
                        rowIterator.next();
                    }
                } else if (currentRace != null) { // Competitor row
                    currentRace.addCompetitor(competitorRow(cellIterator));
                }
            }
        }

        if (currentRace != null) {
            races.add(currentRace);
        }

        return races;
    }

    private static Race raceHeader(String firstCell, Iterator<Cell> cellIterator) throws NoSuchElementException {
        String name = cellIterator.next().getStringCellValue();
        // Iterate the two merged cells
        cellIterator.next();
        cellIterator.next();
        String rawTime = cellIterator.next().getStringCellValue();

        int number = Integer.valueOf(firstCell.replace("Race ", "").trim());
        return new Race(number, name, LocalTime.parse(rawTime));
    }


    private static Competitor competitorRow(Iterator<Cell> cellIterator) {
        String name = cellIterator.next().getStringCellValue();
        String club = cellIterator.next().getStringCellValue();
        String rawTime = cellIterator.next().getStringCellValue();

        Optional<CompetitorResult> competitorResult = Arrays.stream(CompetitorResult.values())
                .filter(r -> r.name().equalsIgnoreCase(rawTime)).findAny();

        return competitorResult
                .map(r -> new Competitor(name, club, r))
                .orElseGet(() -> new Competitor(name, club, LocalTime.parse(rawTime)));
    }

}
