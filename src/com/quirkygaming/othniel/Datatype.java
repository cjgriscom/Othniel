package com.quirkygaming.othniel;

import java.util.HashMap;

public class Datatype {

	static final HashMap<String, Datatype> knownTypes = new HashMap<String, Datatype>();
	
	public static final Datatype I32 = new Datatype("I32", Integer.class, true);
	public static final Datatype Bool = new Datatype("Bool", Boolean.class, false);
	public static final Datatype String = new Datatype("String", String.class, false);
	public static final Datatype Anything = new Datatype("Anything", Object.class, false, true);
	public static final Datatype Numeric = new Datatype("Numeric", Object.class, true, true);
	
	final String ID; final Class<?> clazz;
	
	private int inputIndex;
	private boolean isNumeric;
	private boolean isAbstract = false;
	
	protected Datatype(String ID, Class<?> clazz, boolean isNumeric, boolean isAbstract) {
		this.ID = ID;
		this.clazz = clazz;
		this.isNumeric = isNumeric;
		this.isAbstract = isAbstract;
		knownTypes.put(ID, this);
	}
	
	protected Datatype(String ID, Class<?> clazz, boolean isNumeric) {
		this.ID = ID;
		this.clazz = clazz;
		this.isNumeric = isNumeric;
		knownTypes.put(ID, this);
	}
	
	private Datatype(int inputIndex) {
		ID = null; clazz = null;
		this.inputIndex = inputIndex;
		this.isAbstract = true;
	}
	
	public boolean isImplicit() {
		return clazz==null;
	}
	
	public boolean isNumeric() {
		return this.isNumeric;
	}
	
	public boolean isAbstract() {
		return this.isAbstract;
	}
	
	public Pipe newPipe(String label, CachedCall call) {
		if (isImplicit()) {
			return new Pipe(label, call.ins[inputIndex].type());
		}
		return new Pipe(label, this);
	}
	
	public static Datatype fromExpression(String exp, StructInput[] ins, boolean isInput, int lineN) {
		exp = exp.trim();
		if (knownTypes.containsKey(exp)) {
			return knownTypes.get(exp);
		} else if (exp.startsWith("typeof ")) {
			ParseError.validate(! isInput, lineN, "typeof cannot be applied to inputs");
			String implicitPipe = exp.substring(7).trim();
			ParseError.validate(!implicitPipe.isEmpty(), lineN, "typeof requires a referenced pipe");
			for (int i = 0; i < ins.length; i++) {
				String testPipe = ins[i].label;
				if (testPipe.trim().equals(implicitPipe.trim())) return implicit(i); // Search for a referenced pipe
			}
			ParseError.validate(false, lineN, "typeof expression could not be evaluated for " + implicitPipe);
		}
		//TODO more cases
		ParseError.validate(false, lineN, "Could not evaluate Datatype expression " + exp);
		return null;
	}
	
	public static Datatype implicit(int inputIndex) {
		return new Datatype(inputIndex);
	}
	
}
