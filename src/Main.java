import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import flang.*;

public class Main {

	public static void main(String[] args) {
		try {
			String program = new String(
					Files.readAllBytes(Paths.get("D:\\misha\\src\\hackerrank\\MiniLang\\fib.txt")),
					StandardCharsets.UTF_8);
			Interpreter lang = new Interpreter();
			lang.eval(program);
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
