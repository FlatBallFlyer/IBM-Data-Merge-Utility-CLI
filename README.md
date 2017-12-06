# IBM Data Merge Utility v4.0.0 - CLI

## What Is IDMU-CLI
The IBM Data Merge Utility CLI exposes the [IBM Data Merge Utility](http://flatballflyer.github.io/IBM-Data-Merge-Utility/) as a Command Line utility

---

## Start Here

```
git clone https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-CLI.git
mvn install
```

## Command Line Usage

```
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
export CLASSPATH=$(< cp.txt):$(pwd)/target/idmu-cli-4.0.0-SNAPSHOT.jar

java com.ibm.util.merge.cli.Merge templateName mergeFolder <options>

ex: java com.ibm.util.merge.cli.Merge root.. . --run --runners 20
see src/test/resources/perfTest.sh for some samples
```
__templateName__
- The short name of the base template to use for merges 		

__mergeFolder__
- A folder with merge input. The contents of the merge folder should be:
  - templates: A directory that contains template group.json files 
  - output: A directory where all output will be created
  - config.json: Optional configuration file
  - parameters.json: Optional default parameters
  - payload.txt: Optional default payload
- And One of the following, if more than one of these exists the first is used.
  - requests.json - all requests in a single json file
  - payloadFolder - contains request payloads (1 per file) uses default parameters, output file name == input file name 
  - payloadFile.txt - contains request payloads (1 per line) uses default parameters, output file name == lineXX.output
  - parametersFolder - contains request parameters (1 per file) uses default payload, output file name == input file name
  - parametersFile.txt - contains request parameters (1 per file) uses default parameters, output file name == line##.output

__options__									
- __- -runners  -r__ &lt;number&gt;
Specify the number of runner threads		
- __- -run__ 
Start processing without a confirmation prompt		
- __- -patience__ &lt;minutes&gt;
Specify a timeout for multi-threaded runners
- __- -parameter__ &lt;name&gt;
Specify a parameter name for use with parametersFolder / parametersFile. 
The default parameter used is "idmuCliParm" if none is provided.
												
---

## JSON File Formats
The parameters objects are serialized to HashMap<String,String[]>

```
{
	"parm1": ["value","value], 
	"parm2 : ["value"]...
}
```

The requests json file is based on
```
{"requests":[
	{	"parms":{parameters}, 
		"payload":"aPayload", 
		"output":"fileName"
	},
	{....
	}
]}
```
 	
### See Also
1. [IDMU Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility)

---