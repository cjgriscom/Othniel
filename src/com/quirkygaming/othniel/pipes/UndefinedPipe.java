package com.quirkygaming.othniel.pipes;

public class UndefinedPipe extends Pipe {
	
	public UndefinedPipe(PipeDef p) {
		super(p.getLabel(), p.type());
	}
	
	@Override
	public void set(Pipe otherPipe) {
		throw new RuntimeException("Error"); //TODO
	}

	@Override
	public String toString() {
		return "UNDEFINED PIPE";
	}

}
