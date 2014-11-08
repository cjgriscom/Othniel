package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Datatype;

public abstract class Pipe extends PipeDef {
	
	protected Pipe(String label, Datatype type) {
		super(label, type);
	}
	
	public Pipe copy(CachedCall call) {
		Pipe newPipe = type().newPipe(getLabel(), call);
		newPipe.set(this);
		return newPipe;
	}

	abstract void set(Pipe otherPipe);
	
	public void set(Pipe otherPipe, int lineN) {
		Datatype.checkCompat(otherPipe.type(), this.type(), lineN); // Validate compatibility/strength
		set(otherPipe);
	}
	
	@Override
	public abstract String toString();
}
