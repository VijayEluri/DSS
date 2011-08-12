package com.epri.dss.parser.impl;

import org.apache.commons.lang.mutable.MutableInt;

import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.parser.RPNCalc;

public class Parser {

	private static final char CommentChar = '!';

	private String CmdBuffer;
	private int Position;
	private String ParameterBuffer;
	private String TokenBuffer;
	private String DelimChars;
	private String WhiteSpaceChars;
	private String BeginQuoteChars;
	private String EndQuoteChars;
	private char LastDelimiter;
	private char MatrixRowTerminator;
	private boolean AutoIncrement;
	private boolean ConvertError;
	private boolean IsQuotedString;
	private RPNCalc RPNCalculator;

	private Parser() {
		super();

		this.DelimChars          = ",=";
		this.WhiteSpaceChars     = " " + "\t";  // blank + tab
		this.BeginQuoteChars     = "(\"'[{";
		this.EndQuoteChars       = ")\"']}";
		this.Position            = 0;
		this.MatrixRowTerminator = '|';
		this.AutoIncrement       = false;
		this.RPNCalculator       = new RPNCalcImpl();
	}

	private static class ParserHolder {
		public static final Parser INSTANCE = new Parser();
	}

	private static class AuxParserHolder {
		public static final Parser INSTANCE = new Parser();
	}

	public static Parser getInstance() {
		return ParserHolder.INSTANCE;
	}

	public static Parser getAuxInstance() {
		return AuxParserHolder.INSTANCE;
	}

	private int processRPNCommand(String TokenBuffer, RPNCalc RPN) throws ParserProblem {
		double Number = 0;
		int Result = 0;  // error code on conversion error

		/* First try to make a valid number. If that fails, check for RPN command */
		try {
			Number = Double.valueOf(TokenBuffer);
			Result = 1;
		} catch (NumberFormatException e) {
			Result = 0;
		}

		if (Result == 0) {
			RPN.setX(Number);  // enters number in X register
		} else {  /* Check for RPN command. */
			Result = 0; // reset error return
			String S = TokenBuffer.toLowerCase();

			if (S.equalsIgnoreCase("+")) {
				RPN.add();
			} else if (S.equalsIgnoreCase("-")) {
				RPN.subtract();
			} else if (S.equalsIgnoreCase("*")) {
				RPN.multiply();
			} else if (S.equalsIgnoreCase("/")) {
				RPN.divide();
			} else if (S.equalsIgnoreCase("sqrt")) {
				RPN.sqrt();
			} else if (S.equalsIgnoreCase("sqr")) {
				RPN.square();
			} else if (S.equalsIgnoreCase("^")) {
				RPN.yToTheXPower();
			} else if (S.equalsIgnoreCase("sin")) {
				RPN.sinDeg();
			} else if (S.equalsIgnoreCase("cos")) {
				RPN.cosDeg();
			} else if (S.equalsIgnoreCase("tan")) {
				RPN.tanDeg();
			} else if (S.equalsIgnoreCase("asin")) {
				RPN.aSinDeg();
			} else if (S.equalsIgnoreCase("acos")) {
				RPN.aCosDeg();
			} else if (S.equalsIgnoreCase("atan")) {
				RPN.aTanDeg();
			} else if (S.equalsIgnoreCase("atan2")) {
				RPN.aTan2Deg();
			} else if (S.equalsIgnoreCase("swap")) {
				RPN.swapXY();
			} else if (S.equalsIgnoreCase("rollup")) {
				RPN.rollUp();
			} else if (S.equalsIgnoreCase("rolldn")) {
				RPN.rollDn();
			} else if (S.equalsIgnoreCase("ln")) {
				RPN.natLog();
			} else if (S.equalsIgnoreCase("pi")) {
				RPN.enterPi();
			} else if (S.equalsIgnoreCase("log10")) {
				RPN.tenLog();
			} else if (S.equalsIgnoreCase("exp")) {
				RPN.eToTheX();
			} else if (S.equalsIgnoreCase("inv")) {
				RPN.inv();
			} else {
				Result = 1;  // error
				throw new ParserProblem("Invalid inline math entry: \""+TokenBuffer+"\"");
			}
		}
		return Result;
	}

