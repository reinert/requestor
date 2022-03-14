#!/bin/bash

rm -rf $1
cp -r ../requestor/target/site $1/
#mkdir $1/examples
#cp -r ../requestor/examples/requestor-showcase/src/main/webapp $1/examples/showcase/
#cp -r ../requestor/examples/requestor-showcase/target/requestor-showcase-$1/showcase $1/examples/showcase
#cp -r ../requestor/examples/requestor-showcase/target/requestor-showcase-$1/WEB-INF $1/examples/showcase
git add .
git commit -m "Building site for Requestor $1"

