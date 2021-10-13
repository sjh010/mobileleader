!/bin/sh
export EDS_HOME=/app/create
cd $EDS_HOME
echo 'Current Path = ' $PWD
. ./Config/EdocServer.env
#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=EdsGenXml" |awk {'print $2'})


if [[ $pid = "" ]]; then
	echo "start EdsGenXml"
        nohup java -d64 -Dproject=EdsGenXml com.mobileleader.edoc.batch.BatchGenVerXml &
	#nohup java -d64 -Dproject=EDocBgDaemon com.mobileleader.edoc.bg.daemon.EdsBgDaemon &
else
	echo "already EdsGenXml running"
	exit 0;
fi


