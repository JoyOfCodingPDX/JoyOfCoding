#! /bin/bash

#set -x

# This shell script runs a program that acts as an RMI server.
# Mainly, it sets all of the nasty system properties on the java
# command line

function usage() {
  echo ""
  echo "** $1"
  echo ""
  echo "usage: startServer.sh mainClassName programArgs"
  echo ""

  exit 1
}


if [ $# -le 0 ]; then
  usage "Missing main class name"
fi

#DEBUG=-Djava.security.debug=access,failure

HOST=`hostname`
JAR_DIR=/home/davidw/PSU/jars
POLICY_FILE=`pwd`/rmi-server.policy

# Execute a big, ugly java command line

java -Djava.rmi.server.codebase=file:${JAR_DIR}/familyTree.jar \
  -Djava.rmi.server.hostname=${HOST} \
  -Djava.security.policy=${POLICY_FILE} \
  ${DEBUG} \
  -classpath ${JAR_DIR}/familyTree.jar $*