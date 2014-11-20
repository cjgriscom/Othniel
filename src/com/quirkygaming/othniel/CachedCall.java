package com.quirkygaming.othniel;

import com.quirkygaming.othniel.pipes.Pipe;

public class CachedCall {
	private Pipe[] defIns;
	private Pipe[] defOuts;
	public Pipe[] ins;
	public Pipe[] outs;
	private Callable call;
	private int lineN;
	
	public CachedCall(Pipe[] ins, Callable call, Pipe[] outs, int lineN) {
		this.defIns = ins;
		this.defOuts = outs;
		this.call = call;
		this.lineN = lineN;
		this.ins = ins;
		this.outs = outs;
	}

	public void call() {
		call.call(ins, outs, this);
	}
	
	public void resetRuntime(Structure parent) { // Must be called for instantiated structures before run
		if (!(call instanceof Structure) || ((Structure)call).isInstantiated()) { // Native or instantiated
			ins = defIns.clone();
			outs = defOuts.clone();
		}
		
	}
	
	public int getLine() {return lineN;}
}
