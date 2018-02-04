package com.timunas.core;

import org.junit.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CompetitorTest {

    @Test
    public void creation() {
        String name = "John Doe";
        String club = "Benfica";
        LocalTime time = LocalTime.parse("20:30:04");

        Competitor comp = new Competitor(name, club, time);

        assertThat(comp.getName()).isEqualTo(name);
        assertThat(comp.getClub()).isEqualTo(club);
        assertThat(comp.getTime()).isEqualTo(time);
    }

    @Test
    public void comparison() {
        String name = "John Doe";
        String club = "Benfica";
        LocalTime time = LocalTime.parse("20:30:04");
        Competitor comp = new Competitor(name, club, time);

        String otherName = "Jane Doe";
        LocalTime otherTime = LocalTime.parse("20:29:04");
        Competitor otherComp = new Competitor(otherName, club, otherTime);

        assertThat(otherComp.compareTo(comp)).isEqualTo(-1);
        assertThat(comp.compareTo(otherComp)).isEqualTo(1);
        assertThat(comp.compareTo(comp)).isEqualTo(0);
    }
}
