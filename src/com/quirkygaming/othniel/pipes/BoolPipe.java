package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.Datatype;

public class BoolPipe extends Pipe {
	
	public boolean value;
	
	public BoolPipe(String label) {
		super(label, Datatype.Bool);
	}

	public BoolPipe(String label, boolean value) {
		this(label);
		this.value = value;
	}

	@Override
	void set(Pipe otherPipe) {
		this.value = ((BoolPipe) otherPipe).value;
	}

	@Override
	public String toString() {
		return "" + value;
	}
	
	public void and(BoolPipe other, BoolPipe target) {target.value = value && other.value;}
	public void or(BoolPipe other, BoolPipe target) {target.value = value || other.value;}
	public void xor(BoolPipe other, BoolPipe target) {target.value = value ^ other.value;}
	public void not(BoolPipe target) {target.value = !value;}

	@Override
	public boolean isAbstract() {return false;}
}