	public void setCmdString(final String Value) {
		MutableInt mPosition;

		CmdBuffer = Value + " ";  // add some white space at end to get last param

		mPosition = new MutableInt(0);
		skipWhiteSpace(CmdBuffer, mPosition);  // position at first non whitespace character
		Position = mPosition.intValue();  // passed by reference
	}

	/**
	 * Resets delimiters to default.
	 */
	public void resetDelims() {
		DelimChars          = ",=";
		WhiteSpaceChars     = " " + "\t";  // blank + tab
		MatrixRowTerminator = '|';
		BeginQuoteChars     = "(\"'[{";
		EndQuoteChars       = ")\"']}";
	}

	private boolean isWhiteSpace(char ch) {
		for (int i = 0; i < WhiteSpaceChars.length(); i++)
			if (ch == WhiteSpaceChars.charAt(i))
				return true;
		return false;
	}

	private boolean isDelimiter(final String LineBuffer, MutableInt LinePos) {
		int i;
		char ch;

		boolean Result = false;

		if (isCommentChar(LineBuffer, LinePos)) {
			Result = true;
			LastDelimiter = CommentChar;
			return Result;
		}

		ch = LineBuffer.charAt(LinePos.intValue());

		for (i = 0; i < DelimChars.length(); i++) {
			if (ch == DelimChars.charAt(i)) {
				Result = true;
				LastDelimiter = ch;
				return Result;
			}
		}

		for (i = 0; i < WhiteSpaceChars.length(); i++) {
			if (ch == WhiteSpaceChars.charAt(i)) {
				Result = true;
				LastDelimiter = ' ';  // to indicate stopped on white space
				return Result;
			}
		}
		return Result;
	}

	private boolean isDelimChar(char ch) {
		for (int i = 0; i < DelimChars.length(); i++)
			if (ch == DelimChars.charAt(i))
				return true;
		return false;
	}

	private void skipWhiteSpace(final String LineBuffer, MutableInt LinePos) {
		while ((LinePos.intValue() < LineBuffer.length() - 1) && isWhiteSpace(LineBuffer.charAt(LinePos.intValue())))
			LinePos.increment();
	}


	private int _TokenStart;
	private int _CmdBufLength;
	private int _QuoteIndex;  // value of quote character found
	private String _LineBuffer;
	private int _LinePos;
	private String _Result;

	private void _parseToEndChar(char EndChar) {
		_LinePos += 1;
		_TokenStart = _LinePos;
		while ((_LinePos < _CmdBufLength - 1) && (_LineBuffer.charAt(_LinePos) != EndChar))
			_LinePos += 1;

		_Result = _LineBuffer.substring(_TokenStart, _LinePos);
		if (_LinePos < _CmdBufLength)
			_LinePos += 1;  // increment past endchar
	}

	private void _parseToEndQuote() {
		_parseToEndChar(EndQuoteChars.charAt(_QuoteIndex));
		IsQuotedString = true;
	}

	private boolean _isBeginQuote(char ch) {
		_QuoteIndex = BeginQuoteChars.indexOf(ch);
		return (_QuoteIndex >= 0);
	}

