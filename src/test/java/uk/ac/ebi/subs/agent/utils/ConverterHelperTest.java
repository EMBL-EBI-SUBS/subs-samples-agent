package uk.ac.ebi.subs.agent.utils;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;

import static uk.ac.ebi.subs.agent.utils.ConverterHelper.getInstantFromString;

public class ConverterHelperTest {

    @Test
    public void getInstantFromStringTest() {

        String originalTime = "2017-09-25T11:00:13Z";

        Instant instant = getInstantFromString(originalTime);

        Assert.assertEquals(originalTime, instant.toString());
    }
}
