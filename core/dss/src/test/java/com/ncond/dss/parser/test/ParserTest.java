package com.ncond.dss.parser.test;

import junit.framework.TestCase;

import com.ncond.dss.parser.Parser;

public class ParserTest extends TestCase {

	private String cmdString;
	private Parser parser;
	private String param;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		parser = Parser.getAuxInstance();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetNextParam() {

	}

	public void testParseAsBusName() {
		cmdString = "BusName.1.2.3";
		parser.setCmdString(cmdString);
		param = parser.getNextParam();

		int[] nodes = new int[3];
//		String name = parser.parseAsBusName(nodes.length, nodes);

//		System.out.println("BusName: " + param + name + nodes.toString());
	}

	public void testParseAsVector() {
//		fail("Not yet implemented");
	}

	public void testParseAsMatrix() {
//		fail("Not yet implemented");
	}

	public void testParseAsSymMatrix() {
//		fail("Not yet implemented");
	}

	public void testMakeString() {
//		fail("Not yet implemented");
	}

	public void testMakeInteger() {
//		fail("Not yet implemented");
	}

	public void testMakeDouble() {
//		fail("Not yet implemented");
	}

	public void testGetRemainder() {
//		fail("Not yet implemented");
	}

}
