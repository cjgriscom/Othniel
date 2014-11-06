package com.quirkygaming.othniel;

public class Pipe extends PipeDef {
	private Object value;
	
	public Pipe(Pipe p, int lineN) {
		this(p.label, p.value, p.type, p.type, lineN);
	}
	
	public Pipe(String label, Datatype t) {
		this(label, t, true);
	}
	
	protected Pipe(String label, Datatype t, boolean throwExp) {
		super(label, t);
		
		if (t.isImplicit() && throwExp) throw new RuntimeException("Internal: Cannot directly create a pipe from an implicit datatype");
	}
	
	public Pipe(String label, Object value, Datatype t, Datatype pipeType, int lineN) {
		this(label, t);
		set(value, pipeType, lineN);
	}
	
	private void set(Object object) {
		this.value = object;
	}
	
	public void set(Object object, Datatype pipeType, int lineN) {
		object = Datatype.checkCompatAndCast(object, pipeType, type(), lineN);
		set(object);
	}
	
	public Object get() {
		return value;
	}
}
