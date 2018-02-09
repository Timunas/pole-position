package com.timunas.core;

import java.time.LocalTime;

/**
 * This class consists in a {@link Competitor} instance that saves name and classification info.
 *
 * @author Timunas
 * @since  1.0
 */
public class Competitor implements Comparable<Competitor> {

    private final int number;
    private final String name;
    private final String club;
    private final LocalTime time;
    private final CompetitorResult competitorResult;

    private Competitor(int number, String name, String club, LocalTime time, CompetitorResult competitorResult) {
        this.number = number;
        this.name = name;
        this.club = club;
        this.time = time;
        this.competitorResult = competitorResult;
    }


    /**
     * Creates a new instance of {@link Competitor}
     *
     * @param number of {@link Competitor}
     * @param name of {@link Competitor}
     * @param club of {@link Competitor}
     * @param time of {@link Competitor} race
     */
    public Competitor(int number, String name, String club, LocalTime time) {
        this(number, name, club, time, null);
    }

    /**
     * Creates a new instance of {@link Competitor}
     *
     * @param number of {@link Competitor}
     * @param name of {@link Competitor}
     * @param club of {@link Competitor}
     * @param competitorResult - result state of {@link Competitor}
     */
    public Competitor(int number, String name, String club, CompetitorResult competitorResult) {
        this(number, name, club, LocalTime.MAX, competitorResult);
    }

    /**
     * Gets {@link Competitor} number.
     *
     * @return {@link Competitor} number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets {@link Competitor} name.
     *
     * @return {@link Competitor} string name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets {@link Competitor} club.
     *
     * @return {@link Competitor} club string name
     */
    public String getClub() {
        return club;
    }

    /**
     * Gets {@link Competitor} race time.
     *
     * @return {@link Competitor} time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Gets {@link Competitor} race result.
     *
     * @return {@link CompetitorResult} result
     */
    public CompetitorResult getCompetitorResult() {
        return competitorResult;
    }


    /**
     * Compares this {@link Competitor#time} to another {@link Competitor#time}.
     *
     * @param other {@link Competitor} which will be compared to current one
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(Competitor other) {
        if (competitorResult == CompetitorResult.DNF) {
            return this.time.minusNanos(2).compareTo(other.getTime());
        } else if (competitorResult == CompetitorResult.DSQ) {
            return this.time.minusNanos(1).compareTo(other.getTime());
        } else {
            return this.time.compareTo(other.getTime());
        }
    }
}
