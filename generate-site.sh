#!/bin/bash

mvn clean install -P!project,examples
mvn site -P!project,site # manually commit target/site in gh-pages branch