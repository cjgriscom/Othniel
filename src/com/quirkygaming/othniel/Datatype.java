package com.quirkygaming.othniel;

public enum Datatype {
	Double, Single, I64, I32, I16, I8, 
	String, Bool, 
	Numeric, Anything;
	
	public boolean isNumeric() {
		return this != Datatype.Anything &&
				this != Datatype.Bool &&
				this != Datatype.String;
	}
}
