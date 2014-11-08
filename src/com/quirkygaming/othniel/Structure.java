package com.quirkygaming.othniel;

import java.util.ArrayList;
import java.util.HashMap;

import com.quirkygaming.othniel.pipes.Node;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.PipeDef;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;
import com.quirkygaming.othniel.pipes.UndefinedPipe;

public class Structure extends Callable {
	
	ArrayList<CachedCall> callList = new ArrayList<CachedCall>();
	
	private ExecutionMode em;
	private RunMode rm;
	private StructInput[] inputs;
	private StructOutput[] outputs;
	
	public final HashMap<String, PipeDef> pipeDefs = new HashMap<String, PipeDef>();
	
	public Structure(ExecutionMode em, RunMode rm, StructInput[] inputs, StructOutput[] outputs, String name, int lineN) {
		super(name, inputs, outputs);
		this.em = em;
		this.rm = rm;
		ParseError.validate(rm == RunMode.SEQUENTIAL, lineN, "Error: Parallel execution not yet implemented");
		this.inputs = inputs;
		this.outputs = outputs;
		for (Node pd : inputs) {
			ParseError.throwIf(pd.isAbstract() && isStatic(), lineN, "Abstract or implicit types not allowed in static methods");
			ParseError.throwIf(pipeDefs.containsKey(pd.getLabel()), lineN, "Duplicate pipe symbol: " + pd.getLabel());
			pipeDefs.put(pd.getLabel(), pd);
		}
		for (Node pd : outputs) {
			ParseError.throwIf(pd.isAbstract() && isStatic(), lineN, "Abstract or implicit types not allowed in static methods");
			ParseError.throwIf(pipeDefs.containsKey(pd.getLabel()), lineN, "Duplicate pipe symbol: " + pd.getLabel());
			pipeDefs.put(pd.getLabel(), pd);
		}
	}
	
	private boolean isStatic() {return this.em == ExecutionMode.STATIC;}
	
	public enum ExecutionMode {
		STATIC("static"), INLINE("inline");
		
		private String name;
		private ExecutionMode(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
	public enum RunMode {
		SEQUENTIAL("sequence"), PARALLEL("parallel");
		
		private String name;
		private RunMode(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
	private boolean initializedIns = false;
	private Pipe[] runtimeIns;
	private boolean initializedOuts = false;
	private Pipe[] runtimeOuts;
	
	public Pipe[] getIns(Pipe[] ins, CachedCall c) {
		if (!isStatic() || !initializedIns) { // Static should only init once
			runtimeIns = new Pipe[this.ins.length];
			for (int i = 0; i < this.ins.length; i++) {
				if (ins[i] != null) {
					runtimeIns[i] = ins[i].copy(c); // Copy the input
				} else {
					runtimeIns[i] = inputs[i].getCopy(ins[i].getLabel(), c); // Otherwise get the default TODO defaults
				}
			}
			initializedIns = true;
		} else {
			for (int i = 0; i < this.ins.length; i++) {
				if (ins[i] != null) runtimeIns[i].set(ins[i], c.getLine());
			}
		}
		
		return runtimeIns;
	}
	
	public Pipe[] getOuts(Pipe[] outs, CachedCall c) {
		if (!isStatic() || !initializedOuts) { // Static should only init once
			runtimeOuts = new Pipe[this.outs.length];
			for (int i = 0; i < this.outs.length; i++) {
				runtimeOuts[i] = outputs[i].getCopy(outs[i].getLabel(), c); // get the default TODO defaults
			}
			initializedOuts = true;
		}
		return runtimeOuts;
	}
	
	public void copyResult(Pipe[] to, CachedCall c) {
		for (int i = 0; i < to.length; i++) {
			to[i].set(runtimeOuts[i], c.getLine());
		}
	}

	@Override
	public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
		Pipe[] inputPipes = getIns(ins, c);
		Pipe[] outputPipes = getOuts(outs, c);
		
		//TODO if rm is PARALLEL, link calls and create threads
		
		for (CachedCall innerCall : callList) {
			if (this.em == ExecutionMode.INLINE) innerCall.resetRuntime(); //TODO don't think this is right? maybe it is
			
			replaceWaits(innerCall.ins, true, inputPipes, outputPipes);
			replaceWaits(innerCall.outs, false, inputPipes, outputPipes);
			innerCall.call();
		}
		
		copyResult(outs, c);
	}
	
	private void replaceWaits(Pipe[] pipes, boolean isInput, Pipe[] inputPipes, Pipe[] outputPipes) {
		for (int i = 0; i < pipes.length; i++) {
			if (pipes[i] instanceof UndefinedPipe) {
				boolean replaced = false;
				for (int j = 0; j < inputs.length; j++) {
					PipeDef p = inputs[j];
					if (p.getLabel().equals(pipes[i].getLabel())) {
						pipes[i] = inputPipes[j];
						replaced = true;
						break;
					}
				}
				if (replaced) continue;
				for (int j = 0; j < outputs.length; j++) {
					PipeDef p = outputs[j];
					if (p.getLabel().equals(pipes[i].getLabel())) {
						pipes[i] = outputPipes[j];
						break;
					}
				}
			}
		}
	}

}
