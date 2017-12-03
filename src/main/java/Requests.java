package com.ibm.util.merge.cli;

import java.util.ArrayList;

public class Requests extends ArrayList<Request>{
	private static final long serialVersionUID = 1L;

	public Requests() {
	}

	public synchronized Request nextRequest() {
		if (this.size() > 0) {
			return this.remove(0);
		}
		return null;
	}
	
}