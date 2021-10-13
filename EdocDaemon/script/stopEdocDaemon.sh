#!/bin/sh

#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=edocDaemon" |awk {'print $2'})

if [[ $pid = "" ]]
    then
	echo "there is no process [edocDaemon]"
	exit;
else
	kill -9 $pid
	echo "edocDaemon : kill -9 $pid"
	while [ $(ps -ef |grep -v grep | grep Dproject=edocDaemon | wc -l) -ne 0 ]
		do
			sleep 1
		done
	echo "stopped edocDaemon"
	exit 0;
fi