	private String getToken(final String LineBuffer, MutableInt linePos) {
		MutableInt mLinePos;
		_LineBuffer = LineBuffer;
		_LinePos = linePos.intValue();

		_Result = "";  // if it doesn't find anything, return null string

		_CmdBufLength = _LineBuffer.length();
		if (_LinePos < _CmdBufLength) {

			/* Handle quotes and parentheses around tokens */
			IsQuotedString = false;
			if (_isBeginQuote(_LineBuffer.charAt(_LinePos))) {
				_parseToEndQuote();
			} else {  /* Copy to next delimiter or whitespace */
				_TokenStart = _LinePos;
				mLinePos = new MutableInt(_LinePos);
				while ((mLinePos.intValue() < _CmdBufLength) && (!isDelimiter(_LineBuffer, mLinePos)))
					mLinePos.increment();
				_LinePos = mLinePos.intValue();

				_Result = _LineBuffer.substring(_TokenStart, _LinePos);
			}

			/* Check for stop on comment */

			// if stop on comment, ignore rest of line.
			if (LastDelimiter == CommentChar) {
				_LinePos = _LineBuffer.length() + 1;
			} else {

				/* Get rid of trailing white space */
				if (LastDelimiter == ' ') {
					mLinePos = new MutableInt(_LinePos);
					skipWhiteSpace(_LineBuffer, mLinePos);
					_LinePos = mLinePos.intValue();
				}
				if (isDelimChar(_LineBuffer.charAt(_LinePos))) {
					LastDelimiter = _LineBuffer.charAt(_LinePos);
					_LinePos += 1;  // move past terminating delimiter
				}
				mLinePos = new MutableInt(_LinePos);
				skipWhiteSpace(_LineBuffer, mLinePos);
				_LinePos = mLinePos.intValue();
			}
		}

		linePos.setValue(_LinePos);

		return _Result;
	}

	public String getNextParam() {
		MutableInt mPosition;

		if (Position < CmdBuffer.length()) {
			mPosition = new MutableInt(Position);  // pass by reference
			LastDelimiter = ' ';
			// get entire token and put in token buffer
			TokenBuffer = getToken(CmdBuffer, mPosition);
			if (LastDelimiter == '=') {
				ParameterBuffer = TokenBuffer;
				TokenBuffer = getToken(CmdBuffer, mPosition);
			} else {
				// init to null string
				ParameterBuffer = "";
			}
			Position = mPosition.intValue();
		} else {
			ParameterBuffer = "";
			TokenBuffer = "";
		}
		return ParameterBuffer;
	}

	/**
	 * Looking for "busName.1.2.3" in the TokenBuffer.
	 * Assumes nodeArray is big enough to hold the numbers.
	 */
	public String parseAsBusName(MutableInt NumNodes, int[] NodeArray) {
		int DotPos;
		String NodeBuffer, DelimSave, TokenSave, Result;
		MutableInt NodeBufferPos;

		if (AutoIncrement)
			getNextParam();

		NumNodes.setValue(0);
		DotPos = TokenBuffer.indexOf('.');
		if (DotPos == -1) {
			return TokenBuffer;
		} else {
			Result = TokenBuffer.substring(0, DotPos).trim();  // bus name  TODO Check zero based indexing
			TokenSave = TokenBuffer;
			/* Now get nodes */
			NodeBuffer = TokenBuffer.substring(DotPos, TokenBuffer.length() - DotPos) + " ";

			NodeBufferPos = new MutableInt(0);
			DelimSave = DelimChars;
			DelimChars = ".";
			TokenBuffer = getToken(NodeBuffer, NodeBufferPos);
			try {
				while (TokenBuffer.length() > 0) {
					NumNodes.increment();
					NodeArray[NumNodes.intValue()] = makeInteger();
					if (ConvertError)
						NodeArray[NumNodes.intValue()] = -1;  // indicate an error
					TokenBuffer = getToken(NodeBuffer, NodeBufferPos);
				}
			} catch (Exception e) {
				DSSGlobals.getInstance().getDSSForms().messageDlg("Node buffer too small: " + e.getMessage(), true);
			}

			DelimChars = DelimSave;  // restore to original delimiters
			TokenBuffer = TokenSave;
		}
		return Result;
	}

