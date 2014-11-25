#!/bin/bash

export JAVA_HOME=/opt/jdk1.8.0

function package {
    echo "build package"
    mvn clean
    mvn package -DskipTests
    tar czfv bootstrap.tar.gz -C target/ bootstrap-node-0.0.1-SNAPSHOT.jar lib/
}

package


