package com.timunas.core;

import org.junit.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CompetitorTest {

    @Test
    public void creation() {
        int number = 1;
        String name = "John Doe";
        String club = "Benfica";
        LocalTime time = LocalTime.parse("20:30:04");

        Competitor comp = new Competitor(number, name, club, time);

        assertThat(comp.getNumber()).isEqualTo(number);
        assertThat(comp.getName()).isEqualTo(name);
        assertThat(comp.getClub()).isEqualTo(club);
        assertThat(comp.getTime()).isEqualTo(time);
    }

    @Test
    public void comparison() {
        int number = 1;
        String name = "John Doe";
        String club = "Benfica";
        LocalTime time = LocalTime.parse("20:30:04");
        Competitor comp = new Competitor(number, name, club, time);

        int otherNumber = 2;
        String otherName = "Jane Doe";
        LocalTime otherTime = LocalTime.parse("20:29:04");
        Competitor otherComp = new Competitor(otherNumber, otherName, club, otherTime);

        assertThat(otherComp.compareTo(comp)).isEqualTo(-1);
        assertThat(comp.compareTo(otherComp)).isEqualTo(1);
        assertThat(comp.compareTo(comp)).isEqualTo(0);
    }
}
