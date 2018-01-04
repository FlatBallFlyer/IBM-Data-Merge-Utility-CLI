# IBM Data Merge Utility v4.0.1 - CLI

## What Is IDMU-CLI
The IBM Data Merge Utility CLI exposes the [IBM Data Merge Utility](http://flatballflyer.github.io/IBM-Data-Merge-Utility/) through a Command Line Interface. The IDMU-CLI provides a scalable multi-threaded merge engine that is suited to batch style processing. However, the current implementation of the JDBC Data Provider does not utilize a connection pool and may not perform as well as the JNDI data provider available in the Rest interface. If you are looking to use the CLI tool, please see the [release notes](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-CLI/releases) section of the GitHub pages for this project for instructions.

---
## Related Pages
1. [IDMU Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility) - Core Merge tool
1. [IDMU Users Guide](https://flatballflyer.github.io/IBM-Data-Merge-Utility/USERS_GUIDE)
1. [IDMU-CLI Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-CLI) - Command Line merge tool

---
## Command Line usage

```
java Merge <templateName> <mergeFolder> <options>

example:
java Merge test.. ./testMerge --runners 20 --run

```

__templateName__
* The short name of the base template to use for merges 		

__mergeFolder__
* A folder with merge input. The contents of the merge folder should be:
  * templates: A directory that contains template group.json files 
  * output: A directory where all output will be created
  * config.json: Optional configuration file
  * parameters.json: Optional default parameters
  * payload.txt: Optional default payload
  * And One of the following, if more than one of these exists the first is used.
    * requests.json - all requests in a single json file
    * payloadFolder - contains request payloads (1 per file) uses default parameters, output file name == input file name.output 
    * payloadFile.txt - contains request payloads (1 per line) uses default parameters, output file name == lineXX.output
    * parametersFolder - contains request parameters json (1 per file) uses default payload, output file name == input file name.output
    * parametersFile.txt - contains request parameters (1 per line) uses default parameters, output file name == line##.output

__options__									
  * __--runners  -r__ &lt;number&gt; 
Specify the number of runner threads 
  * __--run__ 
Start processing without a confirmation prompt 	
  * __--patience__ &lt;minutes&gt; 
Specify a timeout for multi-threaded runners 
  * __--parameter__ &lt;name&gt; 
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
[
	{	"parms":{parameters}, 
		"payload":"aPayload", 
		"output":"fileName"
	},
	{....
	}
]
```
## For Contributors 
### Requirements
1. Java 1.8 
1. maven package manager - see (https://maven.apache.org/install.html)

### Start Here

```
git clone https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility-CLI.git
mvn install
```

### Usage

```
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
export CLASSPATH=$(< cp.txt):$(pwd)/target/idmu-cli-4.0.1.jar

java Merge templateName mergeFolder <options>

ex: Merge root.. . --run --runners 20
see src/test/resources/perfTest.sh for some samples
```

### See Also
1. [IDMU Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility)
1. [Template Json Schema](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/WebContent/jsonSchema/schema.template.json)
1. [Config Json Schema](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/WebContent/jsonSchema/schema.config.json)
1. [Sample Config Json](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/src/test/resources/config.sample.json)
1. [Sample Template Json](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility/blob/master/src/test/resources/system.sample.json)

---