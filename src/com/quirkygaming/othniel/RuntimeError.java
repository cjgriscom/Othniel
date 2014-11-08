package com.quirkygaming.othniel;

import com.quirkygaming.othniel.pipes.Pipe;

public class RuntimeError {
	
	public static void checkTypes(Pipe p1, Pipe p2, CachedCall call) {
		if (! p1.type().equals(p2.type())) {
			System.err.println("Cannot cast " + p1.type() + " to " + p2.type() + " at line " + call.getLine());
			System.exit(1);
		}
	}
	
	public static void validate(boolean condition, int line, String problem) {
		if (!condition) {
			System.err.println("Runtime Error: " + problem + " at line " + line);
			System.exit(1);
		}
	}
	
	public static void throwIf(boolean condition, int line, String problem) {
		validate(!condition, line, problem);
	}
	
}
