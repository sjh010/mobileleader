!/bin/sh
export EDS_HOME=/app/create
cd $EDS_HOME
echo 'Current Path = ' $PWD
. ./Config/EdocServer.env
#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=EdsDel" |awk {'print $2'})


if [[ $pid = "" ]]; then
	echo "start EdsDel"
        nohup java -d64 -Dproject=EdsDel com.mobileleader.edoc.batch.BatchDeleteEdocFiles &
	#nohup java -d64 -Dproject=EDocBgDaemon com.mobileleader.edoc.bg.daemon.EdsBgDaemon &
else
	echo "already EdsDel running"
	exit 0;
fi


