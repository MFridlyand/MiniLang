package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import flang.Interpreter;

class Test1 {

	@Test
	void test() {
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
	
	@Test
	void test1() {
		try {
			String program = new String(
					Files.readAllBytes(Paths.get("D:\\misha\\src\\hackerrank\\MiniLang\\sort.txt")),
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
	
	@Test
	void test2() {
		try {
			String program = new String(
					Files.readAllBytes(Paths.get("D:\\misha\\src\\hackerrank\\MiniLang\\list.txt")),
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
	
	@Test
	void test3() {
		try {
			String program = new String(
					Files.readAllBytes(Paths.get("D:\\misha\\src\\hackerrank\\MiniLang\\euclid.txt")),
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
	
	@Test
	void test4() {
		try {
			String program = new String(
					Files.readAllBytes(Paths.get("D:\\misha\\src\\hackerrank\\MiniLang\\ftest2.txt")),
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
