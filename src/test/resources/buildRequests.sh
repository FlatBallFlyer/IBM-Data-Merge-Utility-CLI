echo "How many request batches (50 XML and 50 JSON Requests per Batch)"
read T;
echo -n "{\"idmuArchiveName\":[\"out\"],\"batches\":[" > ./generatePerfData/parameters.json
counter=1
while [ $counter -lt $T ]
do
 echo -n "\"$counter\"," >> ./generatePerfData/parameters.json
((counter++))
done
echo -n "\"end\"]}" >> ./generatePerfData/parameters.json

