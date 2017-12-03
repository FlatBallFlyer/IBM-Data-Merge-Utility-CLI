package com.ibm.util.merge.cli;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public class Merge implements Runnable {
	public static void main(String[] args) {
		try {
			Merge merge = new Merge(args);
			merge.loadRunners();
		} catch (MergeException me) {
			System.err.println("Merge Execption Occured! " + me.getMessage());
			usage();
		} catch (Throwable te) {
			System.err.println("Throwable Execption Occured! " + te.getMessage());
			usage();
		}
	}

	public static void usage() {
		System.out.print("Usage: \n"
	+ "Merge templateFolder baseTemplate <options> requests <requestOption>	\n"
	+ "Where:										\n"
	+ "	templateFolder contains template gropus.json \n"
	+ "	baseTempalte is the template to merge 		\n"
	+ "												\n"
	+ "Options are									\n"
	+ "--config -c									\n"
	+ "		load a configuration 					\n"
	+ "												\n"
	+ "--runners	-r									\n"
	+ "		specify the number of runner threads		\n"
	+ "												\n"
	+ "--defaultParms								\n"
	+ "		specify a file that contains parameters	\n"
	+ "		to be used with all merges. 				\n"
	+ "												\n"
	+ "--defaultPayload								\n"
	+ "		specify a file that contains a single	\n"
	+ "		default payload 							\n"
	+ "												\n"
	+ "RequestOption are								\n"
	+ "			json <file>							\n"
	+ "				All requests in a single json	\n"
	+ "				ignores defaults					\n"
	+ "												\n"
	+ "			payloadFile <file>					\n"
	+ "				Request payloads 1/line			\n"
	+ "				ignores defaultPayload			\n"
	+ "												\n"
	+ "			payloadFolder <folder>				\n"
	+ "				Request payloads 1/file			\n"
	+ "				ignores defaultPayload			\n"
	+ "												\n"
	+ "			parmFile <file> <parm> 				\n"
	+ "				Request parameters 1/line 		\n"
	+ "				added to parms as <parm>			\n"
	+ "												\n"
	+ "			parmFolder <file> <parm>				\n"
	+ "				Request parameters 1/file 		\n"
	+ "				added to parms as <parm>			\n"
	);
	}
	
	/////////////////////////////////////////////////////
	// Merge Object 
	/////////////////////////////////////////////////////
	private Requests requests = new Requests();
	private Cache cache;
	private Gson gson = new GsonBuilder().create();
	private int runners = 1;
	private long patience = 1000000000;
	private String templateName = "";
	private File templatesFolder;
	private Parms defaultParms = new Parms();
	private String defaultPayload = "";
	private String parmName;

	/**
	 * Construct a merge engine from the arguments provided
	 * @param args String array with options 
	 * @throws Exception on processing errors
	 */
	public Merge(String[] args) throws Exception {
		this.templatesFolder	= new File(args[0]);
		if (!templatesFolder.exists()) throw new Exception("Template Folder Missing:" + templatesFolder.getPath());
		if (!templatesFolder.isDirectory()) throw new Exception("Template Folder Missing:" + templatesFolder.getPath());
		this.cache = new Cache(templatesFolder);
		
		this.templateName 	= args[1];
		if (templateName.isEmpty()) throw new Exception("Template name required:" + templateName);
		if (!cache.contains(templateName)) throw new Exception("Template not in cache:" + templateName);
		
		for (int x = 2; x < args.length; x++) {
			switch (args[x]) {
				case "--config" : 
				case "-c" : 
					File configFile = new File(args[++x]);
					if (!configFile.exists()) new Exception("Config file missing:" + configFile.getPath());
					Config.load(configFile);
					break;
				case "--runners" :
				case "-r" :
					this.runners = Integer.valueOf(args[++x]);
					if (runners < 1 ) throw new Exception("Must have at least 1 runner");
					break;
				case "--defaultParms" : 
					File defaultParms = new File(args[++x]);
					if (!defaultParms.exists()) throw new Exception("Default Parms missing:" + defaultParms.getPath());
					this.defaultParms = gson.fromJson(
			        		new String(Files.readAllBytes(defaultParms.toPath()), "ISO-8859-1"),
			        		Parms.class );
					break;
				case "--defaultPayload" :
					File defaultPayload = new File(args[++x]);
					if (!defaultPayload.exists()) throw new Exception("Default Payload missing:" + defaultPayload.getPath());
					this.defaultPayload  = new String(Files.readAllBytes(defaultPayload.toPath()), "ISO-8859-1");
					break;
				case "requests" :
					switch (args[++x]) {
					case "json" :
						File jsonFile = new File(args[++x]);
						if (!jsonFile.exists()) throw new Exception("Json Requests missing:" + jsonFile.getPath());
						this.requests = gson.fromJson(
				        		new String(Files.readAllBytes(jsonFile.toPath()), "ISO-8859-1"),
				        		Requests.class );
						break;
					case "payloadFile" :
						File payloadFile = new File(args[++x]);
						if (!payloadFile.exists()) throw new Exception("Payload File missing:" + payloadFile.getPath());
						String payloadFileContents = new String(Files.readAllBytes(payloadFile.toPath()), "ISO-8859-1");
						for (String line : payloadFileContents.split("\n")) {
							Request req = new Request();
							req.parameters = this.defaultParms;
							req.payload = line;
							this.requests.add(req);
						}
						break;
					case "payloadFoler" :
						File payloadFolder = new File(args[++x]);
						if (!payloadFolder.exists()) throw new Exception("Payload Foler missing:" + payloadFolder.getPath());
						if (!payloadFolder.isDirectory()) throw new Exception("Payload Foler missing:" + payloadFolder.getPath());
						for (File file : payloadFolder.listFiles()) {
							Request req = new Request();
							req.parameters = this.defaultParms;
							req.payload = new String(Files.readAllBytes(file.toPath()), "ISO-8859-1");
							this.requests.add(req);
						}
						break;
					case "parmFile" :
						File parmFile 	= new File(args[++x]);
						this.parmName 	= args[++x];
						if (this.parmName.isEmpty()) throw new Exception("Parameter Name required!");
						if (!parmFile.exists()) throw new Exception("Parameters File missing:" + parmFile.getPath());
						String parmFileContents = new String(Files.readAllBytes(parmFile.toPath()), "ISO-8859-1");
						for (String line : parmFileContents.split("\n")) {
							String[] value = {line};
							Request req = new Request();
							req.payload = this.defaultPayload;
							Parms newParms = new Parms();
							newParms.put(parmName, value);
							for (String name : this.defaultParms.keySet()) {
								newParms.putIfAbsent(name, this.defaultParms.get(name));
							}
							this.requests.add(req);
						}
						break;
					case "parmFoler" :
						File parmFolder = new File(args[++x]);
						this.parmName 	= args[++x];
						if (this.parmName.isEmpty()) throw new Exception("Parameter Name required!");
						if (!parmFolder.exists()) throw new Exception("Parameter Foler missing:" + parmFolder.getPath());
						if (!parmFolder.isDirectory()) throw new Exception("Parameter Foler missing:" + parmFolder.getPath());
						for (File file : parmFolder.listFiles()) {
							Request req = new Request();
							req.payload = this.defaultPayload;
							Parms newParms = gson.fromJson(
					        		new String(Files.readAllBytes(file.toPath()), "ISO-8859-1"),
					        		Parms.class );
							for (String name : this.defaultParms.keySet()) {
								newParms.putIfAbsent(name, this.defaultParms.get(name));
							}
							req.parameters = newParms;
							this.requests.add(req);
						}
						break;
					}
					break;
			}
		}
		if (this.requests.isEmpty()) throw new Exception("No Requests To Process!");
	}

	/**
	 * Implements Multi-Threaded runner process
	 * @throws InterruptedException 
	 */
	public void loadRunners() throws InterruptedException {
		// Launch Runner Threads
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int x = 0; x < runners; x++) {
			Thread t = new Thread(this);
			t.start();
			threads.add(t);
		}
		
		// Wait for threads finish - till out of patience
		long startTime = System.currentTimeMillis();
		while (((System.currentTimeMillis() - startTime) < patience) && threads.size() > 0) {
			for (int i = 0 ; i < threads.size(); i++) {
				Thread t = threads.get(i);
				t.join(1000);
				if (!t.isAlive()) threads.remove(i);
			}
		}
		
		// patience is up, interrupt any remaining threads
		for (Thread t : threads) {
			t.interrupt();
			t.join();
		}
		
	}
	
	/**
	 * Process Requests from the Stack till they are all gone. 
	 */
	public void run() {
		while (requests.size() > 0) {
			Request req = requests.nextRequest();
			if (req != null) {
				try {
					Template merged = new Merger(cache, templateName, req.parameters, req.payload).merge();
					merged.getMergedOutput().streamValue(new BufferedOutputStream(new FileOutputStream(req.outputFile)));
				} catch (Throwable t) {
					System.err.println("Execption during Merge:" + t.getMessage());
				}
			}
		}
	}

	private class Parms extends HashMap<String, String[]> {
		private static final long serialVersionUID = 1L;
	}
}
