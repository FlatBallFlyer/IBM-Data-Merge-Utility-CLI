echo Cleanup from previous run
./cleanup.sh
./buildRequests.sh
./buildData.sh
./buildXml.sh
./buildJson.sh
./compareXml.sh
./compareJson.sh

