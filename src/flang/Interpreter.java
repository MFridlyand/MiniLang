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
		globalContext = new Context(null);
		for (;;) {
			Token tok = getToken();
			if (tok.type == Token.END)
				return;
			st(globalContext);
		}
	}

	private Map<String, IFunction> functions;
	private Context globalContext;
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

	protected void ensureToken(int type) {
		if (getToken().type != type)
			throw new Error("Unexpected token: " + getToken().value);
	}

	protected Token eat(int type) {
		ensureToken(type);
		return nextToken();
	}

	protected void skipBlock() {
		int brackets = 1;
		ensureToken(Token.L_BRACE);
		while (brackets > 0) {
			Token tok = nextToken();
			if (tok.type == Token.L_BRACE)
				brackets++;
			else if (tok.type == Token.R_BRACE)
				brackets--;
			else if (tok.type == Token.END)
				throw new Error("Invalid braces structure");
		}
		eat(Token.R_BRACE);
	}

	protected void block(Context ctx) {
		eat(Token.L_BRACE);
		for (;;) {
			Token tok = getToken();
			if (tok.type == Token.R_BRACE) {
				eat(Token.R_BRACE);
				return;
			}
			if (tok.type == Token.END)
				throw new Error("Invalid braces structure");
			st(ctx);
			if (ctx.wasReturn)
				return;
		}
	}

	protected void stIf(Context ctx) {
		eat(Token.IF);
		double cond = expr(ctx);
		if (cond != 0) {
			block(ctx);
			if (ctx.wasReturn)
				return;
			Token tok = getToken();
			if (tok.type == Token.ELSE) {
				eat(Token.ELSE);
				skipBlock();
			}
		} else {
			skipBlock();
			Token tok = getToken();
			if (tok.type == Token.ELSE) {
				eat(Token.ELSE);
				block(ctx);
			}
		}
	}

	protected void stWhile(Context ctx) {
		eat(Token.WHILE); // eat while
		int token_offset = getTokenOffset();
		double cond = expr(ctx);
		while (cond != 0) {
			block(ctx);
			if (ctx.wasReturn)
				return;
			setTokenOffset(token_offset);
			cond = expr(ctx);
		}
		skipBlock();
	}

	protected void st(Context ctx) {
		Token tok = getToken();
		switch (tok.type) {
		case Token.ID: // process assignment
			stAssign(ctx);
			break;
		case Token.VAR: // process var
			stVarDef(ctx);
			break;
		case Token.FUNCTION:
			funDef();
			break;
		case Token.RETURN:
			eat(Token.RETURN); // eat return
			ctx.returnValue = expr(ctx);
			ctx.wasReturn = true;
			break;
		case Token.IF:
			stIf(ctx);
			break;
		case Token.WHILE:
			stWhile(ctx);
			break;
		case Token.PRINT:
			stPrint(ctx);
			break;
		default:
			throw new Error("Unexpected token " + tok.value);
		}
	}

	protected void stVarDef(Context ctx) {
		eat(Token.VAR);
		String var = getToken().value;
		eat(Token.ID); // eat id
		eat(Token.ASSIGN); // eat assign
		double val = expr(ctx);
		ctx.addVar(var, val);
	}

	protected void stAssign(Context ctx) {
		String ident = getToken().value;
		eat(Token.ID); // eat id
		eat(Token.ASSIGN); // eat assign
		double v = expr(ctx);
		ctx.setValue(ident, v);
	}

	protected void stPrint(Context ctx) {
		Token p = eat(Token.PRINT); // eat print
		if (p.type == Token.STR) {
			String unquote = p.value.substring(1, p.value.length() - 1);
			unquote = unquote.replaceAll("_", " ");
			System.out.println(unquote);
			nextToken();
		} else {
			double printValue = expr(ctx);
			if ((printValue == Math.floor(printValue)) && !Double.isInfinite(printValue)) {
				long val = (long) printValue;
				System.out.println(val);
			} else
				System.out.println(printValue);
		}
	}

	protected void funDef() {
		String name = eat(Token.FUNCTION).value;// eat function and get name
		eat(Token.ID); // eat name
		ArrayList<String> argList = new ArrayList<>();
		eat(Token.L_PAREN); // eat (
		while (getToken().type != Token.R_PAREN) {
			argList.add(getToken().value);
			eat(Token.ID); // eat arg name
			if (getToken().type == Token.COMMA)
				nextToken();
		}
		eat(Token.R_PAREN); // eat )
		int offset = getTokenOffset();
		String[] args = argList.toArray(new String[0]);
		functions.put(name, new UserFunction(args, offset));
		skipBlock();
	}

	protected double funCall(Context ctx) {
		String name = nextToken().value;
		eat(Token.ID);// eat name
		IFunction f = (IFunction) functions.get(name);
		Context funContext = prepareCallContext(ctx, f);
		if (f instanceof UserFunction) {
			UserFunction fUser = (UserFunction) f;
			int tok = getTokenOffset();
			setTokenOffset(fUser.tokenOffset);// jump to function body
			block(funContext);// execute function body
			setTokenOffset(tok);// jump back
		} else // built in function
			f.execute(funContext);
		return funContext.returnValue;
	}

	protected Context prepareCallContext(Context ctx, IFunction f) {
		Context funContext = new Context(globalContext);
		String[] args = f.getArgs();
		eat(Token.L_PAREN);// eat (
		for (int i = 0; i < args.length; i++) {
			double arg_value = expr(ctx);
			funContext.addVar(args[i], arg_value);
			if (getToken().type != Token.R_PAREN)
				eat(Token.COMMA); // eat ','
		}
		eat(Token.R_PAREN); // eat )
		return funContext;
	}

	protected double id(Context ctx) {
		String id = getToken().value;
		return ctx.getValue(id);
	}

	protected double expr(Context ctx) {
		double t1 = andExpr(ctx);
		for (;;) {
			Token tok = getToken();
			if (tok.type != Token.OR_OP)
				break;
			eat(Token.OR_OP);
			boolean t2 = andExpr(ctx) != 0;
			t1 = t1 != 0 || t2 ? 1 : 0;
		}
		return t1;
	}

	protected double andExpr(Context ctx) {
		double t1 = cmpExpr(ctx);
		for (;;) {
			Token tok = getToken();
			if (tok.type != Token.AND_OP)
				break;
			eat(Token.AND_OP);
			boolean t2 = cmpExpr(ctx) != 0;
			t1 = t1 != 0 && t2 ? 1 : 0;
		}
		return t1;
	}

	protected double cmpExpr(Context ctx) {
		double v1 = addExpr(ctx);
		Token tok = getToken();
		if (tok.type != Token.CMP_OP)
			return v1;
		eat(Token.CMP_OP);
		double v2 = addExpr(ctx);
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

	protected double addExpr(Context ctx) {
		double t1 = mulExpr(ctx);
		for (;;) {
			Token tok = getToken();
			if (tok.type != Token.ADD_OP) {
				break;
			}

			eat(Token.ADD_OP);
			double t2 = mulExpr(ctx);
			if (tok.value.equals("+"))
				t1 = t1 + t2;
			else
				t1 = t1 - t2;
		}
		return t1;
	}

	protected double mulExpr(Context ctx) {
		double f1 = atom(ctx);
		for (;;) {
			Token tok = getToken();
			if (tok.type != Token.MUL_OP) {
				break;
			}

			eat(Token.MUL_OP);
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
		} else if (tok.type == Token.NOT) {
			nextToken();
			return not(atom(ctx));
		} else if (tok.type == Token.L_PAREN) {
			eat(Token.L_PAREN); // eat '('
			double res = expr(ctx);
			eat(Token.R_PAREN); // eat ')'
			return res;
		} else if (tok.type == Token.CALL)
			return funCall(ctx);
		double num = -1;
		if (tok.type == Token.NUMBER)
			num = Double.parseDouble(tok.value);
		else
			num = id(ctx);
		nextToken();
		return num;
	}
}
