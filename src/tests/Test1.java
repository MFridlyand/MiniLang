package tests;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import flang.Interpreter;
import flang.utils.Assertion;

class Test1 {

	// --- sample file tests ---

	@Test
	void test() {
		check("samples//fib.txt");
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

	@Test
	void testExtendedEuclid() {
		check("samples//extended_euclid.txt");
	}

	// --- output verification tests ---

	@Test
	void testPrintNumber() {
		String output = run("print 42");
		assertEquals("42", output.trim());
	}

	@Test
	void testPrintExpression() {
		String output = run("print 3 + 4 * 2");
		assertEquals("11", output.trim());
	}

	@Test
	void testPrintVariable() {
		String output = run("var x = 7\nprint x");
		assertEquals("7", output.trim());
	}

	@Test
	void testIfTrue() {
		String output = run("if 1 > 0 {\nprint 1\n} else {\nprint 0\n}");
		assertEquals("1", output.trim());
	}

	@Test
	void testIfFalse() {
		String output = run("if 0 > 1 {\nprint 1\n} else {\nprint 0\n}");
		assertEquals("0", output.trim());
	}

	@Test
	void testWhileLoop() {
		String output = run("var i = 0\nwhile i < 3 {\ni = i + 1\n}\nprint i");
		assertEquals("3", output.trim());
	}

	@Test
	void testRecursion() {
		String output = run(
			"function fact ( n ) {\n" +
			"  if n < 2 {\n" +
			"    return 1\n" +
			"  }\n" +
			"  return n * call fact ( n - 1 )\n" +
			"}\n" +
			"print call fact ( 5 )");
		assertEquals("120", output.trim());
	}

	@Test
	void testArray() {
		String output = run(
			"var a = call make_array ( 3 )\n" +
			"a [ 0 ] = 10\n" +
			"a [ 1 ] = 20\n" +
			"a [ 2 ] = 30\n" +
			"print a [ 0 ]\n" +
			"print a [ 1 ]\n" +
			"print a [ 2 ]");
		assertEquals("10\n20\n30", output.trim());
	}

	// --- error condition tests ---

	@Test
	void testUndefinedVariable() {
		flang.Error ex = assertThrows(flang.Error.class, () -> run("print x"));
		assertTrue(ex.getMessage().contains("Undefined variable: x"));
	}

	@Test
	void testUndefinedFunction() {
		flang.Error ex = assertThrows(flang.Error.class, () -> run("call no_such_fn ( )"));
		assertTrue(ex.getMessage().contains("Unknown function: no_such_fn"));
	}

	@Test
	void testDuplicateVariable() {
		flang.Error ex = assertThrows(flang.Error.class, () -> run("var x = 1\nvar x = 2"));
		assertTrue(ex.getMessage().contains("x"));
	}

	@Test
	void testErrorContainsLineNumber() {
		flang.Error ex = assertThrows(flang.Error.class, () -> run("var a = 1\nvar b = 2\nprint z"));
		assertTrue(ex.getMessage().startsWith("Line "), "Expected line number in: " + ex.getMessage());
	}

	@Test
	void testSyntaxError() {
		assertThrows(flang.Error.class, () -> run("var = 1"));
	}

	// --- helpers ---

	protected void check(String path) {
		try {
			String program = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
			Interpreter lang = new Interpreter();
			lang.registerFunction("assert", new Assertion());
			lang.eval(program);
			System.out.println("done");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}

	protected String run(String program) {
		Interpreter lang = new Interpreter();
		lang.registerFunction("assert", new Assertion());
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		PrintStream old = System.out;
		System.setOut(new PrintStream(buf));
		try {
			lang.eval(program);
		} finally {
			System.setOut(old);
		}
		return buf.toString();
	}
}
