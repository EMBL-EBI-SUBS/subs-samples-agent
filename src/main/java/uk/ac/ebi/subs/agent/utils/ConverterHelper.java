package uk.ac.ebi.subs.agent.utils;

import java.time.Instant;

public class ConverterHelper {

    public static Instant getInstantFromString(String dateTime) {
        try {
            if (dateTime.length() > 18 && !dateTime.endsWith("Z")) {
                dateTime = dateTime + "Z";
            }
            return Instant.parse(dateTime);
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format", e);
        }
    }
}
