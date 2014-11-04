package com.quirkygaming.othniel;

public class StructInput extends PipeDef {
	public final String label;
	public final Datatype type;
	
	public StructInput(String expression, int lineN) {
		expression = expression.trim();
		//TODO optional params
		int colonPos = expression.indexOf(':');
		ParseError.validate(colonPos > -1, lineN, "Expected (label):(type)");
		label = expression.substring(0, colonPos);
		type = Datatype.fromExpression(expression.substring(colonPos + 1), null, true, lineN);	
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
