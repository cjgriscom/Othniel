package com.quirkygaming.othniel;

import java.util.Scanner;

import com.quirkygaming.othniel.MathOps.Op;


public class Natives extends CallableContainer {
	
	private static Scanner in = new Scanner(System.in);
	
	static void initNatives() {
		cache(new Input("INPUT"));
		cache(new InputLine("INPUTLN"));
		cache(new Print(false, "PRINT"));
		cache(new Print(true, "PRINTLN"));
		cache(new Assign(":"));
		cache(new Xor("XOR"));
		cache(new And("AND"));
		cache(new Or("OR"));
		cache(new Not("NOT"));
		cache(new Ternary("?:"));
		cache(new Add("+"));
		
		for (Callable c : Interpreter.cacheFile("Natives.othsrc").values()) {
			cache(c);
		}
	}
	
	private static void cache(Callable c) {
		calls.put(c.name(), c);
	}
	
	abstract static class Native extends Callable {
		
		CachedCall c;
		
		public Native(String name, Datatype[] ins, Datatype[] outs) {
			super(name, ins, outs);
		}
		
		@Override
		public final void call(Pipe[] runtimeIns, Pipe[] runtimeOuts, CachedCall c) {
			for (int i = 0; i < ins.length; i++) {
				Datatype reqType = ins[i];
				if (reqType.isImplicit()) reqType = reqType.getImplicitType(runtimeIns[i].getLabel(), c);
				else continue;
				Datatype runtimeType = runtimeIns[i].type();
				Datatype.checkCompat(runtimeType, reqType, c.getLine()); // TODO verify order
			}
			for (int i = 0; i < outs.length; i++) {
				Datatype reqType = outs[i];
				if (reqType.isImplicit()) reqType = reqType.getImplicitType(runtimeOuts[i].getLabel(), c);
				else continue;
				Datatype runtimeType = runtimeOuts[i].type();
				Datatype.checkCompat(reqType, runtimeType, c.getLine()); // TODO verify order
			}
			this.c = c;
			call(runtimeIns,runtimeOuts);
		}
		
		public abstract void call(Pipe[] ins, Pipe[] outs);
		
	}
	
	static class Input extends Native {
		public Input(String name) {
			super(	name,
					new Datatype[]{Datatype.Anything},
					new Datatype[]{Datatype.implicit(0)});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			Object value = null;
			if (ins[0].type() == Datatype.I8) {
				value = in.nextByte();
			} else if (ins[0].type() == Datatype.I16) {
				value = in.nextShort();
			} else if (ins[0].type() == Datatype.I32) {
				value = in.nextInt();
			} else if (ins[0].type() == Datatype.I64) {
				value = in.nextLong();
			} else if (ins[0].type() == Datatype.Single) {
				value = in.nextFloat();
			} else if (ins[0].type() == Datatype.Double) {
				value = in.nextDouble();
			} else if (ins[0].type() == Datatype.String) {
				value = in.next();
			} else if (ins[0].type() == Datatype.Bool) {
				value = in.nextBoolean();
			} else {
				System.err.println("Invalid input datatype");
			}
			
			outs[0].set(value, ins[0].type(), c.getLine());
		}
	}
	static class InputLine extends Native {
		public InputLine(String name) {
			super(	name,
					new Datatype[0],
					new Datatype[]{Datatype.String});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			Object value = in.nextLine();
			outs[0].set(value, Datatype.String, c.getLine());
		}
	}
	static class Print extends Native {
		boolean newLine;
		public Print(boolean newLine, String name) {
			super(	name,
					new Datatype[]{Datatype.Anything},
					new Datatype[0]);
			this.newLine = newLine;
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			System.out.print(ins[0].get());
			if (newLine) System.out.println();
		}
	}
	static class Assign extends Native {
		public Assign(String name) {
			super(	name,
					new Datatype[]{Datatype.Anything},
					new Datatype[]{Datatype.implicit(0)});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			outs[0].set(ins[0].get(), ins[0].type(), c.getLine());
		}
	}
	static class Xor extends Native {
		public Xor(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool, Datatype.Bool},
					new Datatype[]{Datatype.Bool});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			outs[0].set((Boolean) ins[0].get() ^ (Boolean) ins[1].get(), Datatype.Bool, c.getLine());
		}
	}
	static class And extends Native {
		public And(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool, Datatype.Bool},
					new Datatype[]{Datatype.Bool});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			outs[0].set((Boolean) ins[0].get() && (Boolean) ins[1].get(), Datatype.Bool, c.getLine());
		}
	}
	static class Or extends Native {
		public Or(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool, Datatype.Bool},
					new Datatype[]{Datatype.Bool});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			outs[0].set((Boolean) ins[0].get() || (Boolean) ins[1].get(), Datatype.Bool, c.getLine());
		}
	}
	static class Not extends Native {
		public Not(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool},
					new Datatype[]{Datatype.Bool});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			outs[0].set(!(Boolean) ins[0].get(), Datatype.Bool, c.getLine());
		}
	}
	static class Ternary extends Native {
		public Ternary(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool, Datatype.Anything, Datatype.implicit(1)},
					new Datatype[]{Datatype.implicit(1)});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			outs[0].set((Boolean) ins[0].get() ? ins[1].get() : ins[2].get(), ins[1].type(), c.getLine());
		}
	}
	static class Add extends Native {
		public Add(String name) {
			super(	name,
					new Datatype[]{Datatype.Numeric, Datatype.implicit(0)},
					new Datatype[]{Datatype.implicit(0)});
		}
		public void call(Pipe[] ins, Pipe[] outs) {
			outs[0].set(MathOps.op(Op.ADD, 
					ins[0].get(), 
					ins[1].get(), 
					ins[0].type(), 
					ins[1].type()
					), ins[0].type(), c.getLine());
			
		}
	}
	
}
