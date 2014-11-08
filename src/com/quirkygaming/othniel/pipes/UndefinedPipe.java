package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Datatype;

public class UndefinedPipe extends Pipe {
	
	String abstractType;
	private int inputIndex = -2;
	private int[] inputIndices = null;
	private boolean isNumeric;
	
	public UndefinedPipe(String label, boolean isNumeric) {
		super(label, isNumeric ? Datatype.Numeric : Datatype.Anything);
		this.isNumeric = isNumeric;
	}
	
	public UndefinedPipe(PipeDef p) {
		this(p.getLabel(), false); //TODO verify
	}
	
	public UndefinedPipe(String label, int inputIndex) { // Implicit constructor 
		super(label, Datatype.Implicit);
		this.inputIndex = inputIndex;
	}
	
	public UndefinedPipe(String label, int[] inputIndices) {
		this(label, -1);
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
		return isNumeric;
	}

	@Override
	public String toString() {
		return "UNDEFINED PIPE";
	}

	@Override
	public boolean isAbstract() {return true;}
	
	public Pipe getImplicitType(String label, CachedCall call) {
		if (isImplicit()) {
			int indexToUse = inputIndex;
			if (indexToUse == -1) {
				// Determine strongest numeric type
				Pipe strongest = null;
				for (int i : inputIndices) {
					if (strongest == null || ((NumericPipe)call.ins[i]).isStrongerThan((NumericPipe)strongest)) {
						strongest = call.ins[i];
						inputIndex = i;
					}
				}
			}
			Pipe p = call.ins[inputIndex];
			p.label = label;
			return p;
		}
		return this;
	}

}
