package flang.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import flang.Context;
import flang.IFunction;
import flang.IRegFunction;

public class ArrayLib {
    private Map<Double, ArrayList<Double>> arrays = new HashMap<>();
    int uniqueId = 1;

    public ArrayLib(IRegFunction registrator) {
        registrator.registerFunction("make_array", new MakeArray());
        registrator.registerFunction("array_copy", new ArrayCopy());
        registrator.registerFunction("array_free", new ArrayFree());
        registrator.registerFunction("array_size", new ArraySize());
        registrator.registerFunction("array_add", new ArrayAdd());
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
    
    class ArrayCopy implements IFunction {

        String[] args = { "id" };

        @Override
        public String[] getArgs() {
            // TODO Auto-generated method stub
            return args;
        }

        @Override
        public int execute(Context ctx) {
            double id = ctx.getValue("id");
            ctx.returnValue = copyArray(id);
            return 0;
        }
    }
    
    class ArrayFree implements IFunction {

        String[] args = { "id" };

        @Override
        public String[] getArgs() {
            // TODO Auto-generated method stub
            return args;
        }

        @Override
        public int execute(Context ctx) {
            double id = ctx.getValue("id");
            freeArray(id);
            ctx.returnValue = 0;
            return 0;
        }
    }

    public double makeArray(int size) {
        ArrayList<Double> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            array.add(0.0);
        double id = uniqueId;
        arrays.put(id, array);
        uniqueId++;
        return id;
    }
    
    double copyArray(double id) {
        ArrayList<Double> array = arrays.get(id);
        ArrayList<Double> copy = new ArrayList<>();
        for (int i = 0; i < array.size(); i++)
            copy.add(array.get(i).doubleValue());
        double newid = uniqueId;
        arrays.put(newid, copy);
        uniqueId++;
        return newid;
    }
    
    public ArrayList<Double> getArray(double id){
        return arrays.get(id);
    }
    
    void freeArray(double id) {
        arrays.remove(id);
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
