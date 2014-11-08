package com.quirkygaming.othniel;

public class GarbagePipe extends Pipe {
	
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
}
