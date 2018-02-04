package com.timunas.core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class allows to create a {@link Race} with a certain name and {@link Competitor}s.
 *
 * @author Timunas
 * @since  1.0
 */
public class Race {

    private final int number;
    private final String name;
    private final LocalTime time;
    private final List<Competitor> competitors;

    /**
     * Creates a new instance of {@link Race}
     *
     * @param name of {@link Race}
     */
    public Race(int number, String name, LocalTime time) {
        this.number = number;
        this.name = name;
        this.time = time;
        competitors = new ArrayList<>();
    }

    /**
     * Gets {@link Race} number.
     *
     * @return {@link Race} int number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets {@link Race} name.
     *
     * @return {@link Race} string name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets {@link Race} time.
     *
     * @return {@link Race} time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Gets {@link Race} competitors list.
     *
     * @return {@link Race} competitors list
     */
    public List<Competitor> getCompetitors() {
        return new ArrayList<>(competitors);
    }

    /**
     * Adds a new {@link Competitor}.
     *
     * @param competitor to be added to race competitors list
     */
    public void addCompetitor(Competitor competitor) {
        competitors.add(competitor);
    }

    /**
     * Removes a {@link Competitor} from {@link Race} list.
     *
     * @param competitor to be removed from race competitors list
     */
    public void removeCompetitor(Competitor competitor) {
        if(!competitors.isEmpty()) competitors.remove(competitor);
    }

    /**
     * Sorts the {@link Competitor}s list according to the time of each {@link Competitor}.
     *
     * @return {@link Race} competitors sorted list
     */
    public List<Competitor> sort() {
        List<Competitor> cloned = new ArrayList<>(competitors);
        Collections.sort(cloned);
        return cloned;
    }
}
