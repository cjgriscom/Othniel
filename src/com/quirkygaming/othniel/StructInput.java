package com.quirkygaming.othniel;

public class StructInput extends PipeDef {
	
	public StructInput(String expression, StructInput[] knownInParams, int lineN) {
		super(expression, knownInParams, lineN);
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
