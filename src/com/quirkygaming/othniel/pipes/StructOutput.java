package com.quirkygaming.othniel.pipes;

public class StructOutput extends Node {
	
	public StructOutput(Pipe definition) {
		super(definition);
	}
	
	public StructOutput(String expression, StructInput[] inParams, int lineN) {
		super(Pipe.fromExpression(expression, inParams, false, lineN));
		this.label = definition.label;
	}
}
