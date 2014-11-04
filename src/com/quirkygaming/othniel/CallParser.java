package com.quirkygaming.othniel;

public class CallParser {
	
	public String[] inParams = new String[0];
	public String[] outParams = new String[0];
	public String callName;
	
	public static void main(String[] args) {
		String line = "static inline as[asd:e]";
		String[] splitInfo = line.split(" ");
		
		CallParser call = new CallParser(line.substring(splitInfo[0].length() + splitInfo[1].length() + 2).trim(), 123);
		
		System.out.println(call.callName);
		for (String param : call.inParams) {
			System.out.println("InParam: " + param);
		}
		for (String param : call.outParams) {
			System.out.println("OutParam: " + param);
		}
	}
	
	public CallParser(String line, int lineN) {
		ParseError.validate(! line.isEmpty(), lineN, "Expected [ or call name");
		
		if (line.charAt(0) == '[') { // Contains inParams 
			
			int endBracket = line.indexOf(']');
			ParseError.validate(endBracket >= 1, lineN, "Expected ]");
			
			String inParamString = line.substring(1, endBracket).trim();
			if (!inParamString.isEmpty()) inParams = inParamString.split(",");
			
			line = line.substring(endBracket+1); // Trim line to after endbracket
			
		}
		if (line.contains("[")) {
			int startBracket = line.indexOf('[');
			int endBracket = line.indexOf(']');
			
			ParseError.validate(endBracket != -1 && endBracket > startBracket, lineN, "Expected ]");
			
			String outParamString = line.substring(startBracket + 1, endBracket).trim();
			if (!outParamString.isEmpty()) outParams = outParamString.split(",");
			
			line = line.substring(0, startBracket); // Trim line to only contain call
		}
		
		callName = line;
		ParseError.validate(!callName.isEmpty(), lineN, "Empty call name");
	}
	
}
