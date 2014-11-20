package com.quirkygaming.othniel.pipes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Constants;
import com.quirkygaming.othniel.Datatype;
import com.quirkygaming.othniel.ParseError;
import com.quirkygaming.othniel.RuntimeError;
import com.quirkygaming.othniel.pipes.NumericPipe.*;

public abstract class Pipe extends PipeDef {
	
	public static Pipe fromExpression(String expression, StructInput[] inParams, boolean isInput, int lineN) { // For StructInput and StructOutput
		//Syntax:  pipeName:I32 = 123
		//If isInput, the equal sign defines the input as optional
		
		expression = expression.trim();
		
		int colonPos = expression.indexOf(':');
		
		ParseError.validate(colonPos > 0, lineN, "Expected (label):(type)");
		String label = expression.substring(0, colonPos).trim();
		
		String datatypeS = expression.substring(colonPos + 1).trim();
		
		int equalPos = datatypeS.indexOf('=');
		
		if (equalPos != -1) {
			String defaultExp = datatypeS.substring(equalPos + 1).trim(); // Separate default value
			datatypeS = datatypeS.substring(0, equalPos).trim(); // and datatype
			
			Pipe constantPipe = Constants.matchConstant(defaultExp, lineN);
			ParseError.validate(constantPipe.type().toString().equals(datatypeS), lineN, 
					"Default value type does not match definition");
			
			constantPipe.label = label;
			
			return constantPipe;
		}
		
		if (datatypeS.equals("Double")) {
			return new DoublePipe(label);
		} else if (datatypeS.equals("Single")) {
			return new SinglePipe(label);
		} else if (datatypeS.equals("I64")) {
			return new I64Pipe(label);
		} else if (datatypeS.equals("I32")) {
			return new I32Pipe(label);
		} else if (datatypeS.equals("I16")) {
			return new I16Pipe(label);
		} else if (datatypeS.equals("I8")) {
			return new I8Pipe(label);
		} else if (datatypeS.equals("Bool")) {
			return new BoolPipe(label);
		} else if (datatypeS.equals("String")) {
			return new StringPipe(label);
		} else if (datatypeS.equals("Anything")) {
			return new UndefinedPipe(label, Datatype.Anything);
		} else if (datatypeS.equals("Numeric")) {
			return new UndefinedPipe(label, Datatype.Numeric);
		} else if (datatypeS.startsWith("typeof ")) {
			ParseError.validate(inParams != null, lineN, "typeof is not allowed in this context"); // for datatype constants
			ParseError.validate(equalPos == -1, lineN, "Defaults cannot be defined for implicit types"); 
			String implicitPipe = datatypeS.substring(7).trim();
			ParseError.validate(!implicitPipe.isEmpty(), lineN, "typeof requires a referenced pipe");
			for (int i = 0; i < inParams.length; i++) {
				String testPipe = inParams[i].getLabel();
				if (testPipe.trim().equals(implicitPipe.trim())) 
					return new UndefinedPipe(label, i, inParams[i].type()); // Search for a referenced pipe
			}
			ParseError.validate(false, lineN, "typeof expression could not be evaluated for " + implicitPipe);
		} else {
			ParseError.throwIf(!isInput, lineN, "Abstract types are not allowed in outputs; use typeof when possible:");
			ParseError.validate(equalPos == -1, lineN, "Defaults cannot be defined for abstract types"); 
			//TODO Abstract types; figure out how to deal with
		}
		
		ParseError.validate(false, lineN, "Could not evaluate Datatype expression " + datatypeS);
		return null;
	}
	
	protected Pipe(String label, Datatype type) {
		super(label, type);
	}
	
	public Pipe copy(CachedCall call) {
		Pipe p;
		if (this instanceof UndefinedPipe) {
			p = ((UndefinedPipe)this).getImplicitReference(call); // TODO move to undefinedpipe
		} else {
			p = this;
		}
		Pipe newPipe = null;
		
		if (p instanceof DoublePipe) newPipe = new NumericPipe.DoublePipe(label);
		else if (p instanceof SinglePipe) newPipe = new NumericPipe.SinglePipe(label);
		else if (p instanceof I64Pipe) newPipe = new NumericPipe.I64Pipe(label);
		else if (p instanceof I32Pipe) newPipe = new NumericPipe.I32Pipe(label);
		else if (p instanceof I16Pipe) newPipe = new NumericPipe.I16Pipe(label);
		else if (p instanceof I8Pipe) newPipe = new NumericPipe.I8Pipe(label);
		else if (p instanceof StringPipe) newPipe = new StringPipe(label);
		else if (p instanceof BoolPipe) newPipe = new BoolPipe(label);
		else throw new RuntimeException("newPipe could not instantiate the type " + p.type());
		
		newPipe.set(p);
		return newPipe;
	}

	abstract void set(Pipe otherPipe);
	
	public void set(Pipe otherPipe, int lineN) {
		checkCompat(otherPipe, this, lineN); // Validate compatibility/strength
		set(otherPipe);
	}
	
	public abstract boolean isAbstract();
	public boolean isNumeric() {
		return this instanceof NumericPipe;
	}
	
	@Override
	public abstract String toString();
	
	public static void checkCompat(Pipe fromT, Pipe toT, int lineN) {
		if (toT.isNumeric() && fromT.isNumeric()) {
			RuntimeError.validate(((NumericPipe)toT).isStrongerThan((NumericPipe)fromT) || toT.type() == fromT.type(), lineN, 
					"Cannot cast a number to a weaker type");
		} else {
			RuntimeError.validate(toT.type().equals(fromT.type()) || 
					toT instanceof GarbagePipe,
					lineN, 
					"Incompatible types: " + fromT.getLabel()+":"+fromT.type() + " and " + toT.getLabel()+":"+toT.type());
		}
	}
	
	public boolean isImplicit() {
		return false; // Overridden by UndefinedPipe
	}
	
}
