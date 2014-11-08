package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.Datatype;

public class GarbagePipe extends UndefinedPipe {
	
	public static GarbagePipe INSTANCE = new GarbagePipe();
	
	private GarbagePipe() {
		super("^", Datatype.Anything);
	}
	
	public void set(Pipe otherPipe) { // Absorb set
		return;
	}

	@Override
	public String toString() {
		return "GARBAGE PIPE";
	}

	@Override
	public boolean isAbstract() {return true;}
}
