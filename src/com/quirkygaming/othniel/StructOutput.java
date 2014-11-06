package com.quirkygaming.othniel;

public class StructOutput extends PipeDef {
	
	public StructOutput(String expression, StructInput[] inParams, int lineN) {
		super(expression, inParams, lineN);
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public Datatype type() {
		return type;
	}
	
}