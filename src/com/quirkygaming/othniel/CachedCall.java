package com.quirkygaming.othniel;

public class CachedCall {
	private Pipe[] defIns;
	private Pipe[] defOuts;
	Pipe[] ins;
	Pipe[] outs;
	private Callable call;
	private int line;
	
	public CachedCall(Pipe[] ins, Callable call, Pipe[] outs, int line) {
		this.defIns = ins;
		this.defOuts = outs;
		this.call = call;
		this.line = line;
		this.ins = ins;
		this.outs = outs; // TODO might not work... errrrg
	}

	public void call() {
		call.call(ins, outs, this);
	}
	
	public void resetRuntime() { // Must be called by structure before run
		ins = defIns.clone();
		outs = defOuts.clone();
	}
	
	public int getLine() {return line;}
}
