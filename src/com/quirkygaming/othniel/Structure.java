package com.quirkygaming.othniel;

import java.util.ArrayList;
import java.util.HashMap;

public class Structure extends Callable {
	
	ArrayList<CachedCall> callList = new ArrayList<CachedCall>();
	
	private Type t;
	private StructInput[] inputs;
	private StructOutput[] outputs;
	
	public final HashMap<String, PipeDef> pipeDefs = new HashMap<String, PipeDef>();
	
	public Structure(Type t, StructInput[] inputs, StructOutput[] outputs, String name, int lineN) {
		super(name, getTypesFromStructNodeArray(inputs), getTypesFromStructNodeArray(outputs));
		this.t = t;
		this.inputs = inputs;
		this.outputs = outputs;
		for (PipeDef pd : inputs) {
			ParseError.throwIf(pd.type().isAbstract() && isStatic(), lineN, "Abstract or implicit types not allowed in static methods");
			pipeDefs.put(pd.getLabel(), pd);
		}
		for (PipeDef pd : outputs) {
			ParseError.throwIf(pd.type().isAbstract() && isStatic(), lineN, "Abstract or implicit types not allowed in static methods");
			pipeDefs.put(pd.getLabel(), pd);
		}
	}
	
	private boolean isStatic() {return this.t == Type.STATIC;}
	
	private static Datatype[] getTypesFromStructNodeArray(Object[] array) {
		Datatype[] typeArray = new Datatype[array.length];
		for (int i = 0; i < array.length; i++) {
			Object object = array[i];
			if (object instanceof StructInput) {
				typeArray[i] = ((StructInput) object).type;
			} else {
				typeArray[i] = ((StructOutput) object).type;
			}
		}
		return typeArray;
	}
	
	public enum Type {
		STATIC("static seq"), INLINE("inline seq");
		
		private String name;
		
		private Type(String s) {
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
					runtimeIns[i] = new Pipe(ins[i]); // Copy the input
				} else {
					runtimeIns[i] = inputs[i].type().newPipe(ins[i].getLabel(), c); // Otherwise get the default TODO defaults
				}
			}
			initializedIns = true;
		} else {
			for (int i = 0; i < this.ins.length; i++) {
				if (ins[i] != null) runtimeIns[i].set(ins[i].get());
			}
		}
		
		return runtimeIns;
	}
	
	public Pipe[] getOuts(Pipe[] outs, CachedCall c) {
		if (!isStatic() || !initializedOuts) { // Static should only init once
			runtimeOuts = new Pipe[this.outs.length];
			for (int i = 0; i < this.outs.length; i++) {
				runtimeOuts[i] = outputs[i].type().newPipe(outs[i].getLabel(), c); // get the default TODO defaults
				
			}
			initializedOuts = true;
		}
		return runtimeOuts;
	}
	
	public void copyResult(Pipe[] to) {
		for (int i = 0; i < to.length; i++) {
			to[i].set(runtimeOuts[i].get());
		}
	}

	@Override
	public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
		Pipe[] inputPipes = getIns(ins, c);
		Pipe[] outputPipes = getOuts(outs, c);
		
		for (CachedCall innerCall : callList) {
			if (this.t == Type.INLINE) innerCall.resetRuntime(); //TODO don't think this is right? maybe it is
			
			replaceWaits(innerCall.ins, true, inputPipes, outputPipes);
			replaceWaits(innerCall.outs, false, inputPipes, outputPipes);
			innerCall.call();
		}
		
		copyResult(outs);
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
