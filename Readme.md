# Koldfish Data Acquisition Module
The KoLDfish data aquisition module, offers a simple API for fast access to the Linked Open Data cloud.
It provides a Java API, a simple implementation for it as well as a simple crawler for linked data.

## Overview
The project contains a framework for accessing RDF by dereferencing URIs and a lightweight crawler based upon that.

## Setup
Download the code from [github](https://github.com/lkastler/koldfish-dam) and execute maven with `maven install`.

## Usage
- For Java-based implementations look into the [Java API description](api_java.md).

## Used Projects
In koldfish-dam, we use the following dependencies:
- [Apache Jena](http://jena.apache.org) for retrieving and parsing RDF data (Apache 2.0 Lisence).
- [Google Guava](https://github.com/google/guava) for improved concurrent processes (Apache 2.0 Lisence).
- [SLF4J](http://www.slf4j.org/) for logging (MIT License).
- uk.com.robust-it cloning for deep cloning (Apache 2.0 Lisence).
