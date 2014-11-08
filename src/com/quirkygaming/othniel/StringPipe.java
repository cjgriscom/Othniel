package com.quirkygaming.othniel;

public class StringPipe extends Pipe {
	
	String value;
	
	protected StringPipe(String label) {
		super(label, Datatype.String);
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
}
