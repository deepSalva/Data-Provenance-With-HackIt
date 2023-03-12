#!/usr/bin/env bash

function getFromTemplate(){
    if [ -z "${IP}" ]; then
        IP=$(hostname --ip-address)
    fi
    ORIGIN=$1
    DESTINY=$2
    sed -e "s/__IP__/${IP}/g" ${ORIGIN} > ${DESTINY}
}

if [ -z "${IP}" ]; then
    IP=$(hostname --ip-address)
fi

if [ -z "${BASEDIR}" ]; then
    BASEDIR="/disk/com.qcri.hackit"
fi

if [ -z "${FOLDER_CODE}" ]; then
    FOLDER_CODE="code"
fi

if [ -z "${FOLDER_CONF}" ]; then
    FOLDER_CONF="conf"
fi

if [ -z "${FOLDER_LIBS}" ]; then
    FOLDER_LIBS="libs"
fi

if [ -z "${FLAG_AKKA}" ]; then
    FLAG_AKKA="true"
fi

if [ -z "${FLAG_LOG}" ]; then
    FLAG_LOG="true"
fi

if [ -z "${NAME_CONF_LOG}" ]; then
    NAME_CONF_LOG="log4j.properties"
fi

if [ -z "${FLAG_RHEEM}" ]; then
    FLAG_RHEEM="true"
fi

if [ -z "${NAME_CONF_RHEEM}" ]; then
    NAME_CONF_RHEEM="rheem.properties"
fi

if [ -z "${FLAG_FLINK}" ]; then
    FLAG_FLINK="false"
fi

if [ -z "${NAME_CONF_FLINK}" ]; then
    NAME_CONF_FLINK="flink.properties"
fi

if [ -z "${FLAG_SPARK}" ]; then
    FLAG_SPARK="false"
fi

if [ -z "${NAME_CONF_SPARK}" ]; then
    NAME_CONF_SPARK="spark.properties"
fi

if [ -z "${OTHER_FLAGS}" ]; then
    OTHER_FLAGS="-Xms4g -Xmx8g -XX:+UseParallelGC -XX:+UseParallelOldGC -XX:ParallelGCThreads=4"
fi
. $(pwd)/../base/set_variables.sh

FOLDER_LOGS=$(pwd)/../logs/${NAME}/

if [ ! -d "${FOLDER_LOGS}" ]; then
    mkdir -p ${FOLDER_LOGS}
fi

. $(pwd)/../base/enviroment.sh
