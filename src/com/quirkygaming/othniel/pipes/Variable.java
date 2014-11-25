package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Datatype;
import com.quirkygaming.othniel.ParseError;
import com.quirkygaming.othniel.RuntimeError;

public abstract class Variable extends PipeDef {
	protected Pipe currentPipe;
	protected Pipe defaultPipe;
	
	protected Variable(Pipe defaultPipe) {
		this.label = defaultPipe.getLabel();
		this.type = defaultPipe.type();
		this.defaultPipe = defaultPipe;
	}
	
	public void setInContext(Pipe contents) {
		this.currentPipe = contents;
	}

	@Override
	public Pipe getInternalPipe() {
		return defaultPipe;
	}

	@Override
	public Pipe getRuntimePipe(CachedCall c) {
		return currentPipe;
	}
	
	public PipeDef getImplicitReference(CachedCall c) {
		return ((UndefinedPipe)defaultPipe).getImplicitReference(c);
	}
	
	public boolean isAbstract() {
		return defaultPipe.isAbstract();
	}
	public boolean isImplicit() {
		return defaultPipe.isImplicit();
	}
	public boolean isNumeric() {
		return defaultPipe.isNumeric();
	}
	
	public void checkCompatWith(PipeDef spawnedPipe, int lineN, CachedCall c) {
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
