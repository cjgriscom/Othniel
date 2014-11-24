package com.quirkygaming.othniel.pipes;

public class StructInput extends Variable {
	
	private boolean isOptional;
	
	public StructInput(Pipe defaultPipe) {
		super(defaultPipe);
	}
	
	public StructInput(String expression, StructInput[] knownInParams, int lineN) {
		super(Pipe.fromExpression(expression, knownInParams, true, lineN));
		if (expression.contains("=")) this.isOptional = true; // Equal sign implies that input is optional
	}
	
	public boolean isOptional() {
		return isOptional;
	}
}
