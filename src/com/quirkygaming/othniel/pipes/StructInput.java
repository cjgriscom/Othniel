package com.quirkygaming.othniel.pipes;

public class StructInput extends Node {
	
	private boolean isOptional;
	
	public StructInput(Pipe definition) {
		super(definition);
	}
	
	public StructInput(String expression, StructInput[] knownInParams, int lineN) {
		super(Pipe.fromExpression(expression, knownInParams, true, lineN));
		this.label = definition.label;
		if (expression.contains("=")) this.isOptional = true; // Equal sign implies that input is optional
	}
	
	public boolean isOptional() {
		return isOptional;
	}
}
