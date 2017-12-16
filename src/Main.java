import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			String program = new String(Files.readAllBytes(Paths.get("d:/misha/src/euclid.txt")));
			FLang lang = new FLang(program);
			lang.eval();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
