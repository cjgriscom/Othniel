package com.quirkygaming.othniel;

import java.util.Scanner;

import com.quirkygaming.othniel.CompOps.COp;
import com.quirkygaming.othniel.Keywords.ConfNodeType;
import com.quirkygaming.othniel.MathOps.Op;
import com.quirkygaming.othniel.confnodes.ConfLabel;
import com.quirkygaming.othniel.confnodes.ConfNode;
import com.quirkygaming.othniel.confnodes.ConfPipeType;
import com.quirkygaming.othniel.confnodes.StatementSet;
import com.quirkygaming.othniel.pipes.BoolPipe;
import com.quirkygaming.othniel.pipes.NumericPipe;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StringPipe;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;
import com.quirkygaming.othniel.pipes.UndefinedPipe;

public class Natives {
	
	private static Scanner in = new Scanner(System.in);
	
	static void initNatives() {
		new PipeExists("#PIPEEXISTS");
		new Exec("EXECUTE");
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
	
	static class PipeExists extends Directive {
		
		public PipeExists(String name) {
			super(name, new StructInput[0], 
					new StructOutput[]{new StructOutput(new BoolPipe("exists"))}, 
					new ConfNodeType[]{ConfNodeType.LABEL});
		}
		
		@Override
		public CachedCall directive(Pipe[] ins, Pipe[] outs, ConfNode[] confNodes, CachedCall c) {
			String label = ((ConfLabel)confNodes[0]).getLabel();
			BoolPipe result = new BoolPipe("PIPEEXISTS", c.parent.pipeDefs().containsKey(label));
			
			return new CachedCall(
					new Pipe[]{result}, 
					Callable.getCallable(":"), 
					new ConfNode[]{}, 
					outs, 
					c.getLine(), 
					c.parent);
		}
	}
	
	static class Exec extends Native {

		public Exec(String name) {
			super(name, new StructInput[0], new StructOutput[0], new ConfNodeType[]{ConfNodeType.STATEMENTSET});
		}
		
		@Override
		public void call(Pipe[] ins, Pipe[] outs, ConfNode[] confNodes) {
			((StatementSet)confNodes[0]).call();
		}
	}
	
	static class Input extends Native {
		public Input(String name) {
			super(	name,
					new StructInput[0],
					new StructOutput[]{new StructOutput(new UndefinedPipe("result", 0, Datatype.Anything, true))},
					new ConfNodeType[]{ConfNodeType.PIPETYPE});
		}
		public void call(Pipe[] ins, Pipe[] outs, ConfNode[] confNodes) {
			Datatype type = ((ConfPipeType)confNodes[0]).getPipeType();
			if (type == Datatype.I8) {
				((NumericPipe.I8Pipe)outs[0]).value = in.nextByte();
			} else if (type == Datatype.I16) {
				((NumericPipe.I16Pipe)outs[0]).value = in.nextShort();
			} else if (type == Datatype.I32) {
				((NumericPipe.I32Pipe)outs[0]).value = in.nextInt();
			} else if (type == Datatype.I64) {
				((NumericPipe.I64Pipe)outs[0]).value = in.nextLong();
			} else if (type == Datatype.Single) {
				((NumericPipe.SinglePipe)outs[0]).value = in.nextFloat();
			} else if (type == Datatype.Double) {
				((NumericPipe.DoublePipe)outs[0]).value = in.nextDouble();
			} else if (type == Datatype.String) {
				((StringPipe)outs[0]).value = in.next();
			} else if (type == Datatype.Bool) {
				((BoolPipe)outs[0]).value = in.nextBoolean();
			} else {
				RuntimeError.throwIf(true, c.getLine(), type + " not supported for INPUT");
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
					new StructOutput[]{new StructOutput(new UndefinedPipe("destination", 0, Datatype.Anything))});
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

abstract class Native extends Callable {
	
	CachedCall c;
	
	public Native(String name, StructInput[] ins, StructOutput[] outs) {
		super(name, ins, outs);
	}
	
	public Native(String name, StructInput[] ins, StructOutput[] outs, ConfNodeType[] confNodes) {
		super(name, ins, outs, confNodes);
	}
	
	public Native(String name, StructInput[] ins, StructOutput[] outs, boolean inputsArbitrary) {
		super(name, ins, outs, inputsArbitrary);
	}
	
	protected void checkCompat(Pipe[] runtimeIns, Pipe[] runtimeOuts, CachedCall c) {
		// Loop through ins and outs and if any are defined implicitly, verify their type correctness
		for (int i = 0; i < inSize(); i++) {
			Pipe reqType = getIn(i).definition();
			if (reqType.isImplicit()) reqType = getIn(i).getImplicitReference(c);
			else continue;
			Pipe.checkCompat(runtimeIns[i], reqType, c.getLine());
		}
		for (int i = 0; i < outSize(); i++) {
			Pipe reqType = getOut(i).definition();
			if (reqType.isImplicit()) reqType = getOut(i).getImplicitReference(c);
			else continue;
			Pipe.checkCompat(reqType, runtimeOuts[i], c.getLine()); // TODO verify order
		}
	}
	
	@Override
	public final void call(Pipe[] runtimeIns, Pipe[] runtimeOuts, CachedCall c) {
		checkCompat(runtimeIns, runtimeOuts, c);
		this.c = c;
		call(runtimeIns,runtimeOuts,c.confNodes); // Forward to actual natives
	}
	
	public void call(Pipe[] ins, Pipe[] outs) {
		// Override
	}
	public void call(Pipe[] ins, Pipe[] outs, ConfNode[] confNodes) {
		this.call(ins, outs);
	}
	
	public boolean isStatic() {return false;}
	public boolean isInstantiated() {return true;}
	public boolean isInline() {return false;}
	
}

abstract class Directive extends Native {

	protected Directive(String name, StructInput[] ins, StructOutput[] outs,
			ConfNodeType[] confNodes) {
		super(name, ins, outs, confNodes);
	}
	
	public CachedCall processDirective(Pipe[] ins, Pipe[] outs, CachedCall c) {
		super.checkCompat(ins, outs, c);
		return this.directive(ins, outs, c.confNodes, c);
	}
	
	protected abstract CachedCall directive(Pipe[] ins, Pipe[] outs, ConfNode[] confNodes, CachedCall c);
	
	@Override
	public void call(Pipe[] ins, Pipe[] outs, ConfNode[] confNodes) {
		
	}

}
