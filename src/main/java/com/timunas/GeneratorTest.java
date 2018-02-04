package com.timunas;

import com.timunas.core.Competitor;
import com.timunas.core.ExcelGenerator;
import com.timunas.core.Race;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GeneratorTest {
    public static void main(String[] args) throws IOException {
        List<Race> raceList = new ArrayList<>();
        String name = "Awesome Race";
        LocalTime raceTime = LocalTime.parse("09:00");
        Race race = new Race(1, name, raceTime);

        LocalTime time = LocalTime.parse("20:30:04");
        race.addCompetitor(new Competitor("John Doe", "Benfica", time));

        LocalTime otherTime = LocalTime.parse("20:29:04");
        race.addCompetitor(new Competitor("Jane Doe", "Manchester", otherTime));

        LocalTime anotherTime = LocalTime.parse("19:29:02");
        race.addCompetitor(new Competitor("William Doe", "Real Madrid", anotherTime));

        String name2 = "Not So Awesome Race";
        LocalTime raceTime2 = LocalTime.parse("10:00");
        Race race2 = new Race(2, name2, raceTime2);

        LocalTime time2 = LocalTime.parse("20:30:04");
        race2.addCompetitor(new Competitor("John Cena", "The People", time2));

        LocalTime otherTime2 = LocalTime.parse("20:29:04");
        race2.addCompetitor(new Competitor("Big Show", "Raw", otherTime2));

        LocalTime anotherTime2 = LocalTime.parse("19:29:02");
        race2.addCompetitor(new Competitor("Shawn Michaels", "SmackDown", anotherTime2));

        raceList.add(race);
        raceList.add(race2);

        ExcelGenerator.generate(raceList, Paths.get("teste.xls"),"Results");
    }
}
