package flang;

import java.util.ArrayList;

class Token {
	public Token() {
	}

	public Token(int type) {
		this.type = type;
	}

	public static final int NUMBER = 1;
	public static final int ADD_OP = 2;
	public static final int MUL_OP = 3;
	public static final int L_PAREN = 4;
	public static final int R_PAREN = 5;
	public static final int END = 6;
	public static final int ID = 7;
	public static final int ASSIGN = 8;
	public static final int PRINT = 9;
	public static final int OR_OP = 10;
	public static final int AND_OP = 11;
	public static final int CMP_OP = 12;
	public static final int L_BRACE = 13;
	public static final int R_BRACE = 14;
	public static final int IF = 15;
	public static final int ELSE = 16;
	public static final int WHILE = 17;
	public static final int FUNCTION = 18;
	public static final int COMMA = 19;
	public static final int CALL = 20;
	public static final int RETURN = 21;
	public static final int NOT = 22;
	public static final int STR = 23;
	public static final int VAR = 24;
	public static final int L_SQ = 25;
	public static final int R_SQ = 26;

	public String value;
	public int type;

	public static boolean isNumber(String string) {
		boolean result = true;
		try {
			Double.parseDouble(string);
		} catch (NumberFormatException e) {
			result = false;
		}
		return result;
	}

	public static boolean isStringLiteral(String string) {
		if (string.length() < 2)
			return false;
		return string.startsWith("\"") && string.endsWith("\"");
	}

	public static boolean isValidId(String string) {
		if (string.length() < 1)
			return false;
		if (Character.isDigit(string.charAt(0)))
			return false;
		if (string.contains(",") || string.contains("(") || string.contains(")") || string.contains("=") || string.contains("+")
				|| string.contains("-") || string.contains("*") || string.contains("/"))
			return false;
		return true;
	}

	public static Token[] tokenize(String expr) {
		String[] data = expr.split("\\s+|\\r+|\\n+");
		ArrayList<Token> t = new ArrayList<Token>();
		for (String string : data) {
			if (string.isEmpty() || string.equals(" "))
				continue;
			Token tok = new Token();
			if (string.equals("("))
				tok.type = Token.L_PAREN;
			else if (string.equals(")"))
				tok.type = Token.R_PAREN;
			else if (string.equals("+") || string.equals("-"))
				tok.type = Token.ADD_OP;
			else if (string.equals("*") || string.equals("/"))
				tok.type = Token.MUL_OP;
			else if (string.equals("||"))
				tok.type = Token.OR_OP;
			else if (string.equals("&&"))
				tok.type = Token.AND_OP;
			else if (string.equals(">") || string.equals("<") || string.equals("==") || string.equals("!="))
				tok.type = Token.CMP_OP;
			else if (string.equals("="))
				tok.type = Token.ASSIGN;
			else if (string.equals("{"))
				tok.type = Token.L_BRACE;
			else if (string.equals("}"))
				tok.type = Token.R_BRACE;
			else if (string.equals("if"))
				tok.type = Token.IF;
			else if (string.equals("else"))
				tok.type = Token.ELSE;
			else if (string.equals("while"))
				tok.type = Token.WHILE;
			else if (string.equals("function"))
				tok.type = Token.FUNCTION;
			else if (string.equals(","))
				tok.type = Token.COMMA;
			else if (string.equals("call"))
				tok.type = Token.CALL;
			else if (string.equals("return"))
				tok.type = Token.RETURN;
			else if (string.equals("var"))
				tok.type = Token.VAR;
			else if (string.equals("!"))
				tok.type = Token.NOT;
			else if (isStringLiteral(string))
				tok.type = Token.STR;
			else if (string.equals("print"))
				tok.type = Token.PRINT;
			else if (string.equals("["))
				tok.type = Token.L_SQ;
			else if (string.equals("]"))
				tok.type = Token.R_SQ;
			else if (!isNumber(string)) {
				if (!isValidId(string))
					throw new Error("Invalid id: " + string);
				tok.type = Token.ID;
			} else
				tok.type = Token.NUMBER;
			tok.value = string;
			t.add(tok);
		}
		t.add(new Token(Token.END));
		return t.toArray(new Token[t.size()]);
	}
}