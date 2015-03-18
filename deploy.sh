#!/bin/sh

cd cal
mvn clean install
cd ../cal-utils
mvn clean install
cd ../smaug
mvn clean install
cd ../smaug-solr-nxtp
mvn clean install
cd ../apis/api-services
mvn clean install
cd ../../activities
mvn clean install
cd ../advertising
mvn clean install
cd ../descriptions
mvn clean install
cd ../comments
mvn clean install
cd ../properties
mvn clean install
cd ../events
mvn clean install
cd ../geo
mvn clean install -Dmaven.test.skip=true
cd ../media
mvn clean install
cd ../messages
mvn clean install
cd ../tags
mvn clean install
cd ../users
mvn clean install
cd ../statistics
mvn clean install
cd ../web-tg
mvn clean package -Dmaven.test.skip=true

