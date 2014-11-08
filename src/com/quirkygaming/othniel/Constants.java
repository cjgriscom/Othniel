package com.quirkygaming.othniel;

import com.quirkygaming.othniel.pipes.BoolPipe;
import com.quirkygaming.othniel.pipes.NumericPipe;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StringPipe;

public class Constants {
	public static Pipe matchConstant(String s, int lineN) {
		if (s.equals("TRUE")) {
			return new BoolPipe("Bool Constant", true);
		} else if (s.equals("FALSE")) {
			return new BoolPipe("Bool Constant", false);
		} else if (s.startsWith("\"") && s.endsWith("\"")) {
			return new StringPipe("String Constant", s.substring(1, s.length()-1)); // String constant
		} else { //TODO longs, exceptions
			try {
				int n = Integer.parseInt(s);
				return new NumericPipe.I32Pipe("I32 Constant", n);
			} catch (NumberFormatException e) {}
			try {
				double n = Double.parseDouble(s);
				return new NumericPipe.DoublePipe("Double Constant", n);
			} catch (NumberFormatException e) {}
		}
		
		return null;
	}
}
