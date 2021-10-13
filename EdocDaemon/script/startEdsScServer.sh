export EDS_HOME=/app/create
cd $EDS_HOME
echo 'Current Path = ' $PWD
. ./Config/EdocServer.env
#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=EdocServer" |awk {'print $2'})


if [[ $pid = "" ]]; then
	echo "start EdocServer"
        nohup java -d64 -Dproject=EdocServer com.mobileleader.edoc.sc.server.EdocServer 1> /dev/null 2> error.info &
	#java -d64 -Dproject=EdocServer com.mobileleader.edoc.sc.server.EdocServer &
        waitingTime=0
	while [ $waitingTime -ne 10 ]
		do
			if [ $(ps -ef |grep -v grep | grep "Dproject=EdocServer" | wc -l) -eq 1 ]
			then
				echo "EdocServer is start!! Process ID=$(ps -ef |grep -v grep | grep "Dproject=EdocServer" |awk {'print $2'})"
				exit 0;
			else
				sleep 1
				echo "waiting $waitingTime seconds..."
				waitingTime=`expr $waitingTime + 1`
			fi
		done
	exit -1;
else
	echo "already EdocServer running"
	exit 0;
fi


