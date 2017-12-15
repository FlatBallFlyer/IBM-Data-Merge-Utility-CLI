echo "Process the xml->json transformations"
rm -rf ./perf/payloadFolder
mv ./perf/xmlSource ./perf/payloadFolder
rm -rf ./perf/output
mkdir ./perf/output
java Merge test.bToA. ./perf/ --run --runners 20 

rm -rf ./perf/xmlSource
mv ./perf/payloadFolder ./perf/xmlSource
rm -rf ./perf/jsonGenerated
mv ./perf/output ./perf/jsonGenerated

