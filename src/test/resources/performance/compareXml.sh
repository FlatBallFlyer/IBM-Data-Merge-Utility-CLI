echo compair generated Xml with source Xml
for f in ./perf/xmlGenerated/*; do 
  echo -e -n "\r $f   ";
  diff "$f" "`echo $f | sed s/.json.output/.xml/ | sed s/xmlGenerated/xmlSource/`"; 
done
echo