	public int parseAsVector(int ExpectedSize, double[] VectorBuffer) {
		int NumElements, i;
		String ParseBuffer = null, DelimSave = null;
		MutableInt ParseBufferPos = new MutableInt();

		if (AutoIncrement)
			getNextParam();

		NumElements = 0;
		int Result = 0;  // return 0 if none found or error occurred
		try {
			for (i = 0; i < ExpectedSize; i++)
				VectorBuffer[i] = 0.0;

			/* now get vector values */
			ParseBuffer = TokenBuffer + " ";

			ParseBufferPos.setValue(0);
			DelimSave  = DelimChars;
			DelimChars = DelimChars + MatrixRowTerminator;

			skipWhiteSpace(ParseBuffer, ParseBufferPos);
			TokenBuffer = getToken(ParseBuffer, ParseBufferPos);
			while (TokenBuffer.length() > 0) {
				NumElements += 1;
				if (NumElements <= ExpectedSize)
					VectorBuffer[NumElements] = makeDouble();
				if (LastDelimiter == MatrixRowTerminator)
					break;
				TokenBuffer = getToken(ParseBuffer, ParseBufferPos);
			}

			Result = NumElements;
		} catch (Exception e) {
			DSSGlobals.getInstance().getDSSForms().messageDlg("Vector buffer in parseAsVector probably too small: " + e.getMessage(), true);
		}

		DelimChars  = DelimSave;  // restore to original delimiters
		TokenBuffer = ParseBuffer.substring(ParseBufferPos.intValue(), ParseBuffer.length());  // prepare for next trip

		return Result;
	}

	public int parseAsMatrix(int ExpectedOrder, double[] MatrixBuffer) {
		int i, j, k, ElementsFound;
		double[] RowBuf;

		if (AutoIncrement)
			getNextParam();

		RowBuf = null;

		try {
			RowBuf = new double[ExpectedOrder];

			for (i = 0; i < ExpectedOrder * ExpectedOrder; i++)
				MatrixBuffer[i] = 0.0;

			for (i = 0; i < ExpectedOrder; i++) {
				ElementsFound = parseAsVector(ExpectedOrder, RowBuf);

				/* Returns matrix in column order (Fortran order) */
				k = i;
				for (j = 0; j < ElementsFound; j++) {
					MatrixBuffer[k] = RowBuf[j];
					ExpectedOrder += k;
				}
			}
		} catch (Exception e) {
			DSSGlobals.getInstance().getDSSForms().messageDlg("Matrix buffer in parseAsMatrix probably too small: " + e.getMessage(), true);
		}

		if (RowBuf != null)
			RowBuf = null;

		return ExpectedOrder;
	}

	private int _elementIndex(int ii, int jj, int ExpectedOrder) {
		return (jj - 1) * ExpectedOrder + ii;  // TODO Check zero based indexing
	}

	public int parseAsSymMatrix(int ExpectedOrder, double[] MatrixBuffer) {
		int i, j, ElementsFound;
		double[] RowBuf;

		if (AutoIncrement)
			getNextParam();

		RowBuf = null;

		try {
			RowBuf = new double[ExpectedOrder];

			for (i = 0; i < ExpectedOrder * ExpectedOrder; i++)
				MatrixBuffer[i] = 0.0;

			for (i = 0; i < ExpectedOrder; i++) {
				ElementsFound = parseAsVector(ExpectedOrder, RowBuf);

				/* Returns matrix in Column Order (Fortran order) */
				for (j = 0; j < ElementsFound; j++) {
					MatrixBuffer[_elementIndex(i, j, ExpectedOrder)] = RowBuf[j];
					if (i != j)
						MatrixBuffer[_elementIndex(j, i, ExpectedOrder)] = RowBuf[j];
				}

			}

		} catch (Exception e) {
			DSSGlobals.getInstance().getDSSForms().messageDlg("Matrix buffer in parseAsSymMatrix() probably too small: " + e.getMessage(), true);
		}

		if (RowBuf != null)
			RowBuf = null;

		return ExpectedOrder;
	}

	public String makeString() {
		if (AutoIncrement)
			getNextParam();

		return TokenBuffer;
	}

