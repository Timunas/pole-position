package com.timunas.core;

import org.junit.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class RaceTest {

    @Test
    public void creation() {
        String name = "Awesome Race";
        LocalTime raceTime = LocalTime.parse("09:00");
        Race race = new Race(1, name, raceTime);

        assertThat(race.getName()).isEqualTo(name);
        assertThat(race.getCompetitors()).isEmpty();
        assertThat(race.getNumber()).isEqualTo(1);
        assertThat(race.getTime()).isEqualTo(raceTime);
    }

    @Test
    public void noCompetitors() {
        String name = "Race Without Competitors";
        LocalTime raceTime = LocalTime.parse("09:00");
        Race race = new Race(1, name, raceTime);

        LocalTime time = LocalTime.parse("20:30:04");
        Competitor comp = new Competitor("John Doe", "Benfica", time);

        assertThat(race.getCompetitors()).isEmpty();
        race.removeCompetitor(comp);
        assertThat(race.getCompetitors()).isEmpty();
        assertThat(race.sort()).isEmpty();
    }

    @Test
    public void assortment() {
        String name = "John Doe";
        String club = "Benfica";
        LocalTime time = LocalTime.parse("20:30:04");
        Competitor comp = new Competitor(name, club, time);

        String otherName = "Jane Doe";
        LocalTime otherTime = LocalTime.parse("20:29:04");
        Competitor otherComp = new Competitor(otherName, club, otherTime);

        String anotherName = "William Doe";
        LocalTime anotherTime = LocalTime.parse("19:29:02");
        Competitor anotherComp = new Competitor(anotherName, club, anotherTime);

        LocalTime raceTime = LocalTime.parse("09:00");
        Race race = new Race(1,"Assortment Race", raceTime);

        assertThat(race.getCompetitors()).isEmpty();
        assertThat(race.sort()).isEmpty();

        race.addCompetitor(comp);
        assertThat(race.getCompetitors()).hasSize(1).contains(comp);
        assertThat(race.sort()).hasSize(1).contains(comp);

        race.addCompetitor(otherComp);
        assertThat(race.getCompetitors()).hasSize(2).contains(comp, otherComp);
        assertThat(race.sort()).hasSize(2).containsExactly(otherComp, comp);

        race.addCompetitor(anotherComp);
        assertThat(race.getCompetitors()).hasSize(3).contains(comp, otherComp, anotherComp);
        assertThat(race.sort()).hasSize(3).containsExactly(anotherComp, otherComp, comp);


        race.removeCompetitor(otherComp);
        assertThat(race.getCompetitors()).hasSize(2).contains(comp, anotherComp);
        assertThat(race.sort()).hasSize(2).containsExactly(anotherComp, comp);

        race.removeCompetitor(anotherComp);
        race.removeCompetitor(comp);
        assertThat(race.getCompetitors()).isEmpty();
        assertThat(race.sort()).isEmpty();
    }
}
