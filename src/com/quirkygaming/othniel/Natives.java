package com.quirkygaming.othniel;

import java.util.Scanner;


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
	
	static class Input extends Callable {
		public Input(String name) {
			super(	name,
					new Datatype[]{Datatype.Anything},
					new Datatype[]{Datatype.implicit(0)});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			Object value = null;
			if (ins[0].type() == Datatype.I32) {
				value = in.nextInt();
			} else if (ins[0].type() == Datatype.String) {
				value = in.next();
			} else if (ins[0].type() == Datatype.Bool) {
				value = in.nextBoolean();
			} else {
				System.err.println("Invalid input datatype");
			}
			
			outs[0].set(value);
		}
	}
	static class InputLine extends Callable {
		public InputLine(String name) {
			super(	name,
					new Datatype[0],
					new Datatype[]{Datatype.String});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			Object value = in.nextLine();
			outs[0].set(value);
		}
	}
	static class Print extends Callable {
		boolean newLine;
		public Print(boolean newLine, String name) {
			super(	name,
					new Datatype[]{Datatype.Anything},
					new Datatype[0]);
			this.newLine = newLine;
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			System.out.print(ins[0].get());
			if (newLine) System.out.println();
		}
	}
	static class Assign extends Callable {
		public Assign(String name) {
			super(	name,
					new Datatype[]{Datatype.Anything},
					new Datatype[]{Datatype.implicit(0)});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			outs[0].set(ins[0].get());
		}
	}
	static class Xor extends Callable {
		public Xor(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool, Datatype.Bool},
					new Datatype[]{Datatype.Bool});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			outs[0].set((Boolean) ins[0].get() ^ (Boolean) ins[1].get());
		}
	}
	static class And extends Callable {
		public And(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool, Datatype.Bool},
					new Datatype[]{Datatype.Bool});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			outs[0].set((Boolean) ins[0].get() && (Boolean) ins[1].get());
		}
	}
	static class Or extends Callable {
		public Or(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool, Datatype.Bool},
					new Datatype[]{Datatype.Bool});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			outs[0].set((Boolean) ins[0].get() || (Boolean) ins[1].get());
		}
	}
	static class Not extends Callable {
		public Not(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool},
					new Datatype[]{Datatype.Bool});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			outs[0].set(!(Boolean) ins[0].get());
		}
	}
	static class Ternary extends Callable {
		public Ternary(String name) {
			super(	name,
					new Datatype[]{Datatype.Bool, Datatype.Anything, Datatype.Anything},
					new Datatype[]{Datatype.implicit(1)});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			outs[0].set((Boolean) ins[0].get() ? ins[1].get() : ins[2].get());
		}
	}
	static class Add extends Callable {
		public Add(String name) {
			super(	name,
					new Datatype[]{Datatype.I32, Datatype.I32},
					new Datatype[]{Datatype.I32});
		}
		public void call(Pipe[] ins, Pipe[] outs, CachedCall c) {
			outs[0].set((Integer) ins[0].get() + (Integer) ins[1].get());
		}
	}
	
}
