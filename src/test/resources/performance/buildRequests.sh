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
let "r = $T * 50"
let "t = $r / 2000"
echo Generating $r XML and $r JSON requests - eta $t seconds to tar file
