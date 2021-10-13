#!/bin/sh

. ./Config/EdocServer.env

#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=EdsSche" |awk {'print $2'})
echo "Process ID=$pid"

if [[ $pid = "" ]]
    then
	echo "start EdsSche"
        #nohup java -d64 -Xmx16 -Dproject=EdsSche com.mobileleader.edoc.batch.BatchCronScheduler 1> /dev/null 2> error.info &
java -d64 -Dproject=EdsSche com.mobileleader.edoc.batch.BatchCronScheduler &
else
    echo "already EdsSche running"
fi
