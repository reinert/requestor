#!/bin/bash
# usage: sh release.sh 0.1.0 0.2.0-SNAPSHOT

if [ -n "$1" ] && [ -n "$2" ]; then
  mvn clean deploy -P!project
  mvn install -P!project,examples
  mvn site-deploy -P!project,site
  mvn versions:set -DnewVersion=$1
  mvn versions:commit
  sh ./tag.sh $1
  git add .
  git commit -m "Release $1"
  mvn clean install -Prelease
  git tag -a requestor-$1 -m "Requestor v$1"
  git push origin requestor-$1
  mvn deploy:deploy -P!project
  mvn install -P!project,examples
  mvn site-deploy -P!project,site
  mvn site-deploy -P!project,site -DsitePath=latest
  mvn versions:set -DnewVersion=$2
  mvn versions:commit
  sh ./tag.sh HEAD
  git add .
  git commit -m "Start $2 development"
  git push origin master
elif [ "snapshot" == "$1" ] || [ "current" == "$1" ]; then
  sh ./generate-site.sh
  mvn deploy:deploy -P!project
else
  echo "USAGE"
  echo "1)  Release version: sh release.sh {release-version} {next-snapshot}"
  echo "            Example: sh release.sh 0.1.0 0.2.0-SNAPSHOT"
  echo "2) Release snapshot: sh release.sh snapshot"
  echo "        Alternative: sh release.sh current"
fi
