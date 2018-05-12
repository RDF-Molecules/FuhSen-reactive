# MINTE [![Build Status](https://travis-ci.org/LiDaKrA/FuhSen-reactive.svg?branch=master)](https://travis-ci.org/LiDaKrA/FuhSen-reactive)

### Description
MINTE is a semantic integration technique able to utilize semantics encoded in vocabularies in order to link and fuse semantically equivalent RDF entities in a single step (which we call semantic integration).

### Important Note
Although the source code is well documented, to be easly reusable, MINTE is currently under refactoring not only the source code but the documentation and tutorials as well.
We plan to finish in the following months!

### Dependencies
MINTE reactive project depends on the following software

* JDK 1.8
* Play Web Framework 2.4.6 "Damiya" and Activator 1.3.7

Download Play: https://www.playframework.com/download

Installation steps: https://www.playframework.com/documentation/2.4.x/Installing

MINTE depends on the Silk Workbench to transform the data collected from the data sources into RDF.
An instance of the workbench must be available with the configuration files containing the transformation rules.
The configuration files for the RDF transformation and all the resources needed to set up an instance of the Silk Workbench are 
provided in the project [Data Integration Workspace](https://github.com/LiDaKrA/data-integration-workspace).
MINTE collects data from social networks and other Web sources. Some of these require a key to use their API that is 
stored in conf/application.conf. The key must be provided before starting MINTE. 

#### IDE support 
The quick and easy way to start compiling, running and coding MINTE is to use "activator ui".
However, you can also set up your favorits Java IDE (Eclipse or IntellJ Idea). https://www.playframework.com/documentation/2.4.x/IDE

### Install and Build
MINTE can be installed from the source code on Github or from the Docker image in the [Lidakra repository](https://hub.docker.com/r/lidakra/)

### Install and build from the source code  
To obtain the latest version of the project please clone the github repository

    $ git clone https://github.com/RDF-Molecules/MINTE.git

The build system for MINTE is Sbt. The project can be compiled and run using Sbt or the Typesafe Activator. In order to compile the project with sbt from the project root folder run the command

    $ sbt compile

The project can be packaged in a tar file in /target/universal/ with the command

    $ sbt package universal:packageZipTarball 


Before making a build, update the version of the project in the following files:
.travis.yml, build.sbt, Dockerfile, start_fuhsen.sh

### Install from the Docker image
A Docker image containing MINTE can be built from the Docker file or pulled from the Lidakra Repository on Docker Hub.
Once the image has been downloaded or created the configuration file in conf/application.conf must be changed in order to provide
the keys for the data sources used by MINTE and also to update the url of the Silk Workbench.
The config file must be provided in a Docker data volume loaded with the config file. As an example copy the config file in 
a folder in the server host (e.g. /home/lidakra/application.conf) then run a container using an image
already available or a small one like alpine (a small Linux version) mapping the config file in the host with the keys to the config file in the container

    $ docker run -it -v /home/lidakra/application.conf:/home/lidakra/fuhsen-1.1.0/conf/application.conf:ro \
                                         --name fuhsen-conf alpine /bin/sh

From within the volume container check that the config file is present. Detach from the volume container with Ctrl-p Ctrl-q.
Start a container with MINTE using the config file in the data volume

    $ docker run -it -p 9000:9000 --volumes-from fuhsen-conf --name fuhsen lidakra/fuhsen:v1.1.0

From within the MINTE container check in the /conf folder that the config file is present and up to date and then start MINTE

    #./bin/fuhsen 


### Run
MINTE can be started using Sbt or the Typesafe activator.

#### Run with Sbt
From the project root folder run the command

    $ sbt start

The MINTE server will listen on port 9000.

#### Run with Typesafe Activator 
From the project root folder execute the command "activator ui". The application is going to be compiled and launched 
at the following address: http://localhost:9000. Once the UI is launched in the browser go to the Run tab and select "Run app".

#### OCCRP SSL Certificate installation
1. Find JAVA_HOME. Its can be found with: readlink -f /usr/bin/java | sed "s:bin/java::"
2. Copy ./certs/data.occrp.org.cer to JAVA_HOME/jre/lib/security
3. Go to JAVA_HOME/jre/lib/security and import the certificate into the cacerts keystore

    $ keytool -importcert -alias occrp -keystore cacerts -storepass changeit -file data.occrp.org.cer -noprompt

(The keystore cacerts default password is: changeit)

##### In case the certificate expires or gets deprecated, it can be deleted from the keystore ('cacerts' in this example) as follows:

1- First confirm the certificate is in the 'cacerts' keystore: ('occrp' is the alias given when added)

keytool -list -keystore cacerts | grep occrp

2- To delete the certificate from the keystore (cacerts):

keytool -delete -alias occrp -keystore cacerts

3- For confirmation repeat step 1, nothing should be listed as result.

4- The new certificate can be added with the same (or different) alias. (Follow steps 1 to 3 from 'OCCRP SSL Certificate installation')

#### License

* Copyright (C) 2015-2018 EIS University of Bonn & Fraunhofer IAIS
* Licensed under the Apache 2.0 License
