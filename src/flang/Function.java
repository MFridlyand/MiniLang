package flang;

class Function {
	Function(String args[], int tokenOffset) {
		this.args = args;
		this.tokenOffset = tokenOffset;
	}

	public String args[];
	public int tokenOffset;
}