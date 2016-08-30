package uk.ac.ebi.subs.samplesrepo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableAutoConfiguration
@EnableMongoRepositories(basePackages = {"uk.ac.ebi.subs.samplesrepo"})
public class RepositoryConfiguration {
}
