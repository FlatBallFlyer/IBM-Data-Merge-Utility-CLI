package com.ibm.util.merge.cli;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.util.merge.cli.Parms;

public class Request {
	private Gson gson = new GsonBuilder().create();
	private HashMap<String, String[]> parameters = new HashMap<String,String[]>();
	private File parametersFile;
	private String payload = "";
	private File payLoadFile;
	private String outputFile = "";

	public String getPayLoad() throws UnsupportedEncodingException, IOException {
		if (null != this.payLoadFile) {
			this.payload = new String(Files.readAllBytes(this.payLoadFile.toPath()), "ISO-8859-1");
		}
		return this.payload;
	}
	
	public HashMap<String, String[]> getParameters(HashMap<String, String[]> defaults) throws UnsupportedEncodingException, IOException {
		if (null == this.parameters) {
			this.parameters = new HashMap<String,String[]>();
		}
		
		if (null != this.parametersFile) {
			String stringParms = new String(Files.readAllBytes(this.parametersFile.toPath()), "ISO-8859-1");
			Parms fileParms = gson.fromJson(stringParms, Parms.class);
			for (String name : fileParms.keySet()) {
				this.parameters.putIfAbsent(name, defaults.get(name));
			}
		}
		
		// add defaults
		for (String name : defaults.keySet()) {
			this.parameters.putIfAbsent(name, defaults.get(name));
		}
		
		return this.parameters;
	}
	
	public String getOutputFile() {
		return this.outputFile;
	}
	
	public void setParameter(String name, String value) {
		String[] val = {value};
		this.parameters.put(name, val);
	}
	
	public void setParameters(File value) {
		this.parametersFile = value;
	}
	
	public void setPayload(String value) {
		this.payload = value;
	}

	public void setPayload(File value) {
		this.payLoadFile = value;
	}
	
	public void setOutputFile(String file) {
		this.outputFile = file;
	}
}
