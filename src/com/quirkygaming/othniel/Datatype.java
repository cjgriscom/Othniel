package com.quirkygaming.othniel;

import java.util.HashMap;

public class Datatype {

	static final HashMap<String, Datatype> knownTypes = new HashMap<String, Datatype>();
	public static final Datatype I8 = new Datatype("I8", Byte.class, true);
	public static final Datatype I16 = new Datatype("I16", Short.class, true);
	public static final Datatype I32 = new Datatype("I32", Integer.class, true);
	public static final Datatype I64 = new Datatype("I64", Long.class, true);
	public static final Datatype Single = new Datatype("Single", Float.class, true);
	public static final Datatype Double = new Datatype("Double", Double.class, true);
	public static final Datatype Bool = new Datatype("Bool", Boolean.class, false);
	public static final Datatype String = new Datatype("String", String.class, false);
	public static final Datatype Anything = new Datatype("Anything", Object.class, false, true);
	public static final Datatype Numeric = new Datatype("Numeric", Object.class, true, true);
	
	final String ID; final Class<?> clazz;
	
	private int inputIndex;
	private int[] inputIndices;
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
	
	private Datatype(int inputIndex) { // Implicit constructor 
		ID = null; clazz = null;
		this.inputIndex = inputIndex;
		this.isAbstract = true;
	}
	
	private Datatype(int[] numIndices) { // Implicit constructor for numeric strengths
		this(-1);
		this.inputIndices = numIndices;
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
	
	public Datatype getImplicitType(String label, CachedCall call) {
		if (isImplicit()) {
			int indexToUse = inputIndex;
			if (indexToUse == -1) {
				// Determine strongest numeric type
				Datatype strongest = I8; // I8 is the weakest type besides the unimplemented U8
				for (int i : inputIndices) {
					if (call.ins[i].type().isStrongerThan(strongest)) {
						strongest = call.ins[i].type();
						inputIndex = i;
					}
				}
			}
			return call.ins[inputIndex].type();
		}
		return this;
	}
	
	public Pipe newPipe(String label, CachedCall call) {
		return new Pipe(label, getImplicitType(label, call));
	}
	
	public boolean isStrongerThan(Datatype other) {
		return this.getRelStrength() > other.getRelStrength();
	}
	
	private int getRelStrength() {
		if (this.equals(Double)) return 100; // Floats beat integers
		if (this.equals(Single)) return 99;
		if (this.equals(I64)) return 70; // Larger ints beat smaller ints
		//if (this.equals(U64)) return 65; // Signed beats unsigned
		if (this.equals(I32)) return 60;
		//if (this.equals(U32)) return 55;
		if (this.equals(I16)) return 50;
		//if (this.equals(U16)) return 45;
		if (this.equals(I8)) return 40;
		return 0;
	}
	
	public static Datatype fromExpression(String exp, StructInput[] ins, boolean isInput, int lineN) {
		exp = exp.trim();
		if (knownTypes.containsKey(exp)) {
			return knownTypes.get(exp);
		} else if (ins != null && exp.startsWith("typeof ")) { // Where null would indicate typeof isn't allowed
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
	
	public static Datatype implicitNumStrength(int... inputIndices) {
		return new Datatype(inputIndices);
	}
	
	@Override
	public String toString() {
		return ID;
	}
	
	private static Object checkCompatInternal(Object object, Datatype fromT, Datatype toT, int lineN, boolean cast) {
		if (toT.isNumeric() && fromT.isNumeric()) {
			RuntimeError.validate(toT.isStrongerThan(fromT) || toT.equals(fromT), lineN, 
					"Cannot cast a number to a weaker type");
			if (!toT.equals(fromT) && cast) return MathOps.castNumber(object, fromT, toT);
		} else {
			RuntimeError.validate(toT.equals(fromT), lineN, 
					"Incompatible types: " + fromT + " and " + toT);
		}
		return object;
	}
	
	public static Object checkCompatAndCast(Object object, Datatype fromT, Datatype toT, int lineN) {
		return checkCompatInternal(object, fromT, toT, lineN, true);
	}
	
	public static void checkCompat(Datatype fromT, Datatype toT, int lineN) {
		checkCompatInternal(null, fromT, toT, lineN, false);
	}
	
}
