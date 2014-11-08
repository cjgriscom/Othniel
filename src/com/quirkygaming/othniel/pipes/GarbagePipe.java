package com.quirkygaming.othniel.pipes;

public class GarbagePipe extends UndefinedPipe {
	
	public static GarbagePipe INSTANCE = new GarbagePipe();
	
	private GarbagePipe() {
		super("^", false);
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
