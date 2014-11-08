package com.quirkygaming.othniel.pipes;

import static com.quirkygaming.othniel.Datatype.*;

import com.quirkygaming.othniel.CompOps;
import com.quirkygaming.othniel.Datatype;
import com.quirkygaming.othniel.MathOps;

public abstract class NumericPipe extends Pipe {
	
	protected NumericPipe(String label, Datatype t) {
		super(label, t);
	}
	
	public abstract void op(MathOps.Op o, boolean amIFirstOp, NumericPipe weaker, NumericPipe target);
	public abstract boolean comp(CompOps.COp o, boolean amIFirstOp, NumericPipe weaker);
	
	// Pipe classes
	public static class DoublePipe extends NumericPipe {
		public double value;
	
		public DoublePipe(String label) {super(label, Datatype.Double);}
		public DoublePipe(String label, double value) {this(label); this.value = value;}
	
		@Override
		void set(Pipe otherPipe) {
			this.value = cast((NumericPipe)otherPipe);
		}
		
		public static double cast(NumericPipe other) {
			if (other.type().equals(Double)) return (double) ((DoublePipe) other).value;
			if (other.type().equals(Single)) return (double) ((SinglePipe) other).value;
			if (other.type().equals(I64)) return (double) ((I64Pipe) other).value;
			if (other.type().equals(I32)) return (double) ((I32Pipe) other).value;
			if (other.type().equals(I16)) return (double) ((I16Pipe) other).value;
			if (other.type().equals(I8)) return (double) ((I8Pipe) other).value;
			throw new RuntimeException("Should have casted by now.");
		}
	
		@Override
		public void op(MathOps.Op o, boolean amIFirstOp, NumericPipe weaker, NumericPipe target) {
			DoublePipe targetPipe = ((DoublePipe)target);
			if (amIFirstOp) targetPipe.value = MathOps.op(o, value, cast(weaker));
			else targetPipe.value = MathOps.op(o, cast(weaker), value);
		}
		
		@Override
		public boolean comp(CompOps.COp o, boolean amIFirstOp, NumericPipe weaker) {
			if (amIFirstOp) return CompOps.op(o, value, cast(weaker));
			else return CompOps.op(o, cast(weaker), value);
		}
		
		public String toString() {
			return ""+value;
		}
	}

	public static class SinglePipe extends NumericPipe {
		public float value;
	
		public SinglePipe(String label) {super(label, Datatype.Single);}
		public SinglePipe(String label, float value) {this(label); this.value = value;}
	
		@Override
		void set(Pipe otherPipe) {
			this.value = cast((NumericPipe)otherPipe);
		}
		
		public static float cast(NumericPipe other) {
			if (other.type().equals(Double)) return (float) ((DoublePipe) other).value;
			if (other.type().equals(Single)) return (float) ((SinglePipe) other).value;
			if (other.type().equals(I64)) return (float) ((I64Pipe) other).value;
			if (other.type().equals(I32)) return (float) ((I32Pipe) other).value;
			if (other.type().equals(I16)) return (float) ((I16Pipe) other).value;
			if (other.type().equals(I8)) return (float) ((I8Pipe) other).value;
			throw new RuntimeException("Should have casted by now.");
		}
	
		@Override
		public void op(MathOps.Op o, boolean amIFirstOp, NumericPipe weaker, NumericPipe target) {
			SinglePipe targetPipe = ((SinglePipe)target);
			if (amIFirstOp) targetPipe.value = MathOps.op(o, value, cast(weaker));
			else targetPipe.value = MathOps.op(o, cast(weaker), value);
		}
		
		@Override
		public boolean comp(CompOps.COp o, boolean amIFirstOp, NumericPipe weaker) {
			if (amIFirstOp) return CompOps.op(o, value, cast(weaker));
			else return CompOps.op(o, cast(weaker), value);
		}
		
		public String toString() {
			return ""+value;
		}
	}

	public static class I64Pipe extends NumericPipe {
		public long value;
	
		public I64Pipe(String label) {super(label, Datatype.I64);}
		public I64Pipe(String label, long value) {this(label); this.value = value;}
	
		@Override
		void set(Pipe otherPipe) {
			this.value = cast((NumericPipe)otherPipe);
		}
		
		public static long cast(NumericPipe other) {
			if (other.type().equals(Double)) return (long) ((DoublePipe) other).value;
			if (other.type().equals(Single)) return (long) ((SinglePipe) other).value;
			if (other.type().equals(I64)) return (long) ((I64Pipe) other).value;
			if (other.type().equals(I32)) return (long) ((I32Pipe) other).value;
			if (other.type().equals(I16)) return (long) ((I16Pipe) other).value;
			if (other.type().equals(I8)) return (long) ((I8Pipe) other).value;
			throw new RuntimeException("Should have casted by now.");
		}
	
		@Override
		public void op(MathOps.Op o, boolean amIFirstOp, NumericPipe weaker, NumericPipe target) {
			I64Pipe targetPipe = ((I64Pipe)target);
			if (amIFirstOp) targetPipe.value = MathOps.op(o, value, cast(weaker));
			else targetPipe.value = MathOps.op(o, cast(weaker), value);
		}
		
