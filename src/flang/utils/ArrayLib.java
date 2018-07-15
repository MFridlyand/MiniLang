package flang.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import flang.Context;
import flang.IFunction;
import flang.Interpreter;

public class ArrayLib {
	private Map<Double, ArrayList<Double>> arrays = new HashMap<>();
	int uniqueId = 1;

	public ArrayLib(Interpreter interpreter) {
		interpreter.registerFunction("make_array", new MakeArray());
		interpreter.registerFunction("array_get", new ArrayGet());
		interpreter.registerFunction("array_set", new ArraySet());
		interpreter.registerFunction("array_size", new ArraySize());
		interpreter.registerFunction("array_add", new ArrayAdd());
	}

	class MakeArray implements IFunction {

		String[] args = { "size" };

		@Override
		public String[] getArgs() {
			// TODO Auto-generated method stub
			return args;
		}

		@Override
		public int execute(Context ctx) {
			double size = ctx.getValue("size");
			ctx.returnValue = makeArray((int) size);
			return 0;
		}
	}

	class ArrayGet implements IFunction {

		String[] args = { "id", "index" };

		@Override
		public String[] getArgs() {
			// TODO Auto-generated method stub
			return args;
		}

		@Override
		public int execute(Context ctx) {
			double id = ctx.getValue("id");
			double index = ctx.getValue("index");
			ctx.returnValue = arrayGet(id, (int) index);
			return 0;
		}
	}

	class ArraySet implements IFunction {

		String[] args = { "id", "index", "value" };

		@Override
		public String[] getArgs() {
			// TODO Auto-generated method stub
			return args;
		}

		@Override
		public int execute(Context ctx) {
			double id = ctx.getValue("id");
			double index = ctx.getValue("index");
			double value = ctx.getValue("value");
			arraySet(id, (int) index, value);
			return 0;
		}
	}

	class ArraySize implements IFunction {

		String[] args = { "id" };

		@Override
		public String[] getArgs() {
			// TODO Auto-generated method stub
			return args;
		}

		@Override
		public int execute(Context ctx) {
			double id = ctx.getValue("id");
			ctx.returnValue = arraySize(id);
			return 0;
		}
	}

	class ArrayAdd implements IFunction {

		String[] args = { "id", "value" };

		@Override
		public String[] getArgs() {
			// TODO Auto-generated method stub
			return args;
		}

		@Override
		public int execute(Context ctx) {
			double id = ctx.getValue("id");
			double value = ctx.getValue("value");
			arrayAdd(id, value);
			return 0;
		}
	}

	double makeArray(int size) {
		ArrayList<Double> array = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			array.add(0.0);
		double id = uniqueId;
		arrays.put(id, array);
		uniqueId++;
		return id;
	}

	public double arrayGet(double id, int index) {
		ArrayList<Double> array = arrays.get(id);
		return array.get(index);
	}

	public void arraySet(double id, int index, double value) {
		ArrayList<Double> array = arrays.get(id);
		array.set(index, value);
	}

	void arrayAdd(double id, double value) {
		ArrayList<Double> array = arrays.get(id);
		array.add(value);

	}

	double arraySize(double id) {
		ArrayList<Double> array = arrays.get(id);
		return array.size();
	}

}
