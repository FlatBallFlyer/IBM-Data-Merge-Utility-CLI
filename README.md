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

__--defaultPayload__								
: Specify a file that contains a single default payload

NOTE: This value is ignored if using the request json option. 							
												
### RequestOption is one of:

__json &lt;file&gt;__							
: provide all requests in a single json file
		
__payloadFile &lt;file&gt;__					
: builds requests based on payload file with 1 request per line, output files will be line#.output
			
NOTE: ignores default payload, uses default parms
							
__payloadFolder &lt;folder&gt;__				
: builds requests based on payload folder with 1 request per file, output files will be inputFile.output
			
NOTE: ignores default payload, uses default parms
							
__parmFile &lt;file&gt; &lt;parm&gt;__ 				
: builds requests based on parameters file with 1 request per line, output files will be line#.output

The parm name is used to add the parameter to the default parms, and the default payload is used for all requests.			
							
__parmFolder &lt;file&gt; &lt;parm&gt;__				
: builds requests based on parameters folder with 1 request per file, output files will be inputFile.output

The parm name is used to add the parameter to the default parms for each merge, and the default payload is used for all requests.			

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