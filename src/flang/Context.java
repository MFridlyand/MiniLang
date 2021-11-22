package flang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import flang.utils.ArrayLib;

public class Context {
    Map<String, Double> variables;
    public boolean wasReturn;
    public double returnValue;
    Context parent;
    ArrayLib arrayLib;

    Context(Context parent, ArrayLib arrayLib) {
        variables = new HashMap<>();
        wasReturn = false;
        this.parent = parent;
        this.arrayLib = arrayLib;
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
        return arrayLib.makeArray(size);
    }
    
    public ArrayList<Double> getArray(double id) {
        return arrayLib.getArray(id);
    }

    public double getValue(String s) {
        Double v = variables.get(s);
        if (v != null)
            return v.doubleValue();
        if (parent != null)
            return parent.getValue(s);
        else
            throw new Error("Undefined variable: " + s);
    }
}