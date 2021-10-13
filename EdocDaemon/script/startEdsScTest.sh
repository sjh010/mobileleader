export EDS_HOME=/app/create
cd $EDS_HOME
echo 'Current Path = ' $PWD
. ./Config/EdocServer.env
#java -d64 -Dproject=EdsTest com.mobileleader.edoc.test.convTest 
#java -d64 -Dproject=EdsTest com.mobileleader.edoc.test.GenEdocGrpIdxNo 100 &
#java -d64 -Dproject=EdsTest com.mobileleader.edoc.test.AESCryptoTest
nohup java -d64 -Dproject=EdsTest com.mobileleader.edoc.test.JmeterMain $1 $2 & 
#java -d64 -Dproject=EdsTest com.mobileleader.edoc.test.EaiTest & 
