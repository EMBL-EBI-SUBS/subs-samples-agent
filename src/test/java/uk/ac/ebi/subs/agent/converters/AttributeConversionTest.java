package uk.ac.ebi.subs.agent.converters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.agent.utils.TestUtils;
import uk.ac.ebi.subs.data.component.Attribute;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        UsiAttributeToBsdAttribute.class,
        BsdAttributeToUsiAttribute.class,
        TestUtils.class
})
@EnableAutoConfiguration
public class AttributeConversionTest {

    @Autowired
    UsiAttributeToBsdAttribute toBsdAttribute;
    @Autowired
    BsdAttributeToUsiAttribute toUsiAttribute;

    @Autowired
    TestUtils utils;

    private Map.Entry<String, Collection<Attribute>> usiAttribute;
    private uk.ac.ebi.biosamples.model.Attribute bsdAttribute;

    @Before
    public void setUp() {
        usiAttribute = utils.generateUsiAttribute();
        bsdAttribute = utils.generateBsdAttribute();
    }

    @Test
    public void convertFromUsiAttribute() {
        uk.ac.ebi.biosamples.model.
                Attribute conversion = toBsdAttribute.convert(usiAttribute);

        Map.Entry<String, Collection<Attribute>> conversionBack = toUsiAttribute.convert(conversion);

        assertEquals(usiAttribute, conversionBack);
    }

    @Test
    public void convertFromBsdAttribute() {
        Map.Entry<String, Collection<Attribute>> conversion = toUsiAttribute.convert(bsdAttribute);

        uk.ac.ebi.biosamples.model.
                Attribute conversionBack = toBsdAttribute.convert(conversion);

        assertEquals(bsdAttribute, conversionBack);
    }

}
