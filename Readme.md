# Koldfish Data Access Module
- Creator: <span property="http://purl.org/dc/elements/1.1/creator">Leon Kastler</span>

The KoLDfish data access module, offers a simple API for fast access to the Linked Open Data cloud.
It provides a Java API, a simple implementation for it as well as a simple crawler for linked data.

## Overview
The project contains a framework for accessing RDF by dereferencing URIs and a lightweight crawler based upon that.

## Setup
Download the code from [github](https://github.com/lkastler/koldfish-dam).
It contains two modules:
- koldfish-dam-api for clients of the DAM backend
- koldfish-dam-backend the backend that handles requests and sends messages
Please have a look into the specific readme files for both modules.

## Usage
- For Java-based implementations look into the [Java API description](api_java.md).

## Used Projects
In koldfish-dam, we use the following dependencies:
- [Apache ActiveMQ](http://activemq.apache.org) as middleware (Apache 2.0 Licence).
- [Apache Http components] (http://hc.apache.org) to access sources via HTTM (Apache 2.0 License).
- [Apache Log4j 2.x](logging.apache.org/log4j/2.x/) for logging (Apache 2.0 License).
- [Apache Maven 3.3.X](maven.apache.org/) as build tool.
- [junit](junit.org) for testing (Eclispe Public License 1.0)
- [Google Guava](https://github.com/google/guava) for improved concurrent processes (Apache 2.0 Lisence).


