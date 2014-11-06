package com.quirkygaming.othniel;

public class ParseError {
	
	public static void validate(boolean condition, int lineN, String problem) {
		if (!condition) {
			System.err.println("Parsing Error: " + problem + " at line " + lineN);
			System.exit(1);
		}
	}
	
	public static void verifySymbolRecognized(boolean condition, int lineN, String token) {
		if (!condition) {
			if (token.startsWith("<")) {
				System.err.println("Parsing Error: Valid > link not found for < at line " + lineN);
			} else {
				System.err.println("Parsing Error: Symbol not recognized: " + token + " at line " + lineN);
			}
			System.exit(1);
		}
	}
	
	public static void throwIf(boolean condition, int lineN, String problem) {
		validate(!condition, lineN, problem);
	}
	
	public static void verifyExpectedBit(int bitCompound, int bitToCheck, int lineN, String problem) {
		boolean good = ((bitCompound>>Util.binlog(bitToCheck)) & 1) != 0; // Check if bit is set
		validate(good, lineN, problem);
	}
	
}
