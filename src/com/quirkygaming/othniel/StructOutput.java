package com.quirkygaming.othniel;

public class StructOutput extends PipeDef {
	public final String label;
	public final Datatype type;
	
	public StructOutput(String expression, StructInput[] inParams, int lineN) {
		expression = expression.trim();
		int colonPos = expression.indexOf(':');
		ParseError.validate(colonPos > -1, lineN, "Expected (label):(type)");
		label = expression.substring(0, colonPos);
		type = Datatype.fromExpression(expression.substring(colonPos + 1), inParams, false, lineN);	
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