package com.quirkygaming.othniel;

public class Constants {
	public static Pipe matchConstant(String s, int lineN) {
		if (s.equals("TRUE")) {
			return new BoolPipe(null, true);
		} else if (s.equals("FALSE")) {
			return new BoolPipe(null, false);
		} else if (s.startsWith("\"") && s.endsWith("\"")) {
			return new StringPipe(null, s.substring(1, s.length()-1)); // String constant
		} else { //TODO longs, exceptions
			try {
				int n = Integer.parseInt(s);
				return new NumericPipe.I32Pipe(null, n);
			} catch (NumberFormatException e) {}
			try {
				double n = Double.parseDouble(s);
				return new NumericPipe.DoublePipe(null, n);
			} catch (NumberFormatException e) {}
		}
		
		return null;
	}
}
