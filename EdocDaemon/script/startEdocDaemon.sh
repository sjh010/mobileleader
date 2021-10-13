export EDS_HOME=/programs/app/edocDaemon
cd $EDS_HOME
echo 'Current Path = ' $PWD
. ./Config/EdocServer.env
#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=edocDaemon" |awk {'print $2'})


if [[ $pid = "" ]]; then
	echo "start edocDaemon"
        nohup java -d64 -Dproject=edocDaemon com.mobileleader.edoc.bg.daemon.EdsBgDaemon 1> /dev/null 2> error.info &
	#nohup java -d64 -Dproject=edocDaemon com.mobileleader.edoc.bg.daemon.EdsBgDaemon &
        waitingTime=0
	while [ $waitingTime -ne 10 ]
		do
			if [ $(ps -ef |grep -v grep | grep "Dproject=edocDaemon" | wc -l) -eq 1 ]
			then
				echo "edocDaemon is start!! Process ID=$(ps -ef |grep -v grep | grep "Dproject=edocDaemon" |awk {'print $2'})"
				exit 0;
			else
				sleep 1
				echo "waiting $waitingTime seconds..."
				waitingTime=`expr $waitingTime + 1`
			fi
		done
	exit -1;
else
	echo "already edocDaemon running"
	exit 0;
fi