	public int makeInteger() {
		// Hex integers must be preceeded by "$"
		int Result = 0;
		double Temp = 0;
		MutableInt Code = new MutableInt();

		ConvertError = false;
		if (AutoIncrement)
			getNextParam();

		if (TokenBuffer.length() == 0) {
			Result = 0;
		} else {
			if (IsQuotedString) {
				Temp = interpretRPNString(Code);
				Result = (int) Math.round(Temp);
			} else {
				try {
					Result = Integer.valueOf(TokenBuffer);  // try direct conversion to integer
					Code.setValue(1);
				} catch (NumberFormatException e) {
					Code.setValue(0);
				}
			}

			if (Code.intValue() != 0) {  // on error for integer conversion
				// try again with an double result in case value specified in decimal or some other technique
				try {
					Temp = Double.valueOf(TokenBuffer);
					Code.setValue(1);
				} catch (NumberFormatException e) {
					Code.setValue(0);
				}
				if (Code.intValue() != 0) {
					// not needed with throw ...  Result = 0;
					ConvertError = true;
//					throw new ParserProblem("Integer number conversion error for string: \""+TokenBuffer+"\"");
					DSSGlobals.getInstance().doErrorMsg("", "Integer number conversion error for string: \""+TokenBuffer+"\"", "", 0);
				} else {
					Result = (int) Math.round(Temp);
				}
			}
		}

		return Result;
	}

	public double makeDouble() {
		MutableInt Code = new MutableInt();
		double Result = 0;

		if (AutoIncrement)
			getNextParam();
		ConvertError = false;
		if (TokenBuffer.length() == 0) {
			Result = 0.0;
		} else {
			if (IsQuotedString) {
				Result = interpretRPNString(Code);
			} else {
				try {
					Result = Double.valueOf(TokenBuffer);
					Code.setValue(1);
				} catch (NumberFormatException e) {
					Code.setValue(0);
				}
			}

			if (Code.intValue() != 0) {
				// not needed with throw ...  Result = 0.0;
				ConvertError = true;
//				throw new ParserProblem("Floating point number conversion error for string: \""+TokenBuffer+"\"");
				DSSGlobals.getInstance().doErrorMsg("", "Floating point number conversion error for string: \""+TokenBuffer+"\"", "", 0);
			}
		}

		return Result;
	}

	public String getRemainder() {
		return CmdBuffer.substring(Position, CmdBuffer.length());
	}

	/**
	 * Checks for CommentChar and '//'.
	 */
	private boolean isCommentChar(final String LineBuffer, MutableInt LinePos) {
		switch (LineBuffer.charAt(LinePos.intValue())) {
		case CommentChar:
			return true;
		case '/':
			if ((LineBuffer.length() - 1 > LinePos.intValue()) && (LineBuffer.charAt(LinePos.intValue() + 1) == '/')) {
				return true;
			} else {
				return false;
			}
		default:
			return false;
		}
	}

	private double interpretRPNString(MutableInt Code) {
		MutableInt ParseBufferPos;
		String ParseBuffer;

		Code.setValue(0);
		ParseBuffer = TokenBuffer + " ";
		ParseBufferPos = new MutableInt(0);

		skipWhiteSpace(ParseBuffer, ParseBufferPos);
		TokenBuffer = getToken(ParseBuffer, ParseBufferPos);

		while (TokenBuffer.length() > 0) {

			try {
				Code.setValue( processRPNCommand(TokenBuffer, RPNCalculator) );
			} catch (ParserProblem e) {
				DSSGlobals.getInstance().doErrorMsg("", e.getMessage(), "", 0);
				if (Code.intValue() > 0)
					break;  // Stop on any floating point error
			}

			TokenBuffer = getToken(ParseBuffer, ParseBufferPos);
		}

		double Result = RPNCalculator.getX();

		// prepare for next trip
		TokenBuffer = ParseBuffer.substring(ParseBufferPos.intValue(), ParseBuffer.length());

		return Result;
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

	public String getCmdString() {
		return CmdBuffer;
	}

	public String getToken() {
		return TokenBuffer;
	}

	public void setToken(String Value) {
		this.TokenBuffer = Value;
	}
}
