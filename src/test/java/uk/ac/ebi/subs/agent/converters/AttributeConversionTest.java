package uk.ac.ebi.subs.agent.converters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        UsiAttributeToBsdAttribute.class,
        BsdAttributeToUsiAttribute.class
})
@EnableAutoConfiguration
public class AttributeConversionTest {

    @Autowired
    UsiAttributeToBsdAttribute toBsdAttribute;
    @Autowired
    BsdAttributeToUsiAttribute toUsiAttribute;

    private Attribute usiAttribute;
    private uk.ac.ebi.biosamples.models.Attribute bsdAttribute;

    @Before
    public void setUp() {
        generateUsiAttribute();
        generateBsdAttribute();
    }

    @Test
    public void convertFromUsiAttribute() {
        uk.ac.ebi.biosamples.models.
                Attribute conversion = toBsdAttribute.convert(usiAttribute);

        Attribute conversionBack = toUsiAttribute.convert(conversion);

        Assert.assertEquals(usiAttribute, conversionBack);
    }

    @Test
    public void convertFromBsdAttribute() {
        Attribute conversion = toUsiAttribute.convert(bsdAttribute);

        uk.ac.ebi.biosamples.models.
                Attribute conversionBack = toBsdAttribute.convert(conversion);

        Assert.assertEquals(bsdAttribute, conversionBack);
    }

    private void generateUsiAttribute() {
        usiAttribute = new Attribute();
        usiAttribute.setName("age");
        usiAttribute.setValue("55");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        usiAttribute.setTerms(Arrays.asList(term));
        usiAttribute.setUnits("year");
    }

    private void generateBsdAttribute() {
        bsdAttribute = uk.ac.ebi.biosamples.models.Attribute.build(
                "age",
                "55",
                "http://purl.obolibrary.org/obo/UO_0000036",
                "year"
        );
    }
}
