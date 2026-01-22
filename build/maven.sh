#!/bin/sh

apt-get update
apt-get install -y docker.io

DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
mkdir -p $DOCKER_CONFIG/cli-plugins
curl -SL https://github.com/docker/compose/releases/download/v2.39.4/docker-compose-linux-x86_64 -o $DOCKER_CONFIG/cli-plugins/docker-compose
chmod +x $DOCKER_CONFIG/cli-plugins/docker-compose

# Parent directory of parent directory of this script
SRC_FOLDER=$(dirname "$(dirname "$(readlink -f "$0")")")

cd $SRC_FOLDER
mvn clean install -Pdocker-test
