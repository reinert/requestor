#!/bin/sh

cp Dockerfile-template Dockerfile

UID=`id -u`
PWD=`pwd`
sed -i "s:<USER>:$USER:g" Dockerfile
sed -i "s:<USER_ID>:$UID:g" Dockerfile
sed -i "s:<WORK_DIR>:$PWD:g" Dockerfile
