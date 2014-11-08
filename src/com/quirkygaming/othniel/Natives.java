package com.quirkygaming.othniel;

import java.util.Scanner;

import com.quirkygaming.othniel.CompOps.COp;
import com.quirkygaming.othniel.MathOps.Op;
import com.quirkygaming.othniel.pipes.BoolPipe;
import com.quirkygaming.othniel.pipes.NumericPipe;
import com.quirkygaming.othniel.pipes.NumericPipe.*;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StringPipe;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;
import com.quirkygaming.othniel.pipes.UndefinedPipe;

public class Natives {
	
	private static Scanner in = new Scanner(System.in);
	
	static void initNatives() {
		new Input("INPUT");
		new InputLine("INPUTLN");
		new Print(false, "PRINT");
		new Print(true, "PRINTLN");
		new Assign(":");
		new Xor("XOR");
		new And("AND");
		new Or("OR");
		new Not("NOT");
		new Ternary("?:");
		new MathOp("+", Op.ADD);
		new MathOp("-", Op.SUBTRACT);
		new MathOp("*", Op.MULTIPLY);
		new MathOp("/", Op.DIVIDE);
		new MathOp("%", Op.MOD);
		new CompOp("=", COp.EQUAL);
		new CompOp("!=", COp.NOTEQUAL);
		new CompOp("<=", COp.LESSEQ);
		new CompOp(">=", COp.GREATEREQ);
		new CompOp("<", COp.LESS);
		new CompOp(">", COp.GREATER);
		
		Interpreter.cacheFile("Natives.othsrc").values(); // Load natives file
	}
	
	abstract static class Native extends Callable {
		
		CachedCall c;
		
		public Native(String name, StructInput[] ins, StructOutput[] outs) {
			super(name, ins, outs);
		}
		
		public Native(String name, StructInput[] ins, StructOutput[] outs, boolean inputsArbitrary) {
			super(name, ins, outs, inputsArbitrary);
		}
		
		@Override
		public final void call(Pipe[] runtimeIns, Pipe[] runtimeOuts, CachedCall c) {
			// Loop through ins and outs and if any are defined implicitly, verify their type correctness
			for (int i = 0; i < ins.length; i++) {
				Pipe reqType = ins[i].definition();
				if (reqType.isImplicit()) reqType = ins[i].getImplicitReference(c);
				else continue;
				Pipe.checkCompat(runtimeIns[i], reqType, c.getLine());
			}
			for (int i = 0; i < outs.length; i++) {
				Pipe reqType = outs[i].definition();
				if (reqType.isImplicit()) reqType = outs[i].getImplicitReference(c);
				else continue;
				Pipe.checkCompat(reqType, runtimeOuts[i], c.getLine()); // TODO verify order
			}
			this.c = c;
			call(runtimeIns,runtimeOuts); // Forward to actual natives
		}
		
		public abstract void call(Pipe[] ins, Pipe[] outs);
		
	}
	
