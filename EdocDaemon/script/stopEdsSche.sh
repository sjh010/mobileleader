#!/bin/sh

#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=EdsSche" |awk {'print $2'})

if [[ $pid = "" ]]
then
    echo "there is no process [EdsSche]"
    exit;
else
    echo "EdsSche : kill -9 $pid"
    kill -9 $pid
    exit;
fi
