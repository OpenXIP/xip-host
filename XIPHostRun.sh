#! /usr/bin/env bash
BASEDIR=$(dirname $0)
echo $BASEDIR
ant run -buildfile $BASEDIR/build.xml
read -p "Press any key to continue..."