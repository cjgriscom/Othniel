package com.quirkygaming.othniel;

import com.quirkygaming.othniel.pipes.BoolPipe;
import com.quirkygaming.othniel.pipes.NumericPipe.DoublePipe;
import com.quirkygaming.othniel.pipes.NumericPipe.I16Pipe;
import com.quirkygaming.othniel.pipes.NumericPipe.I32Pipe;
import com.quirkygaming.othniel.pipes.NumericPipe.I64Pipe;
import com.quirkygaming.othniel.pipes.NumericPipe.I8Pipe;
import com.quirkygaming.othniel.pipes.NumericPipe.SinglePipe;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StringPipe;

public enum Datatype {
	Double, Single, I64, I32, I16, I8, 
	String, Bool, 
	Numeric, Anything;
	
	public boolean isNumeric() {
		return this != Datatype.Anything &&
				this != Datatype.Bool &&
				this != Datatype.String;
	}
	
	public static Pipe instantiatePipe(String name, Datatype t, int lineN) {
		switch (t) {
		case Bool:
			return new BoolPipe(name);
		case Double:
			return new DoublePipe(name);
		case I16:
			return new I16Pipe(name);
		case I32:
			return new I32Pipe(name);
		case I64:
			return new I64Pipe(name);
		case I8:
			return new I8Pipe(name);
		case Single:
			return new SinglePipe(name);
		case String:
			return new StringPipe(name);
		default:
			ParseError.throwIf(true, lineN, "Could not instantiate type " + t);
			return null;
		}
	}
}
