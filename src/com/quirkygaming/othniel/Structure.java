package com.quirkygaming.othniel;

import java.util.ArrayList;

import com.quirkygaming.othniel.Keywords.ExecutionMode;
import com.quirkygaming.othniel.Keywords.RunMode;
import com.quirkygaming.othniel.pipes.Variable;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;

public class Structure extends Callable implements PipeOwner {
	
	ArrayList<CachedCall> callList = new ArrayList<CachedCall>();
	
	private ExecutionMode em;
	private RunMode rm;
	
	private final PipeMap pipeDefs = new PipeMap();
	
	public Structure(ExecutionMode em, RunMode rm, StructInput[] inputs, StructOutput[] outputs, String name, int lineN) {
		super(name, inputs, outputs);
		this.em = em;
		this.rm = rm;
		ParseError.validate(rm == RunMode.SEQUENTIAL, lineN, "Parallel execution not yet implemented");
		
		for (Variable pd : inputs) {
			ParseError.throwIf(pd.isAbstract() && isStatic(), lineN, "Abstract or implicit types not allowed in static methods");
			ParseError.throwIf(pipeDefs.containsKey(pd.getLabel()), lineN, "Duplicate pipe symbol: " + pd.getLabel());
			pipeDefs.put(pd.getLabel(), pd);
		}
		for (Variable pd : outputs) {
			ParseError.throwIf(pd.isAbstract() && isStatic(), lineN, "Abstract or implicit types not allowed in static methods");
			ParseError.throwIf(pipeDefs.containsKey(pd.getLabel()), lineN, "Duplicate pipe symbol: " + pd.getLabel());
			pipeDefs.put(pd.getLabel(), pd);
		}
	}
	
	public boolean isStatic() {return this.em == ExecutionMode.STATIC;}
	public boolean isInstantiated() {return this.em == ExecutionMode.INSTANTIATED;}
	public boolean isInline() {return this.em == ExecutionMode.INLINE;}
	
	@Override
	public void call(CachedCall c) {
		//TODO if rm is PARALLEL, link calls and create threads
		
		for (CachedCall innerCall : callList) {
			innerCall.call();
		}
	}
	
	@Override
	public PipeMap pipeDefs() {
		return pipeDefs;
	}

}
