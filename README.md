# IBM Data Merge Utility v4.0.0 - CLI

Overview

## What Is IDMU-CLI
The IBM Data Merge Utility CLI exposes the IBM Data Merge Utility v4.0.0 as a Command Line utility

---

## Start Here

```
&gt; git clone https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility.git
&gt; mvn install
```

## Command Line Usage

```
Merge templateFolder baseTemplate &lt;options&gt; requests &lt;requestOption&gt;
```
__templateFolder__ contains template gropus.json
 
__baseTempalte__ is the template to merge 		

### Options are									
__--config -c__
: load a configuration 					
												
__--runners -r__									
: specify the number of runner threads		

__--defaultParms__								
: Specify a file that contains parameters to be used with all merges.
NOTE: This value is ignored if using the request json option.
The parms files hould take the following json format

```
{"parm1":["value","value], "parm2 : ["value"]...}
``` 				

__--defaultPayload__								
: Specify a file that contains a single default payload
NOTE: This value is ignored if using the request json option. 							
												
###RequestOption are

__json &lt;file&gt;__							
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
		
__payloadFile &lt;file&gt;__					
: builds requests based on payload file with 1 request per line			
NOTE: ignores default payload, uses default parms
							
__payloadFolder &lt;folder&gt;__				
: builds requests based on payload folder with 1 request per file			
NOTE: ignores default payload, uses default parms
							
__parmFile &lt;file&gt; &lt;parm&gt;__ 				
: builds requests based on parameters file with 1 request per line.
The parm name is used to add the parameter to the default parms.
Uses default payload for all requests.			
							
__parmFolder &lt;file&gt; &lt;parm&gt;__				
: builds requests based on parameters folder with 1 request per file.
The parm name is used to add the parameter to the default parms for each merge.
Uses default payload for all requests.			

### See Also
1. [IDMU Project](https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility)

---