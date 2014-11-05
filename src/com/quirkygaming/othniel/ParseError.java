package com.quirkygaming.othniel;

public class ParseError {
	
	public static void validate(boolean condition, int line, String problem) {
		if (!condition) {
			System.err.println("Parsing Error: " + problem + " at line " + line);
			System.exit(1);
		}
	}
	
	public static void throwIf(boolean condition, int line, String problem) {
		validate(!condition, line, problem);
	}
	
}
