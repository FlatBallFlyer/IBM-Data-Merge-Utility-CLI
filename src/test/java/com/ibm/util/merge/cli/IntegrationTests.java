package com.ibm.util.merge.cli;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class IntegrationTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCloudantMergeJson() throws Exception {
		String[] argc = {
				"src/test/resources/test1", 
				"test..", 
				"--output", 
				"src/test/resources/test1/output", 
				"requests", 
				"json", 
				"src/test/resources/test1/requests.json"};
		Merge merge = new Merge(argc);
		Requests reqs = merge.getRequests();
		assertEquals(10, reqs.size());
		Request req = reqs.get(0);
		assertEquals("1.output", req.getOutputFile());
		assertEquals("Foo", req.getPayLoad("Foo"));
		assertEquals("1", req.getParameters(new HashMap<String,String[]>()).get("idcustomer")[0]);
		req = reqs.get(1);
		assertEquals("2.output", req.getOutputFile());
		assertEquals("Foo", req.getPayLoad("Foo"));
		assertEquals("2", req.getParameters(new HashMap<String,String[]>()).get("idcustomer")[0]);
	}

	@Test
	public void testCloudantMergeParmFile() throws Exception {
		String[] argc = {
				"src/test/resources/test1", 
				"test..", 
				"--defaultPayload", 
				"src/test/resources/test1/payload.txt", 
				"requests", 
				"parmFile", 
				"src/test/resources/test1/parmfile.txt",
				"idcustomer"
		};
		Merge merge = new Merge(argc);
		Requests reqs = merge.getRequests();
		assertEquals(10, reqs.size());
		Request req = reqs.get(0);
		assertEquals("line0.output", req.getOutputFile());
		assertEquals("ID,FROM,TO\n5,corpZip,99353\n1,corpUrl,www.spacely.com\n2,corpStreet,101 Future Ave.\n4,corpState,IS\n3,corpCity,Space City", req.getPayLoad("Foo"));
		assertEquals("1", req.getParameters(new HashMap<String,String[]>()).get("idcustomer")[0]);
		req = reqs.get(1);
		assertEquals("line1.output", req.getOutputFile());
		assertEquals("ID,FROM,TO\n5,corpZip,99353\n1,corpUrl,www.spacely.com\n2,corpStreet,101 Future Ave.\n4,corpState,IS\n3,corpCity,Space City", req.getPayLoad("Foo"));
		assertEquals("2", req.getParameters(new HashMap<String,String[]>()).get("idcustomer")[0]);
		req = reqs.get(2);
		assertEquals("line2.output", req.getOutputFile());
		assertEquals("ID,FROM,TO\n5,corpZip,99353\n1,corpUrl,www.spacely.com\n2,corpStreet,101 Future Ave.\n4,corpState,IS\n3,corpCity,Space City", req.getPayLoad("Foo"));
		assertEquals("3", req.getParameters(new HashMap<String,String[]>()).get("idcustomer")[0]);
		req = reqs.get(9);
		assertEquals("line9.output", req.getOutputFile());
		assertEquals("ID,FROM,TO\n5,corpZip,99353\n1,corpUrl,www.spacely.com\n2,corpStreet,101 Future Ave.\n4,corpState,IS\n3,corpCity,Space City", req.getPayLoad("Foo"));
		assertEquals("10", req.getParameters(new HashMap<String,String[]>()).get("idcustomer")[0]);
	}

	@Test
	public void testMergeParmFolder() throws Exception {
		String[] argc = {
				"src/test/resources/test1", 
				"test..", 
				"--defaultPayload", 
				"src/test/resources/test1/payload.txt", 
				"requests", 
				"json", 
				"src/test/resources/test1/requests.json"};
		Merge merge = new Merge(argc);
		Requests reqs = merge.getRequests();
		assertEquals(10, reqs.size());
		Request req = reqs.get(0);
		assertEquals("1.output", req.getOutputFile());
		assertEquals("Foo", req.getPayLoad("Foo"));
		assertEquals("1", req.getParameters(new HashMap<String,String[]>()).get("idcustomer")[0]);
		req = reqs.get(1);
		assertEquals("2.output", req.getOutputFile());
		assertEquals("Foo", req.getPayLoad("Foo"));
		assertEquals("2", req.getParameters(new HashMap<String,String[]>()).get("idcustomer")[0]);
	}

	@Test
	public void testRun1() throws Exception {
		String[] argc = {
				"src/test/resources/test1", 
				"test..", 
				"--defaultPayload", 
				"src/test/resources/test1/payload.txt", 
				"--output", 
				"src/test/resources/test1/output", 
				"requests", 
				"parmFile", 
				"src/test/resources/test1/parmfile.txt",
				"idcustomer"
		};
		Merge merge = new Merge(argc);
		merge.loadRunners();
	}

	@Test
	public void testRun2() throws Exception {
		String[] argc = {
				"src/test/resources/test2", 
				"test.aToB.", 
				"--output", 
				"src/test/resources/test2/output", 
				"requests", 
				"payloadFolder", 
				"src/test/resources/test2/a"
		};
		Merge merge = new Merge(argc);
		merge.loadRunners();
	}

	@Test
	public void testRun3() throws Exception {
		String[] argc = {
				"src/test/resources/test2", 
				"test.bToA.", 
				"--runners", 
				"5", 
				"--output", 
				"src/test/resources/test2/output", 
				"requests", 
				"payloadFolder", 
				"src/test/resources/test2/b"
		};
		Merge merge = new Merge(argc);
		merge.loadRunners();
	}

}
