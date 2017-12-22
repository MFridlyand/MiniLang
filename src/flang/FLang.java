package flang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FLang {

	private Map<String, Function> functions;
	private String expr;
	private Token[] tokens;
	private int curToken;

	public FLang(String expr) {
		this.expr = expr;
		tokens = Token.tokenize(this.expr);
		curToken = 0;
		functions = new HashMap<>();
	}

	protected Token getToken() {
		return tokens[curToken];
	}

	protected Token nextToken() {
		curToken++;
		return getToken();
	}

	protected void putBack() {
		curToken--;
	}

	protected int getTokenOffset() {
		return curToken;
	}

	protected void setTokenOffset(int value) {
		curToken = value;
	}

	protected void skipBlock() {
		int brackets = 1;
		while (brackets > 0) {
			Token tok = nextToken();
			if (tok.type == Token.l_brace)
				brackets++;
			if (tok.type == Token.r_brace)
				brackets--;
		}
		nextToken();
	}

	protected void block(Context ctx) {
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

	protected void stIf(Context ctx) {
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

	protected void stWhile(Context ctx) {
		nextToken(); // eat while
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

	protected void st(Context ctx) {
		Token tok = getToken();
		switch (tok.type) {
		case Token.t_id: // process assignment
			String ident = getToken().value;
			nextToken(); // eat id
			nextToken(); // eat assign
			int v = e(ctx);
			ctx.setValue(ident, v);
			break;
		case Token.t_function:
			funDef();
			break;
		case Token.t_return:
			nextToken(); // eat return
			ctx.return_value = e(ctx);
			ctx.was_return = true;
			break;
		case Token.t_if:
			stIf(ctx);
			break;
		case Token.t_while:
			stWhile(ctx);
			break;
		case Token.t_print:
			Token p = nextToken(); // eat print
			if (p.type == Token.t_str) {
				String unquote = p.value.substring(1, p.value.length() - 1);
				unquote = unquote.replaceAll("_", " ");
				System.out.println(unquote);
				nextToken();
			} else {
				int printValue = e(ctx);
				System.out.println(printValue);
			}
			break;
		default:
			System.out.println("Unknown token " + tok.value);
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
		setTokenOffset(f.tokenOffset);// jump to function body
		block(funContext);// execute function body
		setTokenOffset(tok);// jump back
		return funContext.return_value;
	}

	protected int id(Context ctx) {
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

	protected int not(int v) {
		if (v != 0)
			return 0;
		else
			return 1;
	}

	protected int f(Context ctx) {
		Token tok = getToken();
		if (tok.value.equals("-")) {
			nextToken();
			return -f(ctx);
		}
		if (tok.type == Token.t_not) {
			nextToken();
			return not(f(ctx));
		}
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
