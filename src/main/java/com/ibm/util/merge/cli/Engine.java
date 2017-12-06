package com.ibm.util.merge.cli;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.template.Template;

public class Engine implements Runnable {
	private Gson gson = new GsonBuilder().create();
	private boolean confirm = true;
	private File mergeFolder;
	private File templatesFolder;
	private File output;
	private int runners = 1;
	private long patience = 1000 * 60 * 60;
	private String defaultPayload = "";
	private String parmName = "idmuCliParm";
	private String templateName = "";
	private Requests requests = new Requests();
	private Parms defaultParms = new Parms();
	private Cache cache;
	private Config config;

	/**
	 * Construct a merge engine from the arguments provided
	 * @param args String array with options 
	 * @throws Exception on initialization errors
	 */
	public Engine(String[] args) throws Exception {
		System.out.println("=================================================");
		System.out.println("==== Initializing Merge ");
		for (int x = 2; x < args.length; x++) {
			switch (args[x]) {
				case "--runners" :
				case "-r" :
					this.runners = Integer.valueOf(args[++x]);
					if (runners < 1 ) throw new Exception("Must have at least 1 runner");
					break;
				case "--patience" :
					this.patience = 1000 * 60 * Long.valueOf(args[++x]);
					break;
				case "--run" :
					this.confirm = false;
					break;
				case "--parameter" :
					this.parmName = args[++x];
					break;
			}
		}
		System.out.println("====                  Runners:" + Integer.toString(this.runners));
		System.out.println("====           Patience (min):" + Long.toString(this.patience / 1000 / 60));
		
		setMergeFolder(args[1]);
		setConfig();
		setCache();
		setTemplate(args[0]);
		setOutput();
		setParms();
		setPayload();
		if (loadRequests() ||
			loadPayloadFolder() ||
			loadPayloadFile() ||
			loadParmsFolder() ||
			loadParmsFile() ) {
			System.out.println("====          Requests Loaded:" + Integer.toString(this.requests.size()));
			return;
		}
		throw new Exception("Nothing to Merge!");
	}
	
	/**
	 * Implements Multi-Threaded runner process
	 * @throws InterruptedException 
	 */
	public void loadRunners() throws InterruptedException {
		System.out.println("=================================================");
		if (this.confirm) {
			System.out.print("==== Start Merge (y/n)");
			Scanner scanner = new Scanner(System.in);
			String go = scanner.next();
			scanner.close();
			if (!go.equals("y")) {
				System.out.print("==== Merge Aborted");
				System.out.println("=================================================");
				return;
			}
		}

		System.out.println("====          Starting Thread:" + Integer.toString(runners));
		Long start = System.currentTimeMillis();
		int size = this.requests.size();
		
		// Launch Runner Threads
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int x = 0; x < runners; x++) {
			Thread t = new Thread(this);
			t.start();
			threads.add(t);
		}
		
