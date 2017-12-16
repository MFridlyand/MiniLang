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
		public static final int t_function = 18;
		public static final int t_colon = 19;
		public static final int t_call = 20;
		public static final int t_return = 21;

		public String value;
		public int type;
	}

	private static class Function {
		Function(String args[], int tokenOffset) {
			this.args = args;
			this.tokenOffset = tokenOffset;
		}

		public String args[];
		public int tokenOffset;
	}

	HashMap<String, Function> functions;

	private static class Context {
		HashMap<String, Integer> variables;
		boolean was_return;
		int return_value;

		Context() {
			variables = new HashMap<>();
			was_return = false;
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
		functions = new HashMap<>();
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
			if (ctx.was_return)
				return;
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
		case Token.t_function: {
			funDef();
		}
			break;
		case Token.t_return: {
			nextToken();
			ctx.return_value = e(ctx);
			ctx.was_return = true;
		}
			break;
		case Token.t_if: {
			if (ctx.was_return)
				return;
			nextToken();
			int cond = e(ctx);
			if (cond != 0) {
				block(ctx);
				if (ctx.was_return)
					return;
				Token t_else = getToken();
				if (t_else.type != Token.t_else) {
					return;
				}
				nextToken();
				skipBlock();
			} else {
				skipBlock();
				Token t_else = getToken();
				if (t_else.type != Token.t_else) {
					return;
				}
				nextToken();
				block(ctx);
			}
		}
			break;
		case Token.t_while: {
			if (ctx.was_return)
				return;
			nextToken();
			int token_offset = getTokenOffset();
			int cond = e(ctx);
			while (cond != 0) {
				block(ctx);
				if (ctx.was_return)
					return;
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
	
	protected void funDef() {
		String name = nextToken().value;
		nextToken();// eat (
		ArrayList<String> argList = new ArrayList<>();
		Token tok = nextToken();
		for (;;) {
			if (tok.type == Token.r_bracket)
				break;
			argList.add(tok.value);
			tok = nextToken(); // eat ','
			if (tok.type == Token.r_bracket)
				break;

			tok = nextToken();
		}
		nextToken(); // eat )
		int offset = getTokenOffset();
		String[] args = argList.toArray(new String[0]);
		functions.put(name, new Function(args, offset));
		skipBlock();
	}

	protected int funCall(Context ctx) {
		String name = nextToken().value;
		nextToken();// eat name
		nextToken();// eat (
		Function f = (Function) functions.get(name);
		int args[] = new int[f.args.length];
		Context funContext = new Context();
		for (int i = 0; i < args.length; i++) {
			int arg_value = e(ctx);
			funContext.setValue(f.args[i], arg_value);
			if (getToken().type != Token.r_bracket)
				nextToken();
		}
		nextToken(); // eat )
		int tok = getTokenOffset();
		setTokenOffset(f.tokenOffset);
		block(funContext);
		setTokenOffset(tok);
		return funContext.return_value;
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
		else if (tok.value.equals("<"))
			result = v1 < v2;
		else if (tok.value.equals("=="))
			result = v1 == v2;
		else
			result = v1 != v2;
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
		int f1 = f(ctx);
		for (;;) {
			Token tok = getToken();
			if (tok.type != Token.mul_op) {
				break;
			}

			nextToken();
			int f2 = f(ctx);
			if (tok.value.equals("*"))
				f1 = f1 * f2;
			else
				f1 = f1 / f2;
		}
		return f1;
	}

	protected int f(Context ctx) {
		Token tok = getToken();
		if (tok.type == Token.l_bracket) {
			nextToken(); // eat '('
			int res = e(ctx);
			nextToken(); // eat ')'
			return res;
		} else if (tok.type == Token.t_call)
			return funCall(ctx);
		int num = -1;
		if (tok.type == Token.number)
			num = Integer.parseInt(tok.value);
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
