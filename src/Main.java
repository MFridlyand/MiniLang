import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import flang.*;

public class Main {

	static class ModFunction implements IFunction
	{
		String[] args = {"p", "q"};

		@Override
		public String[] getArgs() {
			// TODO Auto-generated method stub
			return args;
		}

		@Override
		public int execute(Context ctx) {
			int p = ctx.getValue("p");
			int q = ctx.getValue("q");
			ctx.return_value = p % q;
			return 1;
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			String program = new String(Files.readAllBytes(Paths.get("D:\\misha\\src\\hackerrank\\MiniLang\\euclid.txt")), StandardCharsets.UTF_8);
			Interpreter lang = new Interpreter(program);
			lang.registerFunction("mod", new ModFunction());
			lang.eval();
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
