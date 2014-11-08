package com.quirkygaming.othniel;

import java.util.HashMap;

import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;


public abstract class Callable {
	
	static HashMap<String, Callable> callList = new HashMap<String, Callable>();
	
	public StructInput[] ins;
	public StructOutput[] outs;
	private final String name;
	
	public Callable(String name, StructInput[] ins, StructOutput[] outs) {
		this.ins = ins;
		this.outs = outs;
		this.name = name;
		callList.put(name, this);
	}
	
	public static Callable getCallable(String callable) {
		return callList.get(callable);
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
