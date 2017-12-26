package flang;

public interface IFunction {
	String[] getArgs();
	int execute(Context ctx);

}
