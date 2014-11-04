package com.quirkygaming.othniel;


public abstract class Callable {
	
	protected Datatype[] ins;
	protected Datatype[] outs;
	private final String name;
	
	public Callable(String name, Datatype[] ins, Datatype[] outs) {
		this.ins = ins;
		this.outs = outs;
		this.name = name;
	}
	
	public final String name() {
		return name;
	}
	
	public int inSize() {
		return ins.length;
	}
	public int outSize() {
		return outs.length;
	}
	public int size() {
		return inSize() + outSize();
	}
	
	public abstract void call(Pipe[] ins, Pipe[] outs, CachedCall c);
}
