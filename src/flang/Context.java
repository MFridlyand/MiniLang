package flang;
import java.util.HashMap;
import java.util.Map;

public class Context {
	Map<String, Double> variables;
	public boolean was_return;
	public double return_value;

	Context() {
		variables = new HashMap<>();
		was_return = false;
	}

	public void setValue(String s, double v) {
		variables.put(s, v);
	}

	public double getValue(String s) {
		double v = variables.get(s);
		return v;
	}
}