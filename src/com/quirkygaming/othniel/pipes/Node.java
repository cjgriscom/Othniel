package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Datatype;
import com.quirkygaming.othniel.ParseError;
import com.quirkygaming.othniel.RuntimeError;

public abstract class Node extends PipeDef { // TODO rename to Terminal
	protected Pipe definition;
	
	protected Node(Pipe definition) {
		this.definition = definition;
		this.label = definition.label; //TODO correct?
		this.type = definition.type;
	}
	
	public Pipe getCopy(CachedCall c) {
		return definition.copy(c);
	}
	
	public Pipe getCopy(String newLabel, CachedCall c) {
		Pipe p = definition.copy(c);
		p.label = newLabel;
		return p;
	}
	
	public Pipe definition() {
		return definition;
	}
	
	public Pipe getImplicitReference(CachedCall c) {
		return ((UndefinedPipe)definition).getImplicitReference(c);
	}
	
	public boolean isAbstract() {
		return definition.isAbstract();
	}
	public boolean isImplicit() {
		return definition.isImplicit();
	}
	public boolean isNumeric() {
		return definition.isNumeric();
	}
	
	public void checkCompatWith(Pipe spawnedPipe, int lineN, CachedCall c) {
		if (spawnedPipe instanceof GarbagePipe) return;
		
		Datatype spawnType = spawnedPipe.type();
		
		PipeDef nodeInstance = this;
		if (this.isImplicit()) nodeInstance = this.getImplicitReference(c);
		
		if (nodeInstance.isNumeric()) {
			ParseError.validate(spawnedPipe.isNumeric(), lineN, spawnedPipe.getLabel() + " must be numeric");
		} else if (!this.isAbstract()) {
			RuntimeError.validate(nodeInstance.type() == spawnType,
					lineN, 
					"Incompatible types: " + nodeInstance.type() + " and " + spawnedPipe.type());
		}
	}
}
