package uk.ac.ebi.subs.agent.converters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.biosamples.model.Relationship;
import uk.ac.ebi.subs.agent.utils.TestUtils;
import uk.ac.ebi.subs.data.component.SampleRelationship;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        UsiRelationshipToBsdRelationship.class,
        BsdRelationshipToUsiRelationship.class,
        TestUtils.class
})
@EnableAutoConfiguration
public class RelationshipConverterTest {

    @Autowired
    UsiRelationshipToBsdRelationship toBsdRelationship;
    @Autowired
    BsdRelationshipToUsiRelationship toUsiRelationship;

    @Autowired
    TestUtils utils;

    private SampleRelationship usiRelationship;
    private Relationship bsdRelationship;

    @Before
    public void setUp() {
        usiRelationship = utils.generateUsiRelationship();
        bsdRelationship = utils.generateBsdRelationship();
    }

    @Test
    public void convertFromUsiRelationship() {
        Relationship conversion = toBsdRelationship.convert(usiRelationship);
        SampleRelationship conversionBack = toUsiRelationship.convert(conversion);
        Assert.assertEquals(usiRelationship, conversionBack);
    }

    @Test
    public void convertFromBsdRelationship() {
        SampleRelationship conversion = toUsiRelationship.convert(bsdRelationship);
        Relationship conversionBack = toBsdRelationship.convert(conversion);
        Assert.assertEquals(bsdRelationship, conversionBack);
    }

}
