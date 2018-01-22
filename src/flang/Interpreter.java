package flang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Interpreter {

	public Interpreter() {
		functions = new HashMap<>();
	}

	public void registerFunction(String name, IFunction f) {
		functions.put(name, f);
	}
	
	public void eval(String expr) {
		this.expr = expr;
		tokens = Token.tokenize(this.expr);
		curToken = 0;
		Context ctx = new Context();
		for (;;) {
			Token tok = getToken();
			if (tok.type == Token.t_end)
				return;
			st(ctx);
		}
	}
	
	private Map<String, IFunction> functions;
	private String expr;
	private Token[] tokens;
	private int curToken;

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
		double cond = e(ctx);
		if (cond != 0) {
			block(ctx);
			if (ctx.was_return)
				return;
			Token tok = getToken();
			if (tok.type == Token.t_else) {
				nextToken();
				skipBlock();
			}
		} else {
			skipBlock();
			Token tok = getToken();
			if (tok.type == Token.t_else) {
				nextToken();
				block(ctx);
			}
		}
	}

	protected void stWhile(Context ctx) {
		nextToken(); // eat while
		int token_offset = getTokenOffset();
		double cond = e(ctx);
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
			double v = e(ctx);
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
			stPrint(ctx);
			break;
		default:
			System.out.println("Unexpected token " + tok.value);
		}
	}

	protected void stPrint(Context ctx) {
		Token p = nextToken(); // eat print
		if (p.type == Token.t_str) {
			String unquote = p.value.substring(1, p.value.length() - 1);
			unquote = unquote.replaceAll("_", " ");
			System.out.println(unquote);
			nextToken();
		} else {
			double printValue = e(ctx);
			if ((printValue == Math.floor(printValue)) && !Double.isInfinite(printValue)) {
			    long val = (long)printValue;
			    System.out.println(val);
			}
			else
				System.out.println(printValue);
		}
	}

	protected void funDef() {
		String name = nextToken().value;
		nextToken();// eat (
		ArrayList<String> argList = new ArrayList<>();
		Token tok = nextToken();
		for (;;) {
			if (tok.type == Token.r_paren)
				break;
			argList.add(tok.value);
			tok = nextToken(); // eat ','
			if (tok.type == Token.r_paren)
				break;
			tok = nextToken();
		}
		nextToken(); // eat )
		int offset = getTokenOffset();
		String[] args = argList.toArray(new String[0]);
		functions.put(name, new UserFunction(args, offset));
		skipBlock();
	}

	protected double funCall(Context ctx) {
		String name = nextToken().value;
		nextToken();// eat name
		nextToken();// eat (
		IFunction f = (IFunction) functions.get(name);
		Context funContext = new Context();
		String[] args = f.getArgs();
		for (int i = 0; i < args.length; i++) {
			double arg_value = e(ctx);
			funContext.setValue(args[i], arg_value);
			if (getToken().type != Token.r_paren)
				nextToken(); // eat ','
		}
		nextToken(); // eat )
		if (f instanceof UserFunction) {
			UserFunction fUser = (UserFunction) f;
			int tok = getTokenOffset();
			setTokenOffset(fUser.tokenOffset);// jump to function body
			block(funContext);// execute function body
			setTokenOffset(tok);// jump back
		} else // built in function
			f.execute(funContext);
		return funContext.return_value;
	}

	protected double id(Context ctx) {
		String id = getToken().value;
		return ctx.getValue(id);
	}

	protected double e(Context ctx) {
		double t1 = e1(ctx);
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

	protected double e1(Context ctx) {
		double t1 = e2(ctx);
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

	protected double e2(Context ctx) {
		double v1 = e3(ctx);
		Token tok = getToken();
		if (tok.type != Token.cmp_op)
			return v1;
		nextToken();
		double v2 = e3(ctx);
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

	protected double e3(Context ctx) {
		double t1 = t(ctx);
		for (;;) {
			Token tok = getToken();
			if (tok.type != Token.add_op) {
				break;
			}

			nextToken();
			double t2 = t(ctx);
			if (tok.value.equals("+"))
				t1 = t1 + t2;
			else
				t1 = t1 - t2;
		}
		return t1;
	}

	protected double t(Context ctx) {
		double f1 = atom(ctx);
		for (;;) {
			Token tok = getToken();
			if (tok.type != Token.mul_op) {
				break;
			}

			nextToken();
			double f2 = atom(ctx);
			if (tok.value.equals("*"))
				f1 = f1 * f2;
			else
				f1 = f1 / f2;
		}
		return f1;
	}

	protected double not(double v) {
		if (v != 0)
			return 0;
		else
			return 1;
	}

	protected double atom(Context ctx) {
		Token tok = getToken();
		if (tok.value.equals("-")) {
			nextToken();
			return -atom(ctx);
		}
		if (tok.type == Token.t_not) {
			nextToken();
			return not(atom(ctx));
		}
		if (tok.type == Token.l_paren) {
			nextToken(); // eat '('
			double res = e(ctx);
			nextToken(); // eat ')'
			return res;
		} else if (tok.type == Token.t_call)
			return funCall(ctx);
		double num = -1;
		if (tok.type == Token.number)
			num = Double.parseDouble(tok.value);
		else
			num = id(ctx);
		nextToken();
		return num;
	}
}
