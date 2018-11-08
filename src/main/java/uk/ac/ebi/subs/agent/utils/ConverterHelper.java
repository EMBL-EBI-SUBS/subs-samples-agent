package uk.ac.ebi.subs.agent.utils;

import java.time.Instant;

/**
 * This is a helper class to convert the given date from String format to a {@link Instant} date format.
 */
public class ConverterHelper {

    public static Instant getInstantFromString(String dateTime) {
        try {
            if (dateTime.length() >18 && !dateTime.endsWith("Z")) {
                return Instant.parse(dateTime + "Z");
            }
            if (dateTime.matches("\\b[0-9]{4}-[0-9]{2}-[0-9]{2}\\b")) { // date only yyyy-MM-dd
                return Instant.parse(dateTime + "T00:00:00Z");
            }
            if (dateTime.matches("\\b[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}\\b")) { // date, hours and minutes yyyy-MM-ddTHH:mm
                return Instant.parse(dateTime + ":00Z");
            }
            if (dateTime.matches("\\b[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}Z\\b")) { // date, hours and minutes and Z yyyy-MM-ddTHH:mmZ
                dateTime = dateTime.replace("Z", ":00Z");
            }
            return Instant.parse(dateTime);
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format", e);
        }
    }
}