		@Override
		public boolean comp(CompOps.COp o, boolean amIFirstOp, NumericPipe weaker) {
			if (amIFirstOp) return CompOps.op(o, value, cast(weaker));
			else return CompOps.op(o, cast(weaker), value);
		}
		
		public String toString() {
			return ""+value;
		}
	}

	public static class I32Pipe extends NumericPipe {
		public int value;
	
		public I32Pipe(String label) {super(label, Datatype.I32);}
		public I32Pipe(String label, int value) {this(label); this.value = value;}
	
		@Override
		void set(Pipe otherPipe) {
			this.value = cast((NumericPipe)otherPipe);
		}
		
		public static int cast(NumericPipe other) {
			if (other.type().equals(Double)) return (int) ((DoublePipe) other).value;
			if (other.type().equals(Single)) return (int) ((SinglePipe) other).value;
			if (other.type().equals(I64)) return (int) ((I64Pipe) other).value;
			if (other.type().equals(I32)) return (int) ((I32Pipe) other).value;
			if (other.type().equals(I16)) return (int) ((I16Pipe) other).value;
			if (other.type().equals(I8)) return (int) ((I8Pipe) other).value;
			throw new RuntimeException("Should have casted by now.");
		}
	
		@Override
		public void op(MathOps.Op o, boolean amIFirstOp, NumericPipe weaker, NumericPipe target) {
			I32Pipe targetPipe = ((I32Pipe)target);
			if (amIFirstOp) targetPipe.value = MathOps.op(o, value, cast(weaker));
			else targetPipe.value = MathOps.op(o, cast(weaker), value);
		}
		
		@Override
		public boolean comp(CompOps.COp o, boolean amIFirstOp, NumericPipe weaker) {
			if (amIFirstOp) return CompOps.op(o, value, cast(weaker));
			else return CompOps.op(o, cast(weaker), value);
		}
		
		public String toString() {
			return ""+value;
		}
	}

	public static class I16Pipe extends NumericPipe {
		public short value;
	
		public I16Pipe(String label) {super(label, Datatype.I16);}
		public I16Pipe(String label, short value) {this(label); this.value = value;}
	
		@Override
		void set(Pipe otherPipe) {
			this.value = cast((NumericPipe)otherPipe);
		}
		
		public static short cast(NumericPipe other) {
			if (other.type().equals(Double)) return (short) ((DoublePipe) other).value;
			if (other.type().equals(Single)) return (short) ((SinglePipe) other).value;
			if (other.type().equals(I64)) return (short) ((I64Pipe) other).value;
			if (other.type().equals(I32)) return (short) ((I32Pipe) other).value;
			if (other.type().equals(I16)) return (short) ((I16Pipe) other).value;
			if (other.type().equals(I8)) return (short) ((I8Pipe) other).value;
			throw new RuntimeException("Should have casted by now.");
		}
	
		@Override
		public void op(MathOps.Op o, boolean amIFirstOp, NumericPipe weaker, NumericPipe target) {
			I16Pipe targetPipe = ((I16Pipe)target);
			if (amIFirstOp) targetPipe.value = MathOps.op(o, value, cast(weaker));
			else targetPipe.value = MathOps.op(o, cast(weaker), value);
		}
		
		@Override
		public boolean comp(CompOps.COp o, boolean amIFirstOp, NumericPipe weaker) {
			if (amIFirstOp) return CompOps.op(o, value, cast(weaker));
			else return CompOps.op(o, cast(weaker), value);
		}
		
		public String toString() {
			return ""+value;
		}
	}

	public static class I8Pipe extends NumericPipe {
		public byte value;
	
		public I8Pipe(String label) {super(label, Datatype.I8);}
		public I8Pipe(String label, byte value) {this(label); this.value = value;}
	
		@Override
		void set(Pipe otherPipe) {
			this.value = cast((NumericPipe)otherPipe);
		}
		
		public static byte cast(NumericPipe other) {
			if (other.type().equals(Double)) return (byte) ((DoublePipe) other).value;
			if (other.type().equals(Single)) return (byte) ((SinglePipe) other).value;
			if (other.type().equals(I64)) return (byte) ((I64Pipe) other).value;
			if (other.type().equals(I32)) return (byte) ((I32Pipe) other).value;
			if (other.type().equals(I16)) return (byte) ((I16Pipe) other).value;
			if (other.type().equals(I8)) return (byte) ((I8Pipe) other).value;
			throw new RuntimeException("Should have casted by now.");
		}
	
		@Override
		public void op(MathOps.Op o, boolean amIFirstOp, NumericPipe weaker, NumericPipe target) {
			I8Pipe targetPipe = ((I8Pipe)target);
			if (amIFirstOp) targetPipe.value = MathOps.op(o, value, cast(weaker));
			else targetPipe.value = MathOps.op(o, cast(weaker), value);
		}
		
		@Override
		public boolean comp(CompOps.COp o, boolean amIFirstOp, NumericPipe weaker) {
			if (amIFirstOp) return CompOps.op(o, value, cast(weaker));
			else return CompOps.op(o, cast(weaker), value);
		}
		
		public String toString() {
			return ""+value;
		}
	}
}
