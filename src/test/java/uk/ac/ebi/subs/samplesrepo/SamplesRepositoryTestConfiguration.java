package uk.ac.ebi.subs.samplesrepo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SamplesRepositoryTestConfiguration {

    @Bean
    public SampleService sampleService() {
        return new SampleServiceImp();
    }
}
