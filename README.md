# IBM Data Merge Utility v4.0.0 - CLI

Overview

## What Is IDMU-CLI
The IBM Data Merge Utility CLI exposes the IBM Data Merge Utility v4.0.0 as a Command Line utility

---

## Start Here

```
> git clone https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility.git
> mvn install
```

## Command Line Usage

```
Merge templateFolder baseTemplate <options> requests <requestOption>
```
templateFolder contains template gropus.json 
baseTempalte is the template to merge 		
												
###Options are									
--config -c
: load a configuration 					
												
--runners	-r									
: specify the number of runner threads		

--defaultParms								
: Specify a file that contains parameters to be used with all merges.
NOTE: This value is ignored if using the request json option.
The parms files hould take the following json format

```
{"parm":["value","value], "parm2 : ["value"]...}
``` 				

--defaultPayload								
: Specify a file that contains a single default payload
NOTE: This value is ignored if using the request json option. 							
												
###RequestOption are

json <file>							
: provide all requests in a single json file formatted as:

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
		
payloadFile <file>					
: builds requests based on payload file with 1 request per line			
NOTE: ignores default payload, uses default parms
							
payloadFolder <folder>				
: builds requests based on payload folder with 1 request per file			
NOTE: ignores default payload, uses default parms
							
parmFile <file> <parm> 				
: builds requests based on parameters file with 1 request per line.
The parm name is used to add the parameter to the default parms.
Uses default payload for all requests.			
							
parmFolder <file> <parm>				
: builds requests based on parameters folder with 1 request per file.
The parm name is used to add the parameter to the default parms for each merge.
Uses default payload for all requests.			

### See Also
1. [IDMU Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility)

---