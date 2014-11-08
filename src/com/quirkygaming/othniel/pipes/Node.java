package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.CachedCall;

public abstract class Node extends PipeDef {
	protected Pipe definition;
	
	protected Node(Pipe definition) {
		this.definition = definition;
		this.label = definition.label; //TODO correct?
		this.type = definition.type;
	}
	
	public Pipe getCopy(String newLabel, CachedCall c) {
		Pipe p = definition.copy(c);
		p.label = newLabel;
		System.err.println(this.type());
		return p;
	}
	
	public Pipe definition() {
		return definition;
	}
	
	public Pipe getImplicitType(String label, CachedCall c) {
		return ((UndefinedPipe)definition).getImplicitType(label, c);
	}
	
	public boolean isAbstract() {
		return definition.isAbstract();
	}
	public boolean isImplicit() {
		return definition.isImplicit();
	}
}
