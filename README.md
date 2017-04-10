# Samples Agent
This module contains the USI Sample Agent which is the agent in charge of mediating the comunication between USI and [BioSamples](https://www.ebi.ac.uk/biosamples/). This agent will submit new samples, update and fetch existing ones. It has an external dependency on [BioSamples v4.0.0](https://github.com/EBIBioSamples/biosamples-v4), which at this point is still under active development. The agent 'listens' to two queues from RabbitMQ and processes  three types of requests:
- Submission of new samples
- Update of existing samples
- Fetching existing samples

## About
This is a Spring Boot application, to run it you'll have to download the entire subs project, this agent parent project.
The agent is structured as follows:

- SamplesAgentApplication
- agent/
  - Listener
  - SamplesProcessor
  - CertificatesGenerator
  - services/
    - Submission
    - Update
    - Fetch
  - converters/
    - Attribute BioSamples to USI
    - Attribute USI to BioSamples
    - Relationship BioSamples to USI
    - Relationship USI to BioSamples
    - Sample BioSamples to USI
    - Sample USI to BioSamples
  - exceptions/
    - SampleNotFoundException
    
## License
See the [LICENSE](../LICENSE) file in parent project for license rights and limitations (Apache 2.0).