package com.quirkygaming.othniel;

import com.quirkygaming.othniel.pipes.Pipe;

public class CachedCall {
	private Pipe[] defIns;
	private Pipe[] defOuts;
	private int inlineInit = 0;
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
	
	public boolean inlineInitiated() {
		return this.inlineInit == 2;
	}
	
	public void setInlineIns(Pipe[] ins) { // For setting specific occurrences of inline once initialized
		if (call.isInline()) {
			this.defIns = ins;
			inlineInit++;
		}
	}
	
	public void setInlineOuts(Pipe[] outs) { // For setting specific occurrences of inline once initialized
		if (call.isInline()) {
			this.defOuts = outs;
			inlineInit++;
		}
	}
	
	public Pipe[] getInlineIns() {
		return defIns;
	}
	
	public Pipe[] getInlineOuts() {
		return defOuts;
	}
	
	public void resetRuntime(Structure parent) { // Must be called for instantiated structures before run
		if (call.isInstantiated()) { // Native or instantiated
			ins = defIns.clone();
			outs = defOuts.clone();
		} else if (call.isInline()) {
			ins = defIns; //TODO neccesary?
			outs = defOuts;
		}
		
	}
	
	public int getLine() {return lineN;}
}