	static class Input extends Native {
		public Input(String name) {
			super(	name,
					new StructInput[]{new StructInput(new UndefinedPipe("inputType", Datatype.Anything))},
					new StructOutput[]{new StructOutput(new UndefinedPipe("result", 0, Datatype.Anything))});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			if (ins[0] instanceof I8Pipe) {
				((NumericPipe.I8Pipe)outs[0]).value = in.nextByte();
			} else if (ins[0] instanceof I16Pipe) {
				((NumericPipe.I16Pipe)outs[0]).value = in.nextShort();
			} else if (ins[0] instanceof I32Pipe) {
				((NumericPipe.I32Pipe)outs[0]).value = in.nextInt();
			} else if (ins[0] instanceof I64Pipe) {
				((NumericPipe.I64Pipe)outs[0]).value = in.nextLong();
			} else if (ins[0] instanceof SinglePipe) {
				((NumericPipe.SinglePipe)outs[0]).value = in.nextFloat();
			} else if (ins[0] instanceof DoublePipe) {
				((NumericPipe.DoublePipe)outs[0]).value = in.nextDouble();
			} else if (ins[0] instanceof StringPipe) {
				((StringPipe)outs[0]).value = in.next();
			} else if (ins[0] instanceof BoolPipe) {
				((BoolPipe)outs[0]).value = in.nextBoolean();
			} else {
				RuntimeError.throwIf(true, c.getLine(), ins[0].type() + " not supported for INPUT");
			}
		}
	}
	static class InputLine extends Native {
		public InputLine(String name) {
			super(	name,
					new StructInput[0],
					new StructOutput[]{new StructOutput(new StringPipe("result"))});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			((StringPipe)outs[0]).value = in.nextLine();
		}
	}
	static class Print extends Native {
		boolean newLine;
		public Print(boolean newLine, String name) {
			super(	name,
					new StructInput[]{new StructInput(new UndefinedPipe("value", Datatype.Anything))},
					new StructOutput[0],
					true); // Arbitrary inputs
			this.newLine = newLine;
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			for (Pipe p : ins) {
				System.out.print(p);
			}
			if (newLine) System.out.println();
		}
	}
	static class Assign extends Native {
		public Assign(String name) {
			super(	name,
					new StructInput[]{new StructInput(new UndefinedPipe("source", Datatype.Anything))},
					new StructOutput[]{new StructOutput(new UndefinedPipe("destination", Datatype.Anything))});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			outs[0].set(ins[0], c.getLine());
		}
	}
	static class Xor extends Native {
		public Xor(String name) {
			super(	name,
					new StructInput[]{new StructInput(new BoolPipe("a")), new StructInput(new BoolPipe("b"))},
					new StructOutput[]{new StructOutput(new BoolPipe("c"))});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			((BoolPipe)ins[0]).xor((BoolPipe)ins[1], (BoolPipe)outs[0]);
		}
	}
	static class And extends Native {
		public And(String name) {
			super(	name,
					new StructInput[]{new StructInput(new BoolPipe("a")), new StructInput(new BoolPipe("b"))},
					new StructOutput[]{new StructOutput(new BoolPipe("c"))});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			((BoolPipe)ins[0]).and((BoolPipe)ins[1], (BoolPipe)outs[0]);
		}
	}
	static class Or extends Native {
		public Or(String name) {
			super(	name,
					new StructInput[]{new StructInput(new BoolPipe("a")), new StructInput(new BoolPipe("b"))},
					new StructOutput[]{new StructOutput(new BoolPipe("c"))});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			((BoolPipe)ins[0]).or((BoolPipe)ins[1], (BoolPipe)outs[0]);
		}
	}
	static class Not extends Native {
		public Not(String name) {
			super(	name,
					new StructInput[]{new StructInput(new BoolPipe("a"))},
					new StructOutput[]{new StructOutput(new BoolPipe("b"))});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			((BoolPipe)ins[0]).not((BoolPipe)outs[0]);
		}
	}
	static class Ternary extends Native {
		public Ternary(String name) {
			super(	name,
					new StructInput[]{new StructInput(new BoolPipe("condition")), 
					new StructInput(new UndefinedPipe("trueResult", Datatype.Anything)), 
					new StructInput(new UndefinedPipe("falseResult", 1, Datatype.Anything))},
					new StructOutput[]{new StructOutput(new UndefinedPipe("result", 1, Datatype.Anything))});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			boolean condition = ((BoolPipe)ins[0]).value;
			outs[0].set(condition ? ins[1] : ins[2], c.getLine());
		}
	}
	
	static class MathOp extends Native {
		final Op op;
		public MathOp(String name, Op op) {
			super(	name,
					new StructInput[]{
					new StructInput(new UndefinedPipe("a", Datatype.Numeric)),
					new StructInput(new UndefinedPipe("b", Datatype.Numeric))},
					new StructOutput[]{new StructOutput(new UndefinedPipe("result", 0, Datatype.Numeric))});
			this.op = op;
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			try {
				if (((NumericPipe)ins[0]).isStrongerThan((NumericPipe)ins[1])) {
					((NumericPipe) ins[0]).op(op, true, (NumericPipe)ins[1], (NumericPipe)outs[0]);
				} else {
					((NumericPipe) ins[1]).op(op, false, (NumericPipe)ins[0], (NumericPipe)outs[0]);
				}
			} catch (ArithmeticException e) {
				RuntimeError.throwIf(true, c.getLine(), e.getMessage());
			}
		}
	}
	static class CompOp extends Native {
		final COp op;
		public CompOp(String name, COp op) {
			super(	name,
					new StructInput[]{
					new StructInput(new UndefinedPipe("a", Datatype.Numeric)),
					new StructInput(new UndefinedPipe("b", Datatype.Numeric))},
					new StructOutput[]{new StructOutput(new BoolPipe("result"))});
			this.op = op;
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			if (((NumericPipe)ins[0]).isStrongerThan((NumericPipe)ins[1])) {
				 ((BoolPipe)outs[0]).value = ((NumericPipe) ins[0]).comp(op, true, (NumericPipe)ins[1]);
			} else {
				 ((BoolPipe)outs[0]).value = ((NumericPipe) ins[1]).comp(op, false, (NumericPipe)ins[0]);
			}
		}
	}
}
