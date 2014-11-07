package com.quirkygaming.othniel;

import static com.quirkygaming.othniel.Datatype.*;

public class MathOps {
	public static enum Op {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD
	}
	
	public static Object castNumber(Object n, Datatype from, Datatype to) {
		if (from.equals(Double)) {
			if (to.equals(Double)) return (double) (double) n;
			if (to.equals(Single)) return (float) (double) n; 
			if (to.equals(I64)) return (long) (double) n;
			if (to.equals(I32)) return (int) (double) n; 
			if (to.equals(I16)) return (short) (double) n;
			if (to.equals(I8)) return (byte) (double) n;
		}
		if (from.equals(Single)) {
			if (to.equals(Double)) return (double) (float) n;
			if (to.equals(Single)) return (float) (float) n; 
			if (to.equals(I64)) return (long) (float) n;
			if (to.equals(I32)) return (int) (float) n; 
			if (to.equals(I16)) return (short) (float) n;
			if (to.equals(I8)) return (byte) (float) n;
		}
		if (from.equals(I64)) {
			if (to.equals(Double)) return (double) (long) n;
			if (to.equals(Single)) return (float) (long) n; 
			if (to.equals(I64)) return (long) (long) n;
			if (to.equals(I32)) return (int) (long) n; 
			if (to.equals(I16)) return (short) (long) n;
			if (to.equals(I8)) return (byte) (long) n;
		}
		if (from.equals(I32)) {
			if (to.equals(Double)) return (double) (int) n;
			if (to.equals(Single)) return (float) (int) n; 
			if (to.equals(I64)) return (long) (int) n;
			if (to.equals(I32)) return (int) (int) n; 
			if (to.equals(I16)) return (short) (int) n;
			if (to.equals(I8)) return (byte) (int) n;
		}
		if (from.equals(I16)) {
			if (to.equals(Double)) return (double) (short) n;
			if (to.equals(Single)) return (float) (short) n; 
			if (to.equals(I64)) return (long) (short) n;
			if (to.equals(I32)) return (int) (short) n; 
			if (to.equals(I16)) return (short) (short) n;
			if (to.equals(I8)) return (byte) (short) n;
		}
		if (from.equals(I8)) {
			if (to.equals(Double)) return (double) (byte) n;
			if (to.equals(Single)) return (float) (byte) n; 
			if (to.equals(I64)) return (long) (byte) n;
			if (to.equals(I32)) return (int) (byte) n; 
			if (to.equals(I16)) return (short) (byte) n;
			if (to.equals(I8)) return (byte) (byte) n;
		}
		return null;
	}
	
	public static Object op(Op o, Object num1, Object num2, Datatype t1, Datatype t2) {
		if (Double.equals(t1))
			return op(o, (double) num1, (double) num2);
		if (Single.equals(t1))
			return op(o, (float) num1, (float) num2);
		if (I64.equals(t1))
			return op(o, (long) num1, (long) num2);
		if (I32.equals(t1))
			return op(o, (int) num1, (int) num2);
		if (Double.equals(t1))
			return op(o, (double) num1, (double) num2);
		if (Double.equals(t1))
			return op(o, (double) num1, (double) num2);
		
		return null;
	}
	
	public static double op(Op o, double num1, double num2) {
		switch (o) {
			case ADD: return num1 + num2;
			case SUBTRACT: return num1 - num2;
			case MULTIPLY: return num1 * num2;
			case DIVIDE: return num1 / num2;
			case MOD: return num1 % num2;
			default: return 0d;
		}
	}
	
	public static float op(Op o, float num1, float num2) {
		switch (o) {
			case ADD: return num1 + num2;
			case SUBTRACT: return num1 - num2;
			case MULTIPLY: return num1 * num2;
			case DIVIDE: return num1 / num2;
			case MOD: return num1 % num2;
			default: return 0f;
		}
	}
	
	public static long op(Op o, long num1, long num2) {
		switch (o) {
			case ADD: return num1 + num2;
			case SUBTRACT: return num1 - num2;
			case MULTIPLY: return num1 * num2;
			case DIVIDE: return num1 / num2;
			case MOD: return num1 % num2;
			default: return 0L;
		}
	}
	
	public static int op(Op o, int num1, int num2) {
		switch (o) {
			case ADD: return num1 + num2;
			case SUBTRACT: return num1 - num2;
			case MULTIPLY: return num1 * num2;
			case DIVIDE: return num1 / num2;
			case MOD: return num1 % num2;
			default: return 0;
		}
	}
	
	public static short op(Op o, short num1, short num2) {
		switch (o) {
			case ADD: return (short) (num1 + num2);
			case SUBTRACT: return (short) (num1 - num2);
			case MULTIPLY: return (short) (num1 * num2);
			case DIVIDE: return (short) (num1 / num2);
			case MOD: return (short) (num1 % num2);
			default: return 0;
		}
	}
	
	public static byte op(Op o, byte num1, byte num2) {
		switch (o) {
			case ADD: return (byte) (num1 + num2);
			case SUBTRACT: return (byte) (num1 - num2);
			case MULTIPLY: return (byte) (num1 * num2);
			case DIVIDE: return (byte) (num1 / num2);
			case MOD: return (byte) (num1 % num2);
			default: return 0;
		}
	}
}
