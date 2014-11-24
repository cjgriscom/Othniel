package com.quirkygaming.othniel;

import java.util.ArrayList;

import com.quirkygaming.othniel.Keywords.ExecutionMode;
import com.quirkygaming.othniel.Keywords.RunMode;
import com.quirkygaming.othniel.pipes.Terminal;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.PipeDef;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;
import com.quirkygaming.othniel.pipes.UndefinedPipe;

public class Structure extends Callable implements PipeOwner {
	
	ArrayList<CachedCall> callList = new ArrayList<CachedCall>();
	
	private ExecutionMode em;
	private RunMode rm;
	private StructInput[] inputs;
	private StructOutput[] outputs;
	
	private final PipeMap pipeDefs = new PipeMap();
	
	public Structure(ExecutionMode em, RunMode rm, StructInput[] inputs, StructOutput[] outputs, String name, int lineN) {
		super(name, inputs, outputs);
		this.em = em;
		this.rm = rm;
		ParseError.validate(rm == RunMode.SEQUENTIAL, lineN, "Parallel execution not yet implemented");
		this.inputs = inputs;
		this.outputs = outputs;
		for (Terminal pd : inputs) {
			ParseError.throwIf(pd.isAbstract() && isStatic(), lineN, "Abstract or implicit types not allowed in static methods");
			ParseError.throwIf(pipeDefs.containsKey(pd.getLabel()), lineN, "Duplicate pipe symbol: " + pd.getLabel());
			pipeDefs.put(pd.getLabel(), pd);
		}
		for (Terminal pd : outputs) {
			ParseError.throwIf(pd.isAbstract() && isStatic(), lineN, "Abstract or implicit types not allowed in static methods");
			ParseError.throwIf(pipeDefs.containsKey(pd.getLabel()), lineN, "Duplicate pipe symbol: " + pd.getLabel());
			pipeDefs.put(pd.getLabel(), pd);
		}
	}
	
	public boolean isStatic() {return this.em == ExecutionMode.STATIC;}
	public boolean isInstantiated() {return this.em == ExecutionMode.INSTANTIATED;}
	public boolean isInline() {return this.em == ExecutionMode.INLINE;}
	
	private boolean initializedIns = false;
	private Pipe[] runtimeIns;
	private boolean initializedOuts = false;
	private Pipe[] runtimeOuts;
	
	public Pipe[] getIns(Pipe[] ins, CachedCall c) {
		if (isInline()) {
			this.initializedIns = c.inlineInitiated(); // Check if the specific inline instance is initialized
			this.initializedOuts = c.inlineInitiated();
			if (c.inlineInitiated()) { // If it is, use its current values for this runtime.
				this.runtimeIns = c.getInlineIns();
				this.runtimeOuts = c.getInlineOuts();
			}
		}
		
		if (isInstantiated() || !initializedIns) { // Static/inline should only init once
			runtimeIns = new Pipe[this.inSize()];
			for (int i = 0; i < this.inSize(); i++) {
				if (ins[i] != null) {
					runtimeIns[i] = ins[i].copy(c); // Copy the input
				} else {
					runtimeIns[i] = inputs[i].getCopy(c);
				}
			}
			initializedIns = true;
			c.setInlineIns(runtimeIns); // If it's inline
		} else {
			for (int i = 0; i < this.inSize(); i++) {
				if (ins[i] != null) runtimeIns[i].set(ins[i], c.getLine());
			}
		}
		
		return runtimeIns;
	}
	
	public Pipe[] getOuts(Pipe[] outs, CachedCall c) {
		if (isInstantiated() || !initializedOuts) { // Static/inline should only init once
			runtimeOuts = new Pipe[outSize()];
			for (int i = 0; i < outSize(); i++) {
				runtimeOuts[i] = outputs[i].getCopy(c);
			}
			initializedOuts = true;
			c.setInlineOuts(runtimeOuts); // If it's inline
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
			innerCall.resetRuntime(this);
			
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

	@Override
	public PipeMap pipeDefs() {
		return pipeDefs;
	}

}
