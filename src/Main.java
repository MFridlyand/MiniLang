import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import flang.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			String program = new String(Files.readAllBytes(Paths.get("D:\\misha\\src\\hackerrank\\MiniLang\\euclid.txt")));
			FLang lang = new FLang(program);
			lang.eval();
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
