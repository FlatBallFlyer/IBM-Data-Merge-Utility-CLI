echo Cleanup from previous run
./cleanup.sh

echo Use data from MySql to generate a tar with 20k requests in it - typically taks 6-7 seconds
java com.ibm.util.merge.cli.Merge test.batch. ./jsonFromMySql/ --run 

echo Extract the testing data
cd perf/payloadFolder/
tar -xvf ../../out.tar > /dev/null 2> /dev/null
cd ../..

echo "Process the json->xml transformations - typically takes < 5 seconds."
java com.ibm.util.merge.cli.Merge test.aToB. ./perf/ --run --runners 20

