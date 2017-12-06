package com.ibm.util.merge.cli;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MergeUnitTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMergePayloadFolder() throws Exception {
		String[] argc = {
				"test.aToB.", 
				"src/test/resources/jsonToXml" 
		};
		Engine merge = new Engine(argc);
		Requests reqs = merge.getRequests();
		assertEquals(20, reqs.size());
		Request req = reqs.get(0);
		assertTrue(req.getOutputFile().endsWith(".txt.output"));
	}

}
