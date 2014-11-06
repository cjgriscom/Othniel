package com.quirkygaming.othniel;

public abstract class PipeDef {
	protected final String label;
	protected final Datatype type;
	
	protected PipeDef(String label, Datatype type) {
		this.label = label;
		this.type = type;
	}
	
	protected PipeDef(String expression, StructInput[] inParams, int lineN) { // For StructInput and StructOutput
		boolean isInput = this instanceof StructInput;
		
		expression = expression.trim();
		//TODO optional parameters for input
		int colonPos = expression.indexOf(':');
		ParseError.validate(colonPos > -1, lineN, "Expected (label):(type)");
		label = expression.substring(0, colonPos);
		type = Datatype.fromExpression(expression.substring(colonPos + 1), inParams, lineN);	
	}
	
	public String getLabel() {
		return label;
	}
	
	public Datatype type() {
		return type;
	}
}
