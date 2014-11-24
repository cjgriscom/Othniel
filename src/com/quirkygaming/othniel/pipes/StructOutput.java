package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.ParseError;

public class StructOutput extends Variable {
	
	public StructOutput(Pipe defaultPipe) {
		super(defaultPipe);
	}
	
	public StructOutput(String expression, StructInput[] inParams, int lineN) {
		super(Pipe.fromExpression(expression, inParams, false, lineN));
		ParseError.throwIf(isAbstract() && !isImplicit(), lineN, "Output parameter " + getLabel() + " must be fully defined");
	}
}
