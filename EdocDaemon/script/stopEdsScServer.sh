#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=EdocServer" |awk {'print $2'})

if [[ $pid = "" ]]
    then
	echo "there is no process [EdocServer]"
	exit;
else
	kill -9 $pid
	echo "EdocServer : kill -9 $pid"
	while [ $(ps -ef |grep -v grep | grep Dproject=EdocServer | wc -l) -ne 0 ]
		do
			sleep 1
		done
	echo "stopped EdocServer"
	exit 0;
fi
