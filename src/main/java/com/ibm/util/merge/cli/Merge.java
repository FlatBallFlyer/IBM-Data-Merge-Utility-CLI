package com.ibm.util.merge.cli;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

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
	+ "--runners -r									\n"
	+ "		specify the number of runner threads		\n"
	+ "												\n"
	+ "--defaultParms <file>							\n"
	+ "		specify a file that contains parameters	\n"
	+ "		to be used with all merges. 				\n"
	+ "												\n"
	+ "--defaultPayload	<file>						\n"
	+ "		specify a file that contains a single	\n"
	+ "		default payload 							\n"
	+ "												\n"
	+ "--patience <minutes>							\n"
	+ "		multi-threaded time-out					\n"
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
	private long patience = 1000 * 60 * 60;
	private String templateName = "";
	private File templatesFolder;
	private Parms defaultParms = new Parms();
	private String defaultPayload = "";
	private String parmName;
	private File output = new File(".");

	/**
	 * Construct a merge engine from the arguments provided
	 * @param args String array with options 
	 * @throws Exception on processing errors
	 */
	public Merge(String[] args) throws Exception {
		Config config;
		this.templatesFolder	= new File(args[0]);
		File configFile = new File(templatesFolder.getAbsolutePath() + "/" + "config.json");
		if (configFile.exists()) {
			config = new Config(configFile);
			System.out.println("Using " + configFile.getPath());
		} else {
			config = new Config();
		}
		
		if (!templatesFolder.exists()) throw new Exception("Template Folder Missing:" + templatesFolder.getPath());
		if (!templatesFolder.isDirectory()) throw new Exception("Template Folder Missing:" + templatesFolder.getPath());
		this.cache = new Cache(config, templatesFolder);
	
		this.templateName 	= args[1];
		if (templateName.isEmpty()) throw new Exception("Template name required:" + templateName);
		if (!cache.contains(templateName)) throw new Exception("Template not in cache:" + templateName);
		
		for (int x = 2; x < args.length; x++) {
			switch (args[x]) {
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
				case "--output" :
					File outputFolder = new File(args[++x]);
					if (!outputFolder.exists()) throw new Exception("Output folder missing:" + outputFolder.getPath());
					if (!outputFolder.isDirectory()) throw new Exception("Output folder isn't a folder!" + outputFolder.getPath());
					this.output = outputFolder;
					break;
				case "--patience" :
					this.patience = 1000 * 60 * Long.valueOf(args[++x]);
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
						int payLine = 0;
						for (String line : payloadFileContents.split("\n")) {
							Request req = new Request();
							req.setPayload(line);
							req.setOutputFile("line" + Integer.toString(payLine++) + ".output");
							this.requests.add(req);
						}
						break;
					case "payloadFolder" :
						File payloadFolder = new File(args[++x]);
						if (!payloadFolder.exists()) throw new Exception("Payload Foler missing:" + payloadFolder.getPath());
						if (!payloadFolder.isDirectory()) throw new Exception("Payload Foler missing:" + payloadFolder.getPath());
						for (File file : payloadFolder.listFiles()) {
							Request req = new Request();
							req.setPayload(file);
							req.setOutputFile(file.getName() + ".output");
							this.requests.add(req);
						}
						break;
					case "parmFile" :
						File parmFile 	= new File(args[++x]);
						this.parmName 	= args[++x];
						if (this.parmName.isEmpty()) throw new Exception("Parameter Name required!");
						if (!parmFile.exists()) throw new Exception("Parameters File missing:" + parmFile.getPath());
						String parmFileContents = new String(Files.readAllBytes(parmFile.toPath()), "ISO-8859-1");
						int parmLine = 0;
						for (String line : parmFileContents.split("\n")) {
							Request req = new Request();
							req.setPayload(this.defaultPayload);
							req.setParameter(parmName, line);
							req.setOutputFile("line" + Integer.toString(parmLine++) + ".output");
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
							req.setPayload(this.defaultPayload);
							req.setParameters(file);
							req.setOutputFile(file.getPath() + ".output");
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
		System.out.println("Starting " + Integer.toString(runners) + " threads");
		// Launch Runner Threads
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int x = 0; x < runners; x++) {
			Thread t = new Thread(this);
			t.start();
			threads.add(t);
		}
		
		// Wait for threads finish - till out of patience
		long startTime = System.currentTimeMillis();
		System.out.print("Requests Remaining: " + Integer.toString(requests.size()));
		while (((System.currentTimeMillis() - startTime) < patience) && threads.size() > 0) {
			for (int i = 0 ; i < threads.size(); i++) {
				System.out.print("\rRequests Remaining: " + Integer.toString(requests.size()));
				Thread t = threads.get(i);
				t.join(100);
				if (!t.isAlive()) threads.remove(i);
			}
		}
		
		// patience is up, interrupt any remaining threads
		for (Thread t : threads) {
			t.interrupt();
			t.join();
		}
		System.out.println("\rRequests Remaining: " + Integer.toString(requests.size()));		
	}
	
	/**
	 * Process Requests from the Stack till they are all gone. 
	 */
	public void run() {
		while (requests.size() > 0) {
			Request req = requests.nextRequest();
			if (req != null) {
				try {
					Template merged = new Merger(cache, templateName, 
							req.getParameters(this.defaultParms), 
							req.getPayLoad(this.defaultPayload))
						.merge();
					OutputStream stream = new BufferedOutputStream(new FileOutputStream(this.output + "/" + req.getOutputFile()));
					merged.getMergedOutput().streamValue(stream);
					stream.close();
				} catch (Throwable t) {
					System.err.println("Execption during Merge:" + t.getMessage());
				}
			}
		}
	}
	
	public File getOutput() {
		return this.output;
	}
	
	public Requests getRequests() {
		return this.requests;
	}
}
