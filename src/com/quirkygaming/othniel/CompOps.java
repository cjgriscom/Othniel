package com.quirkygaming.othniel;

public class CompOps {
	public static enum COp {
		EQUAL, NOTEQUAL, GREATER, LESS, GREATEREQ, LESSEQ
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean op(COp o, Object o1, Object o2) {
		Comparable num1 = (Comparable) o1;
		Comparable num2 = (Comparable) o2;

		switch (o) {
		case EQUAL: return num1.equals(num2);
		case NOTEQUAL: return !num1.equals(num2);
		case GREATER: return num1.compareTo(num2) > 0;
		case LESS: return num1.compareTo(num2) < 0;
		case GREATEREQ: return num1.compareTo(num2) >= 0;
		case LESSEQ: return num1.compareTo(num2) <= 0;
		}
		
		return false;
	}
}
