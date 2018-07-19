package flang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Context {
	Map<String, Double> variables;
	public boolean wasReturn;
	public double returnValue;
	Context parent;
	Interpreter interpreter;

	Context(Context parent, Interpreter interpreter) {
		variables = new HashMap<>();
		wasReturn = false;
		this.parent = parent;
		this.interpreter = interpreter;
	}

	public void addVar(String s, double v) {
		if (variables.containsKey(s))
			throw new Error("Variable " + s + " already exists in current context");
		variables.put(s, v);
	}

	public void setValue(String s, double v) {
		if (variables.containsKey(s)) {
			variables.put(s, v);
			return;
		}
		if (parent != null)
			parent.setValue(s, v);
		else
			throw new Error("Undefined variable: " + s);
	}
	
	public double makeArray(int size) {
		return interpreter.arrayLib.makeArray(size);
	}
	
	public ArrayList<Double> getArray(double id) {
		return interpreter.arrayLib.getArray(id);
	}

	public double getValue(String s) {
		double v = variables.get(s);
		return v;
	}
}