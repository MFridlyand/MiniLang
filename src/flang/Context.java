package flang;
import java.util.HashMap;
import java.util.Map;

public class Context {
	Map<String, Double> variables;
	public boolean was_return;
	public double return_value;
	Context parent;

	Context(Context parent) {
		variables = new HashMap<>();
		was_return = false;
		this.parent = parent;
	}
	public void addVar(String s, double v) {
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

	public double getValue(String s) {
		double v = variables.get(s);
		return v;
	}
}