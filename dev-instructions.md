# Dev Instructions

## First time only

1. sh setup-dev.sh
2. docker image build -t requestor-jdk7 .
3. docker container run -d -t -v /usr/reinert/requestor/:/usr/reinert/requestor --name requestor-dev requestor-jdk7

## Whenever you need to run or access the dev container

4. docker container start requestor-dev
5. docker exec -it requestor-dev bash
