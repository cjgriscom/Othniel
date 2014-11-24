package com.quirkygaming.othniel;

import com.quirkygaming.othniel.confnodes.ConfNode;
import com.quirkygaming.othniel.pipes.GarbagePipe;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.PipeDef;

public class CachedCall {
	public PipeDef[] externalIns;
	public PipeDef[] externalOuts;
	public Pipe[] instanceIns;
	public Pipe[] instanceOuts;
	private boolean initialized = false;
	public ConfNode[] confNodes;
	public Callable call;
	private int lineN;
	public PipeOwner parent;
	
	public CachedCall(PipeDef[] ins, Callable call, ConfNode[] confNodes, PipeDef[] outs, int lineN, PipeOwner parent) {
		this.externalIns = ins;
		this.externalOuts = outs;
		this.instanceIns = new Pipe[ins.length];
		this.instanceOuts = new Pipe[outs.length];
		this.call = call;
		this.lineN = lineN;
		this.confNodes = confNodes;
		this.parent = parent;
	}

	public void call() {
		// Set runtime
		
		if (call.isStatic()) {
			this.initialized = call.staticInitialized;
			call.staticInitialized = true;
		}
		
		if (!initialized) {
			for (int i = 0; i < instanceIns.length; i++) {
				if (call.getIn(i).isAbstract()) {
					instanceIns[i] = Datatype.instantiatePipe(
							call.getIn(i).getLabel(), 
							externalIns[i].getRuntimePipe().type(), 
							lineN);
				} else {
					instanceIns[i] = call.getIn(i).getInternalPipe().copy(call.getIn(i).getLabel(), this);
				}
			}
			for (int i = 0; i < instanceOuts.length; i++) {
				if (call.getOut(i).isAbstract()) {
					if (externalOuts[i].getRuntimePipe() instanceof GarbagePipe) {
						//TODO maybe there's a more resourceful way of passing on the garbage pipe
						//This way just makes a new variable and loses the reference
						instanceOuts[i] = call.getOut(i).getImplicitReference(this).getInternalPipe().copy(call.getOut(i).getLabel(), this);
					} else {
						instanceOuts[i] = Datatype.instantiatePipe(
								call.getOut(i).getLabel(), 
								externalOuts[i].getRuntimePipe().type(), 
								lineN);
					}
				} else {
					instanceOuts[i] = call.getOut(i).getInternalPipe().copy(call.getOut(i).getLabel(), this);
				}
			}
		}
		
		if (call.isStatic() && initialized) {
			for (int i = 0; i < instanceIns.length; i++) {
				instanceIns[i] = call.getIn(i).getRuntimePipe();
			}
			for (int i = 0; i < instanceOuts.length; i++) {
				instanceOuts[i] = call.getOut(i).getRuntimePipe();
			}
		}
		
		if (!call.isInstantiated()) { // Instantiated should initialize every time
			initialized = true;
		}
		
		// Set in values
		for (int i = 0; i < instanceIns.length; i++) {
			if (externalIns[i] != null) instanceIns[i].set(externalIns[i].getRuntimePipe(), lineN);
			call.getIn(i).setInContext(instanceIns[i]);
		}
		for (int i = 0; i < instanceOuts.length; i++) {
			call.getOut(i).setInContext(instanceOuts[i]);
		}
		
		call.call(this);
		
		for (int i = 0; i < instanceOuts.length; i++) {
			externalOuts[i].getRuntimePipe().set(instanceOuts[i], lineN);
		}
	}
	
	public int getLine() {return lineN;}
}
