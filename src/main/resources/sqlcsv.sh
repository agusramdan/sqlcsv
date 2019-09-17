#!/bin/sh
#
#
#
# ./sqlcsv.sh -j "jdbc:oracle:thin:@localhost:1522:xa" -u oracle -p oracle -quote -qf query.sql
# ./sqlcsv.sh -driver "com.mysql.jdbc.Driver" -j "jdbc:mysql://localhost:3306/portal" -u root -nopassword -quote -qf query.sql
# ./sqlcsv.sh -driver "com.mysql.cj.jdbc.Driver" -j "jdbc:mysql://localhost:3306/portal" -u root -nopassword -quote -qf query.sql

PARAM=""

while [ $# -ne 0 ]; do
    case $1 in
        -driver )         shift
                           PARAM="$PARAM -driver $1"
                           ;;
        -j | --jdbc )       shift
                           PARAM="$PARAM -j $1"
                           ;;
        -u | --username )  shift
                           PARAM="$PARAM -u $1"
                           ;;
        -p | --password )  shift
                           PARAM="$PARAM -p $1"
                           ;;  
        -i | --input )     shift
                           PARAM="$PARAM -i $1"
                           ;;
        -o | --output )    shift
                           PARAM="$PARAM -o $1"
                           ;;
        -d | --delimiter )  shift
                           PARAM="$PARAM -d $1"
                           ;;
        -q | --query )     shift
                           PARAM="$PARAM -q $1"
                           ;;
        -qf | --qfile )    shift
                           PARAM="$PARAM -qf $1"
                           ;;
        * )                PARAM="$PARAM -$1"
                           ;;
                
    esac
    shift
done

echo $PARAM

HOME_SQLCSV=~/sqlcsv
CURRENTDATE=`date +"%Y%m%d_%H%M%s"`
GC_LOG_FILE="$HOME_SQLCSV/log/gc-sqlcsv-${CURRENTDATE}.log"
GC_LOG_OPTION="-verbosegc -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -Xloggc:$GC_LOG_FILE"
VM_OPTION="-server $GC_LOG_OPTION -XX:+UseParallelGC -XX:+UseParallelOldGC -Xms256m -XX:MaxNewSize=1g -XX:NewRatio=1 -XX:SurvivorRatio=6 -XX:ParallelGCThreads=32 -XX:MaxGCPauseMillis=100 "

java $VM_OPTION -jar $HOME_SQLCSV/sqlcsv.jar $PARAM
