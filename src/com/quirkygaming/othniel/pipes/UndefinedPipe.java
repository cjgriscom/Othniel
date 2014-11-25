package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Datatype;
import com.quirkygaming.othniel.confnodes.ConfPipeType;

public class UndefinedPipe extends Pipe {
	
	private int inputIndex = -2;
	private int[] inputIndices = null;
	private boolean waiting;
	private boolean confNode = false;
	
	public UndefinedPipe(String label, Datatype abstractType) {
		super(label, abstractType);
	}
	
	//public UndefinedPipe(Terminal p) {
	//	super(p.definition.getLabel(), p.definition.type());
	//	waiting = true;
	//}
	
	public UndefinedPipe(String label, int inputIndex, Datatype abstractType) { // Implicit constructor 
		super(label, abstractType);
		this.inputIndex = inputIndex;
	}

	public UndefinedPipe(String label, int index, Datatype abstractType, boolean fromConfNode) { // PipeType confNode constructor 
		this(label, index, abstractType);
		this.confNode = fromConfNode;
		
	}
	
	public UndefinedPipe(String label, int[] inputIndices, Datatype abstractType) {
		this(label, -1, abstractType);
		this.inputIndices = inputIndices;
	}
	
	@Override
	public void set(Pipe otherPipe) {
		throw new RuntimeException("Error"); //TODO
	}
	
	public boolean isImplicit() {
		return inputIndex >= -1;
	}
	public boolean isNumeric() {
		return type.isNumeric();
	}
	
	public boolean isWaiting() {
		return waiting;
	}
	
	public PipeDef getImplicitReference(CachedCall call) {
		if (inputIndex == -2) throw new RuntimeException("Improper use of getImplicitReference in " + this.label);
		
		int indexToUse = inputIndex;
		if (indexToUse == -1) {
			// Determine strongest numeric type
			PipeDef strongest = null;
			for (int i : inputIndices) {
				if (strongest == null || ((NumericPipe)call.externalIns[i]).isStrongerThan((NumericPipe)strongest)) {
					strongest = call.externalIns[i];
					indexToUse = i;
				}
			}
		}
		if (confNode) {
			return Datatype.instantiatePipe("UNNAMED", 
					((ConfPipeType)call.confNodes[inputIndex]).getPipeType(), 
					call.getLine());
		} else {
			return call.externalIns[indexToUse];
		}
	}
	
	@Override
	public String toString() {
		return "UNDEFINED PIPE";
	}
	
	@Override
	public Pipe getRuntimePipe(CachedCall c) {
		throw new RuntimeException("ImplError: cannot check runtime of UndefinedPipe " + this.getLabel());
	}

	@Override
	public boolean isAbstract() {return true;}

}
