# usage: sh release.sh 0.1.0 0.2.0-SNAPSHOT
mvn clean deploy
mvn install -P!project,examples
mvn site-deploy -P!project,site
mvn versions:set -DnewVersion=$1
mvn versions:commit
mvn scm:tag
git add .
git commit -m "Release $1"
mvn clean install
mvn deploy -P!project,release
mvn install -P!project,examples
mvn site-deploy -P!project,site
mvn site-deploy -P!project,site -DsitePath=latest
mvn versions:set -DnewVersion=$2
mvn versions:commit
mvn -Dtag="HEAD" scm:tag
git add .
git commit -m "Start $2 development"
