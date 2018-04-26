package com.timunas.core;

import java.time.LocalTime;

/**
 * This enum represents the possible {@link Competitor} results when result is not time.
 *
 * @author Timunas
 * @since  1.0
 */ 
public enum CompetitorResult {
    DNF(LocalTime.MAX.minusNanos(2)), // Did Not Finish
    DSQ(LocalTime.MAX.minusNanos(1)), // Disqualified
    DNS(LocalTime.MAX); // Did Not Start

    private LocalTime time;

    CompetitorResult(LocalTime time) {
        this.time = time;
    }

    public LocalTime time() {
        return time;
    }

    public static CompetitorResult fromString(String text) {
        for (CompetitorResult result : CompetitorResult.values()) {
            if (result.toString().equalsIgnoreCase(text)) {
                return result;
            }
        }
        return null;
    }
}