		// Wait for threads finish - till out of patience
		System.out.print("Requests Remaining: " + Integer.toString(requests.size()));
		while (((System.currentTimeMillis() - start) < patience) && threads.size() > 0) {
			for (int i = 0 ; i < threads.size(); i++) {
				System.out.print("\r====       Requests Remaining:" + Integer.toString(requests.size()) + "           ");
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
		long duration = System.currentTimeMillis() - start;
		long response = duration / (size - requests.size()); 
		System.out.println("");
		System.out.println("====       Requests Processed:" + Integer.toString(size - requests.size()) + "           ");
		System.out.println("====      Merge Duration (ms):" + Long.toString(duration));
		System.out.println("====     Average Respose (ms):" + Long.toString(response));
		System.out.println("=================================================");
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
					System.err.println("Execption during Merge:" + req.getOutputFile() + "message:" + t.getMessage());
				}
			}
		}
	}
	
	/**
	 * @return the output folder
	 */
	public File getOutput() {
		return this.output;
	}
	
	/**
	 * @return the Request Stack
	 */
	public Requests getRequests() {
		return this.requests;
	}

	/**
	 * Set the Merge folder and validate that it exists 
	 * @param folder the Folder
	 * @throws Exception on Processing Errors
	 */
	private void setMergeFolder(String folder) throws Exception {
		this.mergeFolder	= new File(folder);
		if (!mergeFolder.exists()) throw new Exception("Merge Folder Missing:" + mergeFolder.getPath());
		if (!mergeFolder.isDirectory()) throw new Exception("Merge Folder Missing:" + mergeFolder.getPath());
		System.out.println("====       Using Merge Folder:" + this.mergeFolder.getPath());
	}

	/**
	 * Load or Create the Configuration
	 * @throws Exception on file i/o errors
	 */
	private void setConfig() throws Exception {
		File configFile = new File(mergeFolder.getAbsolutePath() + "/config.json");
		if (configFile.exists()) {
			config = new Config(configFile);
			System.out.println("====      Using Configuration:" + configFile.getPath());
		} else {
			System.out.println("====      Using Configuration:Default");
			config = new Config();
		}
	}
	
	/**
	 * Setup and Load the cache
	 * @throws Exception on processing errors
	 */
	private void setCache() throws Exception {
		templatesFolder = new File(mergeFolder.getAbsolutePath() + "/templates");
		if (!templatesFolder.exists()) throw new Exception("Template Folder Missing:" + templatesFolder.getPath());
		if (!templatesFolder.isDirectory()) throw new Exception("Template Folder Missing:" + templatesFolder.getPath());
		this.cache = new Cache(config, templatesFolder);
		System.out.println("====             Cache Loaded:" + templatesFolder.getPath());
		System.out.println("====         Templates Cached:" + Integer.toString(cache.getSize()));
	}
	
	/**
	 * Set and Validate the output directory
	 * @throws Exception on processing errors
	 */
	private void setOutput() throws Exception {
		this.output = new File(mergeFolder.getAbsolutePath() + "/output");
		if (!output.exists()) throw new Exception("Output Folder Missing:" + output.getPath());
		if (!output.isDirectory()) throw new Exception("Output Folder Not a Directory:" + output.getPath());
		System.out.println("====   Using Output Directory:" + output.getPath());
	}

	/**
	 * Set and validate the base template
	 * @param template name
	 * @throws Exception on processing errors
	 */
	private void setTemplate(String template) throws Exception {
		this.templateName = template;
		if (templateName.isEmpty()) throw new Exception("Template name required:" + templateName);
		if (!cache.contains(templateName)) throw new Exception("Template not in cache:" + templateName);
		System.out.println("====      Using Base Template:" + templateName);
	}
	
	/**
	 * Set the default parameters
	 * @throws Exception on processing errors
	 */
	private void setParms() throws Exception {
		File defaultParms = new File(mergeFolder + "/parameters.json");
		if (defaultParms.exists()) {
			this.defaultParms = gson.fromJson(
					new String(Files.readAllBytes(defaultParms.toPath()), "ISO-8859-1"),
					Parms.class );
			System.out.println("==== Using Default Parameters:" + defaultParms.getPath() );
		}
	}
	
	/**
	 * Set the default Payload value
	 * @throws Exception on processing errors
	 */
	private void setPayload() throws Exception {
		File defaultPayload = new File(mergeFolder + "/payload.txt");
		if (defaultPayload.exists()) {
			this.defaultPayload  = new String(Files.readAllBytes(defaultPayload.toPath()), "ISO-8859-1");
			System.out.println("====    Using default payload:" + defaultPayload.getPath());
		}
	}

	/**
	 * Load requests from the requests.json file
	 * @return true if loaded
	 * @throws Exception on processing errors
	 */
	private boolean loadRequests() throws Exception {
		File jsonFile = new File(mergeFolder + "/requests.json");
		if (jsonFile.exists()) {
			this.requests = gson.fromJson(
	        		new String(Files.readAllBytes(jsonFile.toPath()), "ISO-8859-1"),
	        		Requests.class );
			System.out.println("====      Using Json Requests:" + jsonFile.getPath());
			return true;
		}
		return false;
	}
	
	/**
	 * Load requests from payload folder - 1 request per file
	 * @return true of loaded
	 * @throws Exception on processing errors
	 */
	private boolean loadPayloadFolder() throws Exception {
		File payloadFolder = new File(mergeFolder + "/payloadFolder");
		if (payloadFolder.exists()) {
			if (!payloadFolder.isDirectory()) throw new Exception("Payload Foler missing:" + payloadFolder.getPath());
			for (File file : payloadFolder.listFiles()) {
				if (!file.getName().startsWith(".")) {
					Request req = new Request();
					req.setPayload(file);
					req.setOutputFile(file.getName() + ".output");
					this.requests.add(req);
				}
			}
			System.out.println("====    Loaded Payload Folder:" + payloadFolder.getPath());
			return true;
		}
		return false;
	}
	
	/**
	 * Load requests from payload file - 1 request per line
	 * @return true if loaded
	 * @throws Exception
	 */
	private boolean loadPayloadFile() throws Exception {
		File payloadFile = new File(mergeFolder + "/payloadFile.txt");
		if (payloadFile.exists()) {
			String payloadFileContents = new String(Files.readAllBytes(payloadFile.toPath()), "ISO-8859-1");
			int payLine = 0;
			for (String line : payloadFileContents.split("\n")) {
				Request req = new Request();
				req.setPayload(line);
				req.setOutputFile("line" + Integer.toString(payLine++) + ".output");
				this.requests.add(req);
			}
			System.out.println("====      Loaded Payload File:" + payloadFile.getPath());
			return true;
		}
		return false;
	}
	
	/**
	 * Load requests from parmFolder - 1 request per file
	 * @return true if loaded
	 * @throws Exception on processing errors
	 */
	private boolean loadParmsFolder() throws Exception {
		File parmFolder = new File(mergeFolder + "/parametesFolder");
		if (parmFolder.exists()) {
			if (!parmFolder.isDirectory()) throw new Exception("Parameter Foler missing:" + parmFolder.getPath());
			for (File file : parmFolder.listFiles()) {
				if (!file.getName().startsWith(".")) {
					Request req = new Request();
					req.setPayload(this.defaultPayload);
					req.setParameters(file);
					req.setOutputFile(file.getPath() + ".output");
					this.requests.add(req);
				}
			}
			System.out.println("==== Loaded Parameters Folder:" + parmFolder.getPath());
			System.out.println("====          Using Parameter:" + this.parmName);
			return true;
		}
		return false;
	}
	
	/**
	 * Load requests from parmFile - 1 request per line
	 * @return true if loaded
	 * @throws Exception on processing errors
	 */
	private boolean loadParmsFile() throws Exception {
		File parmFile 	= new File(mergeFolder + "/parametersFile.txt");
		if (parmFile.exists()) {
			String parmFileContents = new String(Files.readAllBytes(parmFile.toPath()), "ISO-8859-1");
			int parmLine = 0;
			for (String line : parmFileContents.split("\n")) {
				Request req = new Request();
				req.setPayload(this.defaultPayload);
				req.setParameter(parmName, line);
				req.setOutputFile("line" + Integer.toString(parmLine++) + ".output");
				this.requests.add(req);
			}
			System.out.println("====   Loaded Parameters File:" + parmFile.getPath());
			System.out.println("====          Using Parameter:" + this.parmName);
			return true;
		}
		return false;
	}
		

}
