package com.epri.dss.parser.impl;

import com.epri.dss.parser.Parser;
import com.epri.dss.parser.RPNCalc;

public class ParserImpl implements Parser {

	private static final char CommentChar = '!';

	public class ParserProblem extends Exception {

	}

	private String CmdBuffer;
    private int Position;
    private String ParameterBuffer;
    private String TokenBuffer;
    private String DelimChars;
    private String WhiteSpaceChars;
    private String BeginQuoteChars, EndQuoteChars;
    private char LastDelimiter;
    private char MatrixRowTerminator;
    private boolean AutoIncrement;
    private boolean ConvertError;
    private boolean IsQuotedString;
    private RPNCalc RPNCalculator;

	public ParserImpl() {
		this.DelimChars          = ",=";
		this.WhiteSpaceChars     = " " + "\t";   // blank + tab
		this.BeginQuoteChars     = "(\"'[{";
		this.EndQuoteChars       = ")\"']}";
		this.Position            = 0;
		this.MatrixRowTerminator = '|';
		this.AutoIncrement       = false;
		this.RPNCalculator       = new RPNCalcImpl();
    }

	public String getDelimChars() {
		return DelimChars;
	}

	public void setDelimChars(String delimChars) {
		DelimChars = delimChars;
	}

	public String getWhiteSpaceChars() {
		return WhiteSpaceChars;
	}

	public void setWhiteSpaceChars(String whiteSpaceChars) {
		WhiteSpaceChars = whiteSpaceChars;
	}

	public String getBeginQuoteChars() {
		return BeginQuoteChars;
	}

	public void setBeginQuoteChars(String beginQuoteChars) {
		BeginQuoteChars = beginQuoteChars;
	}

	public String getEndQuoteChars() {
		return EndQuoteChars;
	}

	public void setEndQuoteChars(String endQuoteChars) {
		EndQuoteChars = endQuoteChars;
	}

	public boolean isAutoIncrement() {
		return AutoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		AutoIncrement = autoIncrement;
	}

	public String getRemainder() {
		return null;
	}

	public void setCmdString(String Value) {

	}

	public String getCmdString() {
		return CmdBuffer;
	}

	public String makeString() {
		return null;
	}

	public int makeInteger() {
		return 0;
	}

	public double makeDouble() {
		return 0.0;
	}

	public String getNextParam() {
		return null;
	}

	private void skipWhiteSpace(String LineBuffer, int LinePos) {

	}

	private boolean isWhiteSpace(char ch) {
		return false;
	}

	private boolean isDelimiter(String LineBuffer, int LinePos) {
		return false;
	}

	private boolean isDelimChar(char ch) {
		return false;
	}

	private boolean isCommentChar(String LineBuffer, int LinePos) {
		return false;
	}

	private String getToken(String LineBuffer, int LinePos) {
		return null;
	}

	private double interpretRPNString(int Code) {
		return 0.0;
	}

	public String getToken() {
		return TokenBuffer;
	}

	public void setToken(String Value) {
		this.TokenBuffer = Value;
	}

	public String parseAsBusName(int NumNodes, int[] NodeArray) {
		return null;
	}

	public int parseAsVector(int ExpectedSize, double[] VectorBuffer) {
		return 0;
	}

	public int parseAsMatrix(int ExpectedOrder, double[] MatrixBuffer) {
		return 0;
	}

	public int parseAsSymMatrix(int ExpectedOrder, double[] MatrixBuffer) {
		return 0;
	}

	/* resets delimiters to default */
	public void resetDelims() {

	}
}