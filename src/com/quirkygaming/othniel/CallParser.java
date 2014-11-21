package com.quirkygaming.othniel;

import java.util.ArrayList;

import com.quirkygaming.othniel.pipes.GarbagePipe;

public class CallParser {
	
	private ArrayList<ParsedCall> calls = new ArrayList<ParsedCall>();
	public class ParsedCall {
		public boolean isBlockStart = false;
		public boolean isBlockEnd = false;
		public String[] inParams;
		public String[] outParams;
		public String[] confNodes;
		public String callName;
	}
	
	public ParsedCall get(int index) {
		return calls.get(index);
	}
	
	public ParsedCall firstCall() {
		return get(0);
	}
	
	public int size() {
		return calls.size();
	}
	
	public ArrayList<ParsedCall> getCalls() {
		return calls;
	}
	
	public static void main(String[] args) { // Test method
		String line = "[pipe]uhh: "
				+ "[o]:[p] s{var:I32=0}[ss] [s]:[^]a[var33, l] "
				+ "if{[qwerty]:{stuff}[xyz],xyz]}: "
				+ "[a]asd[b]op[p] "
				+ ":elseif{corn}: "
				+ ":end "
				+ ":end";

		for (Component com : separateComponents(line, 123)) System.out.print(com.type + " ");
		System.out.println();
		
		CallParser parser = new CallParser(line, 123, false);
		int indent = 0;
		
		for (ParsedCall call : parser.calls) {
			if (call.isBlockEnd) {indent--;}
			for (int ind = 0; ind < indent; ind++) {System.out.print("  ");}
			if (call.isBlockEnd) System.out.print(":");
			System.out.print("[");
			for (String param : call.inParams) {
				System.out.print(param + " ");
			}
			System.out.print("]");
			System.out.print(call.callName);
			if (call.confNodes.length > 0) {
				System.out.print("{ ");
				for (String param : call.confNodes) {
					System.out.print(param + " ");
				}
				System.out.print("}");
			}
			
			System.out.print("[");
			for (String param : call.outParams) {
				System.out.print(param + " ");
			}
			System.out.print("]");
			if (call.isBlockStart) {indent++; System.out.print(":");}
			System.out.println();
		}
	}
	
	private String[] adjoinedInsFromOuts(String[] outs) {
		// Determine length, scrapping garbage pipes
		int length = 0;
		for (String label : outs) {
			if (!label.equals(GarbagePipe.INSTANCE.getLabel())) {
				length++;
			}
		}
		
		String[] ins = new String[length]; // Add each of the non-garbage labels to the clone
		int i = 0;
		for (String label : outs) {
			if (label.equals(">")) {
				ins[i] = "<";
				i++;
			} else if (!label.equals(GarbagePipe.INSTANCE.getLabel())) {
				ins[i] = label;
				i++;
			}
		}
		return ins;
	}
	
	public CallParser(String line, int lineN, boolean isHeader) {
		ArrayList<Component> components = separateComponents(line, lineN);
		processIntoCalls(line, components, lineN);
		if (isHeader) {
			ParseError.validate(size() == 1, lineN, 
					"Found " + size() + " definitions in header; 1 expected");
		}
	}
	
	final static int PARAMETER = 1;
	final static int SPACE     = 2;
	final static int CALL_NAME = 4;
	final static int CONF_NODE = 8;
	final static int COLON 	= 16;
	private static class Component {
		int type;
		int beginIndex;
		String content;
		Component(int type, String content, int beginIndex) {
			this.type = type; this.content = content.trim(); this.beginIndex = beginIndex;
		}
		Component(int beginIndex) {
			// For space
			this.type = SPACE;
			this.beginIndex = beginIndex;
		}
		public String toString() {
			switch (type) {
				case PARAMETER: return "parameter: " + content;
				case SPACE: return "space";
				case CALL_NAME: return "call: " + content;
				case COLON: return "colon";
				default: return "confnode: " + content;
			}
		}
	}
	
	private void processIntoCalls(String line, ArrayList<Component> components, int lineN) {
		int expected = PARAMETER + CALL_NAME + COLON;
		ParsedCall currentCall = new ParsedCall();
		int lastType = SPACE; // Coming off of another line, theoretically a space exists in between
		int type = SPACE;
		Component c = null;
		for (int i = 0; i < components.size(); i++) {
			c = components.get(i);
			ParseError.verifyExpectedBit(expected, c.type, lineN, "Unexpected " + c + " at index " + c.beginIndex + " following " + lastType);
			lastType = type;
			type = c.type;
			if (type == PARAMETER) {
				
				int nParams = 0; i--;
				while (components.size() > i + 1 && components.get(i + 1).type == PARAMETER) {
					i++;
					if (!components.get(i).content.isEmpty()) nParams++; // An empty param denotes empty brackets; we don't want to add them
				}
				String[] paramArray = new String[nParams];
				for (int j = 0; j < nParams; j++) {
					paramArray[j] = components.get(i - nParams + j+1).content;
				}
				// Done processing params
				if (lastType == CALL_NAME || lastType == CONF_NODE) { // I.E. this is an out parameter
					currentCall.outParams = paramArray;
					expected = CALL_NAME + SPACE + COLON;
				} else {  // I.E. this is an in parameter
					currentCall.inParams = paramArray;
					expected = CALL_NAME;
				}
			} else if (c.type == CALL_NAME) {
				if (currentCall.outParams != null) { // Adjoined call
					// Determine adjoined inputs and advance currentCall to new one
					String[] newIns = adjoinedInsFromOuts(currentCall.outParams);
					currentCall = advance(currentCall, lineN, c);
					currentCall.inParams = newIns;
				}
				currentCall.callName = c.content;
				expected = CONF_NODE + SPACE + PARAMETER + COLON;
			} else if (c.type == CONF_NODE) {
				expected = SPACE + PARAMETER + COLON;
				int nNodes = 0; i--;
				while (components.size() > i + 1 && components.get(i + 1).type == CONF_NODE) {
					i++;
					if (!components.get(i).content.isEmpty()) nNodes++; // An empty param denotes empty brackets; we don't want to add them
				}
				String[] cnodeArray = new String[nNodes];
				for (int j = 0; j < nNodes; j++) {
					cnodeArray[j] = components.get(i - nNodes + j+1).content;
				}
				currentCall.confNodes = cnodeArray;
			} else if (c.type == SPACE) {
				// Advance currentCall to new one
				currentCall = advance(currentCall, lineN, c);
				expected = PARAMETER + CALL_NAME + COLON;
			} else if (c.type == COLON) {
				if (lastType == SPACE) {
					currentCall.isBlockEnd = true;
					expected = CALL_NAME + PARAMETER;
				} else {
					currentCall.isBlockStart = true;
					expected = SPACE;
				}
			}
		}
		advance(currentCall, lineN, c);
	}
	
