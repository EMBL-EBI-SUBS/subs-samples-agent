package uk.ac.ebi.subs.agent.converters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.agent.utils.TestUtils;
import uk.ac.ebi.subs.data.submittable.Sample;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        UsiSampleToBsdSample.class,
        BsdSampleToUsiSample.class,
        UsiAttributeToBsdAttribute.class,
        BsdAttributeToUsiAttribute.class,
        UsiRelationshipToBsdRelationship.class,
        BsdRelationshipToUsiRelationship.class,
        TestUtils.class
})
public class SampleConverterTest {

    @Autowired
    UsiSampleToBsdSample toBsdSample;
    @Autowired
    BsdSampleToUsiSample toUsiSample;

    @Autowired
    TestUtils utils;

    private Sample usiSample;
    private uk.ac.ebi.biosamples.models.Sample bsdSample;

    @Before
    public void setUp() {
        usiSample = utils.generateUsiSample();
        bsdSample = utils.generateBsdSample();
    }

    @Test
    public void convertFromUsiSample() {
        uk.ac.ebi.biosamples.models.
                Sample conversion = toBsdSample.convert(usiSample);

        Sample conversionBack = toUsiSample.convert(conversion);

        Assert.assertEquals(usiSample, conversionBack);
    }

    @Test
    public void convertFromBsdSample() {
        Sample conversion = toUsiSample.convert(bsdSample);

        uk.ac.ebi.biosamples.models.
                Sample conversionBack = toBsdSample.convert(conversion);

        Assert.assertEquals(bsdSample, conversionBack);
    }

}
