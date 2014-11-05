package com.quirkygaming.othniel;

public class UndefinedPipe extends Pipe {
	
	public UndefinedPipe(PipeDef p) {
		super(p.getLabel(), p.type(), false);
		
	}
	
	@Override
	public void set(Object object, Datatype pipeType, int lineN) {
		throw new RuntimeException("Error"); //TODO
	}
	
	@Override
	public Object get() {
		throw new RuntimeException("Error");
	}

}
