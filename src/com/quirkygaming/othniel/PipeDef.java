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
		
		int colonPos = expression.indexOf(':');
		ParseError.validate(colonPos > -1, lineN, "Expected (label):(type)");
		label = expression.substring(0, colonPos).trim();
		
		String datatypeS = expression.substring(colonPos + 1).trim();
		
		if (isInput) {
			//TODO optional parameters : datatypeS = ...
		}
		
		type = Datatype.fromExpression(datatypeS, inParams, lineN);	
		
		ParseError.throwIf(type.isAbstract() && !type.isImplicit() && !isInput, lineN, "Abstract types are not allowed in outputs; use typeof when possible:");
	}
	
	public String getLabel() {
		return label;
	}
	
	public Datatype type() {
		return type;
	}
}
