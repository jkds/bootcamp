spray-routing-add42
===================

Small restful service that adds 42 to a specified number

This project uses sbt to build an artefact which can then be run from the command line.
sbt targets:

clean 
assembly

Once the assembly plugin has created the runnable jar to execute the app use the following:

    java -Xmx512m -Xms512m -jar http-adder-service.jar bindHost={host} bindPort={port}

The bindHost and bindPort parameters are optional. If not supplied then these will default
to localhost and 8080 respectively.

To test the service, open a browser and attempt to retrieve the following url:

    http://{host}:{port}/add42/123

The response body should be 145
