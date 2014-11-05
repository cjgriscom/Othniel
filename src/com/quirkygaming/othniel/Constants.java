package com.quirkygaming.othniel;

public class Constants {
	public static Pipe matchConstant(String s, int lineN) {
		if (s.equals("TRUE")) {
			return new Pipe(null, true, Datatype.Bool, Datatype.Bool, lineN);
		} else if (s.equals("FALSE")) {
			return new Pipe(null, false, Datatype.Bool, Datatype.Bool, lineN);
		} else if (s.startsWith("\"") && s.endsWith("\"")) {
			return new Pipe(null, s.substring(1, s.length()-1), Datatype.String, Datatype.String, lineN); // String constant
		} else { //TODO longs, exceptions
			try {
				int n = Integer.parseInt(s);
				return new Pipe(null, n, Datatype.I32, Datatype.I32, lineN);
			} catch (NumberFormatException e) {}
			try {
				double n = Double.parseDouble(s);
				return new Pipe(null, n, Datatype.Double, Datatype.Double, lineN);
			} catch (NumberFormatException e) {}
		}
		
		return null;
	}
}
