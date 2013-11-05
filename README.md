# CAS Protocol Extension for Shibboleth 3.0 IdP

<https://github.com/serac/shibboleth-idp-ext-cas>

The Shibboleth IdPv3 exention module provides basic support for the CAS
2.0 protocol.

## Requirements

1. Java 1.7
2. Maven 3.x

## Building
Get the source and execute the following command from the root directory:

    mvn clean install

## Running
The source ships with a demonstration Web application that runs at the
following URL

https://localhost:8443

The demo webapp runs in an embedded Jetty servlet container launched via the
maven-jetty-plugin Maven plugin.

Launch the demo webapp by entering the idp-cas-webapp directory and
executing the following command:

mvn jetty:run

The startup process is complete when you see the following output:

    [INFO] Started Jetty Server
    [INFO] Starting scanner at interval of 10 seconds.

Open a Web browser and navigate to https://localhost:8443 and follow the
instructions.

