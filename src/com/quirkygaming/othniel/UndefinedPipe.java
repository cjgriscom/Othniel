package com.quirkygaming.othniel;

public class UndefinedPipe extends Pipe {
	
	public UndefinedPipe(PipeDef p) {
		super(p.getLabel(), p.type(), false);
		
	}
	
	@Override
	public void set(Object object) {
		throw new RuntimeException("Todo"); //TODO
	}
	
	@Override
	public Object get() {
		throw new RuntimeException("Todo");
	}

}
