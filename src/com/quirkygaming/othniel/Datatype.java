package com.quirkygaming.othniel;

import java.util.HashMap;

import com.quirkygaming.othniel.pipes.BoolPipe;
import com.quirkygaming.othniel.pipes.NumericPipe;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StringPipe;
import com.quirkygaming.othniel.pipes.StructInput;

public class Datatype {

	static final HashMap<String, Datatype> knownTypes = new HashMap<String, Datatype>();
	public static final Datatype I8 = new Datatype("I8", true);
	public static final Datatype I16 = new Datatype("I16", true);
	public static final Datatype I32 = new Datatype("I32", true);
	public static final Datatype I64 = new Datatype("I64", true);
	public static final Datatype Single = new Datatype("Single", true);
	public static final Datatype Double = new Datatype("Double", true);
	public static final Datatype Bool = new Datatype("Bool", false);
	public static final Datatype String = new Datatype("String", false);
	public static final Datatype Anything = new Datatype("Anything", false, true);
	public static final Datatype Numeric = new Datatype("Numeric", true, true);
	
	final String ID;
	
	private int inputIndex = -2;
	private int[] inputIndices;
	private boolean isNumeric;
	private boolean isAbstract = false;
	
	protected Datatype(String ID, boolean isNumeric, boolean isAbstract) {
		this.ID = ID;
		this.isNumeric = isNumeric;
		this.isAbstract = isAbstract;
		knownTypes.put(ID, this);
	}
	
	protected Datatype(String ID, boolean isNumeric) {
		this.ID = ID;
		this.isNumeric = isNumeric;
		knownTypes.put(ID, this);
	}
	
	private Datatype(int inputIndex) { // Implicit constructor 
		ID = null;
		this.inputIndex = inputIndex;
		this.isAbstract = true;
	}
	
	private Datatype(int[] numIndices) { // Implicit constructor for numeric strengths
		this(-1);
		this.inputIndices = numIndices;
	}
	
	public boolean isImplicit() {
		return inputIndex >= -1;
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
		Datatype t = getImplicitType(label, call);
		if (t.equals(Double)) return new NumericPipe.DoublePipe(label);
		if (t.equals(Single)) return new NumericPipe.SinglePipe(label);
		if (t.equals(I64)) return new NumericPipe.I64Pipe(label);
		if (t.equals(I32)) return new NumericPipe.I32Pipe(label);
		if (t.equals(I16)) return new NumericPipe.I16Pipe(label);
		if (t.equals(I8)) return new NumericPipe.I8Pipe(label);
		if (t.equals(String)) return new StringPipe(label);
		if (t.equals(Bool)) return new BoolPipe(label);
		throw new RuntimeException("newPipe could not instantiate the type " + t);
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
	
	public static Datatype fromExpression(String exp, StructInput[] ins, int lineN) {
		exp = exp.trim();
		if (knownTypes.containsKey(exp)) {
			return knownTypes.get(exp);
		} else if (exp.startsWith("typeof ")) {
			ParseError.validate(ins != null, lineN, "typeof is not allowed in this context"); // for datatype constants
			String implicitPipe = exp.substring(7).trim();
			ParseError.validate(!implicitPipe.isEmpty(), lineN, "typeof requires a referenced pipe");
			for (int i = 0; i < ins.length; i++) {
				String testPipe = ins[i].getLabel();
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
	
	public static void checkCompat(Datatype fromT, Datatype toT, int lineN) {
		if (toT.isNumeric() && fromT.isNumeric()) {
			RuntimeError.validate(toT.isStrongerThan(fromT) || toT.equals(fromT), lineN, 
					"Cannot cast a number to a weaker type");
		} else {
			RuntimeError.validate(toT.equals(fromT) || 
					toT.equals(Anything), // Should only occur for GarbagePipe
					lineN, 
					"Incompatible types: " + fromT + " and " + toT);
		}
	}
	
}
