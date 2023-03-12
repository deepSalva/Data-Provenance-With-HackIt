#!/usr/bin/env bash

SIZE_FILE=$1
EXEC=$2
OPTION=$3

NAME="grep"
CLASS="org.qcri.Main"
EXP="grep"
. ./../base/base.sh

OUTPUT_FILE=hdfs://${IP}:8300/outputTest
INPUT_FILE=hdfs://${IP}:8300/data/long_abstract_clean/${SIZE_FILE}

. ./../base/execute.sh \
      "${BASEDIR}" \
      "${IP}" \
      "${INPUT_FILE}" \
      "${OUTPUT_FILE}" \
      "hakespeare" \
      "${OPTION}" \
        |& tee ${FOLDER_LOGS}/debug_${SIZE_FILE}_${OPTION}_${EXEC}_${EXP}.log
