package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.Datatype;

public class StringPipe extends Pipe {
	
	public String value;
	
	public StringPipe(String label) {
		super(label, Datatype.String);
		this.value = "";
	}

	public StringPipe(String label, String value) {
		this(label);
		this.value = value;
	}

	@Override
	void set(Pipe otherPipe) {
		this.value = ((StringPipe) otherPipe).value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean isAbstract() {return false;}
}