	private ParsedCall advance(ParsedCall currentCall, int lineN, Component c) {
		ParseError.validate(currentCall.callName != null, lineN, "Floating parameters at index " + c.beginIndex); 
		if (currentCall.inParams == null) currentCall.inParams = new String[0];
		if (currentCall.outParams == null) currentCall.outParams = new String[0];
		if (currentCall.confNodes == null) currentCall.confNodes = new String[0];
		calls.add(currentCall);
		return new ParsedCall();
	}
	
	private static ArrayList<Component> separateComponents(String line, int lineN) {
		// Deconstruct line byte by byte
		
		ArrayList<Component> components = new ArrayList<Component>();
		
		int btbbrackets = line.indexOf("][");
		if (btbbrackets == -1) btbbrackets = line.indexOf("}{");
		ParseError.validate(btbbrackets == -1, lineN, "Encountered illegal brackets at index " + btbbrackets);
		
		int NOTHING = 0;
		
		int beginIndex = 0;
		line = line.trim(); // Very important
		
		boolean inQuotes = false;
		int curlyCount = 0;
		int current = NOTHING;
		char c;
		boolean atEnd;
		
		for (int i = 0; i <= line.length(); i++) {
			atEnd = i+1 == line.length(); // for colon checking
			if (i == line.length()) {
				ParseError.validate(current == NOTHING || current == CALL_NAME, lineN, "Expected closing bracket");
				if (current == CALL_NAME) {
					components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
				}
				break;
			} else {
				c = line.charAt(i);
			}
			
			if ((c == ' ' || c == '\t') && curlyCount == 0) { // Process space or tab IF
				if (	current != PARAMETER && // Not a parameter list
						current != CONF_NODE && // Not a list of conf nodes
						components.size() >= 1 && // List has substantial length
						components.get(components.size() - 1).type != SPACE // Last one wasn't a space
						) {
					if (current == CALL_NAME) {
						components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
						current = NOTHING;
					}
					components.add(new Component(i)); // Add spaces between calls
				}
			} else if (inQuotes && curlyCount == 0) { // We want to ignore other symbols when in quotation marks
				if (c == '"' || c == '\'') {
					inQuotes = false; // Encountered end
				}
			} else if ((c == '"' || c == '\'') && curlyCount == 0) { // If quotes found
				ParseError.validate(
						current == PARAMETER || current == CONF_NODE,
						lineN,
						"Encountered unexpected \" at index " + i);
				inQuotes = true;
			} else if (c == '[' && curlyCount == 0) { // Open params
				ParseError.validate(
						current == NOTHING || current == CALL_NAME,
						lineN,
						"Encountered unexpected [ at index " + i);
				if (current == CALL_NAME)  // End of call
					components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
				current = PARAMETER;
				beginIndex = i + 1;
			} else if (c == '{') { // Open conf nodes
				if (curlyCount == 0) {
					ParseError.validate(
							current == NOTHING || current == CALL_NAME,
							lineN,
							"Encountered unexpected { at index " + i);
					if (current == CALL_NAME) // End of call
						components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
					current = CONF_NODE;
					beginIndex = i + 1;
				}
				curlyCount++;
				
			} else if (c == ']' && curlyCount == 0) {
				ParseError.validate(
						current == PARAMETER,
						lineN,
						"Encountered unexpected ] at index " + i);
				String item = line.substring(beginIndex, i);
				components.add(new Component(current, item, beginIndex));
				current = NOTHING;
			} else if (c == '}') {
				if (curlyCount <= 1) {
					ParseError.validate(
							current == CONF_NODE,
							lineN,
							"Encountered unexpected } at index " + i);
					String item = line.substring(beginIndex, i);
					components.add(new Component(current, item, beginIndex));
					current = NOTHING;
				}
				curlyCount--;
			} else if (c == ',' && curlyCount <= 1) {
				ParseError.validate(
						current == PARAMETER || current == CONF_NODE,
						lineN,
						"Encountered unexpected , at index " + i);
				components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
				beginIndex = i + 1;
			} else if (c == ':' && (current == NOTHING || current == CALL_NAME) &&
					(
							(atEnd || line.charAt(i+1) == ' ' || line.charAt(i+1) == '\t') ||  // Make sure it's not a : or ?: native
							(i == 0 || line.charAt(i-1) == ' ' || line.charAt(i-1) == '\t')
					) ) {
				if (current == CALL_NAME) { // End of call
					components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
					current = NOTHING;
				}
				components.add(new Component(COLON, "", i));
			} else { 
				if (current == NOTHING) {
					current = CALL_NAME;
					beginIndex = i;
				}
			}
		}
		return components;
	}
	
}
