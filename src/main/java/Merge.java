

import com.ibm.util.merge.cli.Engine;
import com.ibm.util.merge.exception.MergeException;

public class Merge {
	public static void main(String[] args) {
		if (args.length < 2) {
			usage();
			return;
		}
		
		try {
			Engine merge = new Engine(args);
			merge.loadRunners();
		} catch (MergeException me) {
			System.err.println("Merge Execption Occured! " + me.getMessage());
		} catch (Throwable te) {
			System.err.println("Throwable Execption Occured! " + te.getMessage());
		}
	}

	public static void usage() {
		System.out.print("Usage: \n"
	+ "Merge baseTemplate mergeFolder <options>	\n"
	+ "Where:										\n"
	+ "  baseTempalte is the main template to merge	\n"
	+ "  mergeFolder contains:						\n"
	+ "    - templates: A directory with template group files\n" 
	+ "    - output: The output directory\n"
	+ "    - config.json: Optional configuration file\n"
	+ "    - parameters.json: Optional default parameters\n"
	+ "    - payload.txt: Optional default payload\n"
	+ " \n"
	+ "And One of the following, \n"
	+ "if more than one of these exists the first is used.\n"
	+ "    - requests.json - all requests in a single json file\n"
	+ "    - payloadFolder - contains request payloads (1 per file)\n"
	+ "      will use the default parameters, and  \n"
	+ "      the input file name is used for output\n" 
	+ "    - payloadFile.txt - contains request payloads (1 per line)\n"
	+ "      will use the default parameters, and \n"
	+ "      the output file name will be lineXX.output\n"
	+ "    - parametersFolder - contains request parameters (1 per file)\n"
	+ "      will use the default payload, and\n"
	+ "      the input file name is used for output\n"
	+ "    - parametersFile.txt - contains request parameters (1 per file)\n"
	+ "      will use the default parameters, and \n"
	+ "      the output file name will be line##.output	 \n"
	+ " \n"
	+ "Options are									\n"
	+ "--runners -r	<number>							\n"
	+ "		specify the number of runner threads		\n"
	+ "												\n"
	+ "--run              							\n"
	+ "		start merge without confirmation			\n"
	+ "												\n"
	+ "--patience <minutes>							\n"
	+ "		multi-threaded time-out					\n"
	+ "												\n"
	+ "--parameter <name> \n"
	+ "		Specify a parameter name for use with parametersFolder / parametersFile.\n" 
	+ "		The default parameter used is idmuCliParm if none is provided.\n"
	+ "\n"
	+ "Example\n"
	+ "java Merge my.test. /bigJob --runners 20 \n"
	+ "\n"
	+ "See https://flatballflyer.github.io/IBM-Data-Merge-Utility-CLI/ for more information\n"
	);
	}
}
