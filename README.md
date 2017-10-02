# Samples Agent

[![Build Status](https://travis-ci.org/EMBL-EBI-SUBS/subs-samples-agent.svg?branch=master)]([https://travis-ci.org/EMBL-EBI-SUBS/subs-samples-agent])

This repository contains the Unified Submission Interface (USI) Samples Agent which is the microservice in charge of handling the communication between USI and [BioSamples](https://www.ebi.ac.uk/biosamples/).

## About 
This agent will submit new samples, update and fetch existing ones. It has an external dependency on [BioSamples v4.0.0](https://github.com/EBIBioSamples/biosamples-v4), 
which at this point is still under active development. The agent 'listens' to two queues from RabbitMQ and processes  three types of requests:
- Submission of new samples
- Update of existing samples
- Fetching existing samples

This is a Spring Boot application. The agent is structured as follows:

- SamplesAgentApplication
- agent/
  - Listener
  - SamplesProcessor
  - CertificatesGenerator
  - services/
    - Submission
    - Update
    - Fetch
    - Integrity _(avoids unwanted resubmission)_
  - converters/
    - Attribute BioSamples to USI
    - Attribute USI to BioSamples
    - Relationship BioSamples to USI
    - Relationship USI to BioSamples
    - Sample BioSamples to USI
    - Sample USI to BioSamples
    
## Running it
### Dependencies
The Samples agent has dependencies on:
- [BioSamples v4.0.0](https://github.com/EBIBioSamples/biosamples-v4)
- [RabbitMQ](https://www.rabbitmq.com/)

### Execution
The samples agent comes with a gradle wrapper and can be executed as follows:
```bash
$ cd subs-samples-agent/
$ ./gradlew bootRun
```

## License
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details.
