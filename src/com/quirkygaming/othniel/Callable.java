package com.quirkygaming.othniel;

import java.util.HashMap;

import com.quirkygaming.othniel.Keywords.ConfNodeType;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;

public abstract class Callable {
	
	static HashMap<String, Callable> callList = new HashMap<String, Callable>();
	
	private StructInput[] ins;
	private boolean inputsArbitrary; // TODO automatic implicit reqs
	private ConfNodeType[] confNodes;
	private StructOutput[] outs;
	private final String name;
	
	public Callable(String name, StructInput[] ins, StructOutput[] outs, ConfNodeType[] confNodes) {
		this.ins = ins;
		this.outs = outs;
		this.name = name;
		this.confNodes = confNodes;
		callList.put(name, this);
	}
	
	public Callable(String name, StructInput[] ins, StructOutput[] outs) {
		this(name, ins, outs, new ConfNodeType[0]);
	}
	
	public Callable(String name, StructInput[] ins, StructOutput[] outs, boolean inputsArbitrary) {
		this(name, ins, outs, new ConfNodeType[0]);
		this.inputsArbitrary = inputsArbitrary; // For natives like PRINT that take multiple args
	}
	
	public Callable(String name, StructInput[] ins, StructOutput[] outs, ConfNodeType[] confNodes, boolean inputsArbitrary) {
		this(name, ins, outs, confNodes);
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
	public int confNodes() {
		return confNodes.length;
	}
	
	public boolean inputsArbitrary() {
		return inputsArbitrary;
	}
	
	public abstract boolean isStatic();
	public abstract boolean isInstantiated();
	public abstract boolean isInline();
	
	public abstract void call(Pipe[] ins, Pipe[] outs, CachedCall c);
}
