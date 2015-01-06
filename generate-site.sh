if ["$1" == "latest"]; then
  mvn clean install -P!project,examples
  mvn site-deploy -P!project,site -DsitePath=latest
else
  mvn clean install -P!project,examples
  mvn site-deploy -P!project,site
fi