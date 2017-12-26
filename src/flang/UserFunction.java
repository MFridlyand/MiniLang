package flang;

class UserFunction  implements IFunction{
	UserFunction(String args[], int tokenOffset) {
		this.args = args;
		this.tokenOffset = tokenOffset;
	}
	public String args[];
	public int tokenOffset;
	@Override
	public String[] getArgs() {
		// TODO Auto-generated method stub
		return args;
	}
	@Override
	public int execute(Context ctx) {
		// TODO Auto-generated method stub
		return 0;
	}
}