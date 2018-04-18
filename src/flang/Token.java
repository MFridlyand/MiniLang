package flang;

import java.util.ArrayList;

class Token {
	public Token() {
	}

	public Token(int type) {
		this.type = type;
	}

	public static final int number = 1;
	public static final int add_op = 2;
	public static final int mul_op = 3;
	public static final int l_paren = 4;
	public static final int r_paren = 5;
	public static final int t_end = 6;
	public static final int t_id = 7;
	public static final int t_assign = 8;
	public static final int t_print = 9;
	public static final int or_op = 10;
	public static final int and_op = 11;
	public static final int cmp_op = 12;
	public static final int l_brace = 13;
	public static final int r_brace = 14;
	public static final int t_if = 15;
	public static final int t_else = 16;
	public static final int t_while = 17;
	public static final int t_function = 18;
	public static final int t_colon = 19;
	public static final int t_call = 20;
	public static final int t_return = 21;
	public static final int t_not = 22;
	public static final int t_str = 23;

	public String value;
	public int type;
	
	public static boolean isNumber(String s) {
		boolean result = true;
		try {
			Double.parseDouble(s);
		} catch (NumberFormatException e) {
			result = false;
		}
		return result;
	}
	
	public static boolean isStringLiteral(String s) {
		if (s.length() < 2)
			return false;
		return s.startsWith("\"") && s.endsWith("\"");
	}
	
	public static boolean isValidId(String s) {
		if (s.length() < 1)
			return false;
		if (Character.isDigit(s.charAt(0)))
			return false;
		if (s.contains(",") || s.contains("(") || s.contains(")") || s.contains("=")
				|| s.contains("+") || s.contains("-") || s.contains("*") || s.contains("/"))
			return false;
		return true;
	}
	
	public static Token[] tokenize(String expr) {
		String[] data = expr.split("\\s+|\\r+|\\n+");
		ArrayList<Token> t = new ArrayList<Token>();
		for (String s : data) {
			if (s.isEmpty() || s.equals(" "))
				continue;
			Token tok = new Token();
			if (s.equals("("))
				tok.type = Token.l_paren;
			else if (s.equals(")"))
				tok.type = Token.r_paren;
			else if (s.equals("+") || s.equals("-"))
				tok.type = Token.add_op;
			else if (s.equals("*") || s.equals("/"))
				tok.type = Token.mul_op;
			else if (s.equals("||"))
				tok.type = Token.or_op;
			else if (s.equals("&&"))
				tok.type = Token.and_op;
			else if (s.equals(">") || s.equals("<") || s.equals("==") || s.equals("!="))
				tok.type = Token.cmp_op;
			else if (s.equals("="))
				tok.type = Token.t_assign;
			else if (s.equals("{"))
				tok.type = Token.l_brace;
			else if (s.equals("}"))
				tok.type = Token.r_brace;
			else if (s.equals("if"))
				tok.type = Token.t_if;
			else if (s.equals("else"))
				tok.type = Token.t_else;
			else if (s.equals("while"))
				tok.type = Token.t_while;
			else if (s.equals("function"))
				tok.type = Token.t_function;
			else if (s.equals(","))
				tok.type = Token.t_colon;
			else if (s.equals("call"))
				tok.type = Token.t_call;
			else if (s.equals("return"))
				tok.type = Token.t_return;
			else if (s.equals("!"))
				tok.type = Token.t_not;
			else if (isStringLiteral(s))
				tok.type = Token.t_str;
			else if (s.equals("print"))
				tok.type = Token.t_print;
			else if (!isNumber(s)) {
				if (!isValidId(s))
					throw new Error("Invalid id: "+ s);
				tok.type = Token.t_id;
			}
			else
				tok.type = Token.number;
			tok.value = s;
			t.add(tok);
		}
		t.add(new Token(Token.t_end));
		return t.toArray(new Token[t.size()]);
	}
}