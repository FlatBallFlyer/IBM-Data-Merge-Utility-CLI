mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
export CLASSPATH=$(< cp.txt):$(pwd)/target/idmu-cli-4.0.2-SNAPSHOT.jar

