package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Datatype;

public abstract class PipeDef {
	protected String label;
	protected Datatype type;
	
	protected PipeDef(String label, Datatype type) {
		this.label = label;
		this.type = type;
	}
	
	protected PipeDef() {}
	
	public String getLabel() {
		return label;
	}
	
	public Datatype type() {
		return type;
	}
	
	public abstract boolean isAbstract();
	public abstract boolean isImplicit();
	public abstract boolean isNumeric();
	
	public abstract Pipe getInternalPipe();
	public abstract Pipe getRuntimePipe(CachedCall c);
}
