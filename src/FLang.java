import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class FLang {
	private static class Token {
		public Token() {
		}

		public Token(int type) {
			this.type = type;
		}

		public static final int number = 1;
		public static final int add_op = 2;
		public static final int mul_op = 3;
		public static final int l_bracket = 4;
		public static final int r_bracket = 5;
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

		public String value;
		public int type;
	}

	public static class Context {
		HashMap<String, Integer> variables;

		Context() {
			variables = new HashMap<>();
		}

		public void setValue(String s, int v) {
			variables.put(s, v);
		}

		public int getValue(String s) {
			int v = (Integer) variables.get(s);
			return v;
		}
	}

	String expr;
	Token[] tokens;
	int curToken;

	public FLang(String expr) {
		this.expr = expr;
		tokenize(this.expr);
		curToken = 0;
	}

	protected boolean isNumber(String s) {
		boolean result = true;
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			result = false;
		}
		return result;
	}

	protected void tokenize(String expr) {
		String[] data = expr.split("\\s+|\\r+|\\n+");
		ArrayList<Token> t = new ArrayList<Token>();
		for (String s : data) {
			if (s.isEmpty() || s.equals(" "))
				continue;
			Token tok = new Token();
			if (s.equals("("))
				tok.type = Token.l_bracket;
			else if (s.equals(")"))
				tok.type = Token.r_bracket;
			else if (s.equals("+") || s.equals("-"))
				tok.type = Token.add_op;
			else if (s.equals("*") || s.equals("/"))
				tok.type = Token.mul_op;
			else if (s.equals("||"))
				tok.type = Token.or_op;
			else if (s.equals("&&"))
				tok.type = Token.and_op;
			else if (s.equals(">") || s.equals("<"))
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
			else if (s.equals("print"))
				tok.type = Token.t_print;
			else if (!isNumber(s))
				tok.type = Token.t_id;
			else
				tok.type = Token.number;
			tok.value = s;
			t.add(tok);
		}
		t.add(new Token(Token.t_end));
		tokens = t.toArray(new Token[t.size()]);
	}

	protected Token getToken() {
		return tokens[curToken];
	}

	protected Token nextToken() {
		curToken++;
		return getToken();
	}

	void putBack() {
		curToken--;
	}

	int getTokenOffset() {
		return curToken;
	}

	void setTokenOffset(int value) {
		curToken = value;
	}

	void skipBlock() {
		Stack<String> st = new Stack<>();
		Token tok = getToken();
		st.push(tok.value);
		while (!st.empty()) {
			tok = nextToken();
			if (tok.type == Token.l_brace)
				st.push(tok.value);
			if (tok.type == Token.r_brace)
				st.pop();
		}
		nextToken();
	}

	void block(Context ctx) {
		nextToken();
		for (;;) {
			Token tok = getToken();
			if (tok.type == Token.r_brace) {
				nextToken();
				return;
			}
			st(ctx);
		}
	}

	void st(Context ctx) {
		Token tok = getToken();
		switch (tok.type) {
		case Token.t_id: {
			String ident = getToken().value;
			nextToken(); // eat id
			nextToken(); // eat assign
			int v = e(ctx);
			ctx.setValue(ident, v);
		}
			break;
		case Token.t_if: {
			nextToken();
			int cond = e(ctx);
			if (cond != 0) {
				block(ctx);
				Token t_else = getToken();
				if (t_else.type != Token.t_else) {
					// putBack();
					return;
				}
				nextToken();
				skipBlock();
			} else {
				skipBlock();
				Token t_else = getToken();
				if (t_else.type != Token.t_else) {
					// putBack();
					return;
				}
				nextToken();
				block(ctx);
			}
		}
			break;
		case Token.t_while: {
			nextToken();
			int token_offset = getTokenOffset();
			int cond = e(ctx);
			while (cond != 0) {
				block(ctx);
				setTokenOffset(token_offset);
				cond = e(ctx);
			}
			skipBlock();
		}
			break;
		case Token.t_print: {
			nextToken(); // eat print
			int v = e(ctx);
			System.out.println(v);
		}
			break;
		}
	}

	int id(Context ctx) {
		String id = getToken().value;
		return ctx.getValue(id);
	}

	protected int e(Context ctx) {
		int t1 = e1(ctx);
		boolean result = t1 != 0;
		boolean has_or = false;
		for (;;) {
			Token tok = getToken();

			if (tok.type != Token.or_op) {
				if (!has_or)
					return t1;
				break;
			}

			has_or = true;
			nextToken();
			boolean t2 = e1(ctx) != 0;
			result = result || t2;
		}
		return result ? 1 : 0;
	}

	protected int e1(Context ctx) {
		int t1 = e2(ctx);
		boolean result = t1 != 0;
		boolean has_and = false;
		for (;;) {
			Token tok = getToken();

			if (tok.type != Token.and_op) {
				if (!has_and)
					return t1;
				break;
			}

			has_and = true;
			nextToken();
			boolean t2 = e2(ctx) != 0;
			result = result && t2;
		}
		return result ? 1 : 0;
	}

	protected int e2(Context ctx) {
		int v1 = e3(ctx);
		Token tok = getToken();
		if (tok.type != Token.cmp_op)
			return v1;
		nextToken();
		int v2 = e3(ctx);
		boolean result = false;
		if (tok.value.equals(">"))
			result = v1 > v2;
		else
			result = v1 < v2;
		return result ? 1 : 0;
	}

	protected int e3(Context ctx) {

		int t1 = t(ctx);

		for (;;) {
			Token tok = getToken();

			if (tok.type != Token.add_op) {
				break;
			}

			nextToken();
			int t2 = t(ctx);

			if (tok.value.equals("+"))
				t1 = t1 + t2;
			else
				t1 = t1 - t2;
		}
		return t1;
	}

	protected int t(Context ctx) {
		Token e = getToken();
		int f1 = f(ctx);

		for (;;) {
			e = getToken();
			if (e.type != Token.mul_op) {
				break;
			}

			nextToken();
			int f2 = f(ctx);
			if (e.value.equals("*"))
				f1 = f1 * f2;
			else
				f1 = f1 / f2;
		}

		return f1;
	}

	protected int f(Context ctx) {
		Token e = getToken();
		if (e.type == Token.l_bracket) {
			nextToken(); // eat '('
			int res = e(ctx);
			nextToken(); // eat ')'
			return res;
		}
		int num = -1;
		if (e.type == Token.number)
			num = Integer.parseInt(e.value);
		else
			num = id(ctx);
		nextToken();
		return num;
	}

	public void eval() {
		Context ctx = new Context();
		for (;;) {
			Token tok = getToken();
			if (tok.type == Token.t_end)
				return;
			st(ctx);
		}
	}
}
