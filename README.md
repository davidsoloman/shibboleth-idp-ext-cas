# CAS Protocol Extension for Shibboleth 3.0 IdP

<https://github.com/serac/shibboleth-idp-ext-cas>

The IdPv3 CAS extension module provides a server-side CAS plugin for the
Shibboleth IdP that supports the CAS 2.0 protocol.

## Requirements

1. Java 1.7
2. Maven 3.x

## Building
Get the source and execute the following command from the project root
directory:

    mvn clean package

## Running the Demo
The source ships with a demonstration Web application that runs in an
embedded Jetty servlet container launched via the Maven Jetty plugin.
Launch the demo webapp by executing the following command from the project
root directory:

    mvn -Pdemo clean package

The startup process is complete when you see the following output:

    [INFO] Started Jetty Server
    [INFO] Starting scanner at interval of 10 seconds.

Open a Web browser and navigate to https://localhost:8443 and follow the
instructions.

