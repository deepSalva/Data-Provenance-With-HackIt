#!/usr/bin/env bash

mvn clean package

rm -rf ./libs ./code
mkdir libs code

cp ./target/libs/* ./libs/
cp -r ./target/com.qcri.hackit-1.0-SNAPSHOT.jar ./code/