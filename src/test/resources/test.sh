java com.ibm.util.merge.cli.Merge test.batch. ./jsonFromMySql/ --run 
cd perf/payloadFolder/
tar -xvf ../../out.tar > /dev/null 2> /dev/null
cd ../..
java com.ibm.util.merge.cli.Merge test.aToB. ./perf/ --run --runners 20

