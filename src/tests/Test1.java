package tests;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import flang.Interpreter;
import flang.utils.Assertion;

class Test1 {

	@Test
	void test() {
		check("samples//fib.txt");
	}

	protected void check(String path) {
		try {
			String program = new String(
					Files.readAllBytes(Paths.get(path)),
					StandardCharsets.UTF_8);
			Interpreter lang = new Interpreter();
			lang.registerFunction("assert", new Assertion());
			lang.eval(program);
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void test1() {
		check("samples//sort.txt");
	}
	
	@Test
	void test2() {
		check("samples//list.txt");
	}
	
	@Test
	void test3() {
		check("samples//euclid.txt");
	}
	
	@Test
	void test4() {
		check("samples//ftest2.txt");
	}
	
	@Test
	void test5() {
		check("samples//ftest.txt");
	}
}
