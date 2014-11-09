package com.quirkygaming.othniel;

import java.util.HashMap;

import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;


public abstract class Callable {
	
	static HashMap<String, Callable> callList = new HashMap<String, Callable>();
	
	private StructInput[] ins;
	private boolean inputsArbitrary; // TODO automatic implicit reqs
	private StructOutput[] outs;
	private final String name;
	
	public Callable(String name, StructInput[] ins, StructOutput[] outs) {
		this.ins = ins;
		this.outs = outs;
		this.name = name;
		callList.put(name, this);
	}
	
	public Callable(String name, StructInput[] ins, StructOutput[] outs, boolean inputsArbitrary) {
		this(name, ins, outs);
		this.inputsArbitrary = inputsArbitrary; // For natives like PRINT that take multiple args
	}
	
	public static Callable getCallable(String callable) {
		return callList.get(callable);
	}
	
	public StructInput getIn(int index) {
		if (inputsArbitrary) {
			return ins[0];
		} else {
			return ins[index];
		}
	}
	
	public StructOutput getOut(int index) {
		return outs[index];
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
	
	public boolean inputsArbitrary() {
		return inputsArbitrary;
	}
	
	public abstract void call(Pipe[] ins, Pipe[] outs, CachedCall c);
}
