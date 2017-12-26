package flang;
import java.util.HashMap;
import java.util.Map;

public class Context {
	Map<String, Integer> variables;
	public boolean was_return;
	public int return_value;

	Context() {
		variables = new HashMap<>();
		was_return = false;
	}

	public void setValue(String s, int v) {
		variables.put(s, v);
	}

	public int getValue(String s) {
		int v = variables.get(s);
		return v;
	}
}