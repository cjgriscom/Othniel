package com.quirkygaming.othniel;

public class Pipe extends PipeDef {
	private String label;
	private Object value;
	private Datatype type;
	
	public Pipe(Pipe p) {
		this(p.label, p.value, p.type);
	}
	
	public Pipe(String label, Datatype t) {
		this(label, t, true);
	}
	
	protected Pipe(String label, Datatype t, boolean throwExp) {
		this.label = label;
		this.type = t;
		
		if (t.isImplicit() && throwExp) throw new RuntimeException("Internal: Cannot directly create a pipe from an implicit datatype");
	}
	
	public Pipe(String label, Object value, Datatype t) {
		this(label, t);
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void set(Object object) {
		this.value = object;
	}
	
	public Object get() {
		return value;
	}
	
	public Datatype type() {
		return type;
	}
}
