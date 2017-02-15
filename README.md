#Samples Agent
[![Build Status](https://travis-ci.org/EMBL-EBI-SUBS/subs.svg?branch=SUBS-287-samples-agent)](https://travis-ci.org/EMBL-EBI-SUBS/subs)
Under active development on branch [SUBS-287-samples-agent](https://github.com/EMBL-EBI-SUBS/subs/tree/SUBS-287-samples-agent)

This module contains the USI Sample Agent which is the agent in charge of mediating the comunication between USI and [BioSamples](https://www.ebi.ac.uk/biosamples/). This agent will submit new samples, update and fetch existing ones. It has an external dependency on [BioSamples v4.0.0](https://github.com/EBIBioSamples/biosamples-v4), which at this point is still under active development.
##About
This is a Spring Boot application, to run it you'll have to download the entire subs project, this agent parent project.
The agent is structered as follows:
- Listener
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
