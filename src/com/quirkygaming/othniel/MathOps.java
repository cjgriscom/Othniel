package com.quirkygaming.othniel;

public class MathOps {
	public static enum Op {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD
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
