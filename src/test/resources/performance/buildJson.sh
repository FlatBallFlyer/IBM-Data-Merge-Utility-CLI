echo "Process the json->xml transformations"
rm -rf ./perf/payloadFolder
mv ./perf/jsonSource ./perf/payloadFolder
rm -rf ./perf/output
mkdir ./perf/output
java com.ibm.util.merge.cli.Merge test.aToB. ./perf/ --run --runners 20 

rm -rf ./perf/jsonSource
mv ./perf/payloadFolder ./perf/jsonSource
rm -rf ./perf/xmlGenerated
mv ./perf/output ./perf/xmlGenerated

