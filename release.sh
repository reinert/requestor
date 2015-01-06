#!/bin/bash
# usage: sh release.sh 0.1.0 0.2.0-SNAPSHOT
mvn clean deploy
mvn install -P!project,examples
mvn site-deploy -P!project,site
mvn versions:set -DnewVersion=$1
mvn versions:commit
sh ./tag.sh $1
git add .
git commit -m "Release $1"
mvn clean install
git tag -a requestor-$1 -m "Requestor v$1"
git push origin requestor-$1
mvn deploy -P!project,release
mvn install -P!project,examples
mvn site-deploy -P!project,site
mvn site-deploy -P!project,site -DsitePath=latest
mvn versions:set -DnewVersion=$2
mvn versions:commit
sh ./tag.sh HEAD
git add .
git commit -m "Start $2 development"
