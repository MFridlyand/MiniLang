package flang.utils;

import flang.Context;
import flang.IFunction;

public class Assertion implements IFunction {

    String[] args = { "expr" };
    @Override
    public String[] getArgs() {
        // TODO Auto-generated method stub
        return args;
    }

    @Override
    public int execute(Context ctx) {
        double expr = ctx.getValue(args[0]);
        if (expr == 0.0)
            throw new Error("assertion failed");
        return 0;
    }

}
