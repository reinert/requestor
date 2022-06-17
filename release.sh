#!/bin/bash
# usage: bash release.sh 0.1.0 0.2.0-SNAPSHOT

if [ -n "$1" ] && [ -n "$2" ]; then
  # update version to release version
  mvn versions:set -DnewVersion=$1
  mvn versions:commit
  # update tag to release version
  bash ./tag.sh $1
  # commit
  git add .
  git commit -m "Release $1"
  # deploy
  # errors are occurring during deployment; it's necessary to try many times
  mvn clean deploy -P!project,release
  # tag
  git tag -a requestor-$1 -m "Requestor v$1"
  git push origin requestor-$1
  # site
  mvn install -P!project,examples
  mvn site -P!project,site # manually commit target/site in gh-pages branch
  # update version to next snapshot
  mvn versions:set -DnewVersion=$2
  mvn versions:commit
  # update tag to next snapshot
  bash ./tag.sh $2
  # commit
  git add .
  git commit -m "Start $2 development"
  # deploy snapshot
  mvn clean deploy -P!project
  # push
  git push origin master
elif [ "snapshot" == "$1" ] || [ "current" == "$1" ]; then
  mvn clean install -P!project,examples
  mvn site -P!project,site # manually commit target/site in gh-pages branch
  mvn clean deploy -P!project
else
  echo "USAGE"
  echo "1)  Release version: sh release.sh {release-version} {next-snapshot}"
  echo "            Example: sh release.sh 0.1.0 0.2.0-SNAPSHOT"
  echo "2) Release snapshot: sh release.sh snapshot"
  echo "        Alternative: sh release.sh current"
fi
