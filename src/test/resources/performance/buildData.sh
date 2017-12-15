echo Now Generating a tar with XML and JSON requests in it 
java Merge test.. ./generatePerfData/ --run 

echo Extract the json testing data
cd perf/jsonSource
tar -xvf ../../out.tar *.json > /dev/null 2> /dev/null
cd ../..

echo Extract the xml testing data
cd perf/xmlSource
tar -xvf ../../out.tar *.xml > /dev/null 2> /dev/null
cd ../..


