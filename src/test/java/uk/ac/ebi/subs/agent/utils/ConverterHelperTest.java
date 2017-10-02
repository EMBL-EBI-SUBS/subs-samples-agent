package uk.ac.ebi.subs.agent.utils;


import org.junit.Test;

import java.time.Instant;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.subs.agent.utils.ConverterHelper.getInstantFromString;

public class ConverterHelperTest {

    private final String dateFormatRegex = "\\b[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(.[0-9]+)?Z\\b";

    private final String dateTHourMinute = "2017-09-25T11:00";

    @Test
    public void getInstantFromDateHourMinuteSecond() {
        String originalTime = "2017-09-25T11:00:13Z";

        Instant instant = getInstantFromString(originalTime);

        assertThat(instant.toString(), matchesPattern(dateFormatRegex));
        assertTrue(instant.toString().startsWith(dateTHourMinute));
    }

    @Test
    public void getInstantFromDateHourMinuteSecondNoZ() {
        String originalTime = "2017-09-25T11:00:13";

        Instant instant = getInstantFromString(originalTime);

        assertThat(instant.toString(), matchesPattern(dateFormatRegex));
        assertTrue(instant.toString().startsWith(dateTHourMinute));
    }

    @Test
    public void getInstantFromDateHourMinuteSecondSecondNoZ() {
        String originalTime = "2017-09-25T11:00:13.0000004";

        Instant instant = getInstantFromString(originalTime);

        assertTrue(instant.toString().matches(dateFormatRegex));
        assertTrue(instant.toString().startsWith(dateTHourMinute));
    }

    @Test
    public void getInstantFromDateHourMinuteSecondSecondZ() {
        String originalTime = "2017-09-25T11:00:13.003004Z";

        Instant instant = getInstantFromString(originalTime);

        assertTrue(instant.toString().matches(dateFormatRegex));
        assertTrue(instant.toString().startsWith(dateTHourMinute));
    }

    @Test
    public void getInstantFromDateHourMinute() {
        String originalTime = "2017-09-25T11:00";

        Instant instant = getInstantFromString(originalTime);

        assertTrue(instant.toString().matches(dateFormatRegex));
        assertTrue(instant.toString().startsWith("2017-09-25T"));
    }

    @Test
    public void getInstantFromDate() {
        String originalTime = "2017-09-25";

        Instant instant = getInstantFromString(originalTime);

        assertTrue(instant.toString().matches(dateFormatRegex));
        assertTrue(instant.toString().startsWith("2017-09-25"));
    }

    @Test
    public void test() {
        String originalTime = "2017-09-25T10:04Z";

        Instant instant = getInstantFromString(originalTime);

        assertTrue(instant.toString().matches(dateFormatRegex));
        assertTrue(instant.toString().startsWith("2017-09-25"));
    }
}
