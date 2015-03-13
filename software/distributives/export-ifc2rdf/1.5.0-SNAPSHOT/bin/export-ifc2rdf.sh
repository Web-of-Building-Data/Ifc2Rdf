#! /bin/sh

#
# Created by: Nam Vu Hoang, nam.vuhoang@aalto.fi
# Creation date: 22.10.2013
#

MAIN_CLASS=fi.hut.cs.drumbeat.ifc.convert.ifc2rdf.cli.Main

if [ "$1" ]
then
   bash bin/java-run.sh $MAIN_CLASS $@
else
  bash bin/java-run.sh $MAIN_CLASS -?
fi