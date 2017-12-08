mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
export CLASSPATH=$(< cp.txt):$(pwd)/target/idmu-cli-4.0.0-SNAPSHOT.jar

