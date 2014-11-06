package com.quirkygaming.othniel;

public class GarbagePipe extends Pipe {
	
	public static GarbagePipe INSTANCE = new GarbagePipe();
	
	private GarbagePipe() {
		super("^", Datatype.Anything, false);
	}
	
	public Object get() {
		throw new RuntimeException("Someone tried to get the value of the GarbagePipe...");
	}
	
	public void set(Object object, Datatype pipeType, int lineN) { // Absorb set
		return;
	}
}
