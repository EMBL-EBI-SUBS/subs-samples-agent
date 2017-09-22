package uk.ac.ebi.subs.agent.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Sample;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
public class IntegrityServiceTest {

    @Autowired
    private IntegrityService integrityService;

    private Sample sample;

    @Before
    public void setUp() throws Exception {
        sample = new Sample();
        sample.setAlias("ERS821816");
        Team team = new Team();
        team.setName("aap-users-domain");
        sample.setTeam(team);
    }

    @Test
    public void sampleDoesExistTest() {
       assertTrue(integrityService.doesSampleExistInBiosamples(sample));
    }

    @Test
    public void sampleDoesNotExistTest() {
        sample.setAlias("random_alias");
        assertTrue(!integrityService.doesSampleExistInBiosamples(sample));
    }
}
