package com.quirkygaming.othniel;

public class Constants {
	public static Pipe matchConstant(String s) {
		if (s.equals("TRUE")) {
			return new Pipe(null, true, Datatype.Bool);
		} else if (s.equals("FALSE")) {
			return new Pipe(null, false, Datatype.Bool);
		} else if (s.startsWith("\"") && s.endsWith("\"")) {
			return new Pipe(null, s.substring(1, s.length()-1), Datatype.String); // String constant
		} else { //TODO longs, exceptions
			try {
				int n = Integer.parseInt(s);
				return new Pipe(null, n, Datatype.I32);
			} catch (NumberFormatException e) {}
			try {
				double n = Double.parseDouble(s);
				return new Pipe(null, n, Datatype.Double);
			} catch (NumberFormatException e) {}
		}
		
		return null;
	}
}
