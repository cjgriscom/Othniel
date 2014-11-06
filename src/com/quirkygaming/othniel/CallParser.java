package com.quirkygaming.othniel;

import java.util.ArrayList;

public class CallParser {
	
	private ArrayList<ParsedCall> calls = new ArrayList<ParsedCall>();
	public class ParsedCall {
		public String[] inParams;
		public String[] outParams;
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
		String line = "[pipe]a s[ss] [s]s[]a[var33, l] [a]asd[b]op[p]";
		
		CallParser parser = new CallParser(line, 123, false);
		
		for (ParsedCall call : parser.calls) {
			System.out.print("[");
			for (String param : call.inParams) {
				System.out.print(param + " ");
			}
			System.out.print("]");
			System.out.print(call.callName);
			System.out.print("[");
			for (String param : call.outParams) {
				System.out.print(param + " ");
			}
			System.out.println("]");
		}
	}
	
	public CallParser(String line, int lineN, boolean isHeader) {
		ArrayList<Component> components = separateComponents(line, lineN);
		processIntoCalls(line, components, lineN);
		if (isHeader) {
			ParseError.validate(size() == 1, lineN, 
					"Found " + size() + " definitions in header; 1 expected");
		}
	}
	
	final int PARAMETER = 1;
	final int SPACE     = 2;
	final int CALL_NAME = 4;
	final int CONF_NODE = 8;
	private class Component {
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
				case PARAMETER: return "parameter:" + content;
				case SPACE: return "space";
				case CALL_NAME: return "call:" + content;
				default: return "confnode:" + content;
			}
		}
	}
	
	private void processIntoCalls(String line, ArrayList<Component> components, int lineN) {
		int expected = PARAMETER + CALL_NAME;
		ParsedCall currentCall = new ParsedCall();
		int lastType;
		int type = -1;
		Component c = null;
		for (int i = 0; i < components.size(); i++) {
			c = components.get(i);
			ParseError.verifyExpectedBit(expected, c.type, lineN, "Unexpected " + c + " at index " + c.beginIndex);
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
				if (lastType == CALL_NAME || lastType == CONF_NODE) {
					currentCall.outParams = paramArray;
					expected = CALL_NAME + SPACE;
				} else {
					currentCall.inParams = paramArray;
					expected = CALL_NAME;
				}
			} else if (c.type == CALL_NAME) {
				if (currentCall.outParams != null) { // Adjoined call
					// Advance currentCall to new one
					String[] newIns = currentCall.outParams.clone();
					currentCall = advance(currentCall, lineN, c);
					currentCall.inParams = newIns;
				}
				currentCall.callName = c.content;
				expected = CONF_NODE + SPACE + PARAMETER;
			} else if (c.type == CONF_NODE) {
				expected = SPACE + PARAMETER;
				throw new RuntimeException("Configutation nodes not yet implemented");
			} else if (c.type == SPACE) {
				// Advance currentCall to new one
				currentCall = advance(currentCall, lineN, c);
				expected = PARAMETER + CALL_NAME;
			}
		}
		advance(currentCall, lineN, c);
	}
	
	private ParsedCall advance(ParsedCall currentCall, int lineN, Component c) {
		ParseError.validate(currentCall.callName != null, lineN, "Floating parameters at index " + c.beginIndex); 
		if (currentCall.inParams == null) currentCall.inParams = new String[0];
		if (currentCall.outParams == null) currentCall.outParams = new String[0];
		calls.add(currentCall);
		return new ParsedCall();
	}
	
	private ArrayList<Component> separateComponents(String line, int lineN) {
		// Deconstruct line byte by byte
		
		ArrayList<Component> components = new ArrayList<Component>();
		
		int btbbrackets = line.indexOf("][");
		if (btbbrackets == -1) btbbrackets = line.indexOf("}{");
		ParseError.validate(btbbrackets == -1, lineN, "Encountered illegal brackets at index " + btbbrackets);
		
		int NOTHING = 0;
		
		int beginIndex = 0;
		line = line.trim(); // Very important
		
		boolean inQuotes = false;
		int current = NOTHING;
		char c;
		
		for (int i = 0; i <= line.length(); i++) {
			if (i == line.length()) {
				ParseError.validate(current == NOTHING || current == CALL_NAME, lineN, "Expected closing bracket");
				if (current == CALL_NAME) {
					components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
				}
				break;
			} else {
				c = line.charAt(i);
			}
			
			if (c == ' ' || c == '	') { // Process space or tab IF
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
			} else if (inQuotes) { // We want to ignore other symbols when in quotation marks
				if (c == '"' || c == '\'') {
					inQuotes = false; // Encountered end
				}
			} else if (c == '"' || c == '\'') { // If quotes found
				ParseError.validate(
						current == PARAMETER || current == CONF_NODE,
						lineN,
						"Encountered unexpected \" at index " + i);
				inQuotes = true;
			} else if (c == '[') { // Open params
				ParseError.validate(
						current == NOTHING || current == CALL_NAME,
						lineN,
						"Encountered unexpected [ at index " + i);
				if (current == CALL_NAME)  // End of call
					components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
				current = PARAMETER;
				beginIndex = i + 1;
			} else if (c == '{') { // Open conf nodes
				ParseError.validate(
						current == NOTHING || current == CALL_NAME,
						lineN,
						"Encountered unexpected { at index " + i);
				if (current == CALL_NAME) // End of call
					components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
				current = CONF_NODE;
				beginIndex = i + 1;
			} else if (c == ']') {
				ParseError.validate(
						current == PARAMETER,
						lineN,
						"Encountered unexpected ] at index " + i);
				String item = line.substring(beginIndex, i);
				components.add(new Component(current, item, beginIndex));
				current = NOTHING;
			} else if (c == '}') {
				ParseError.validate(
						current == CONF_NODE,
						lineN,
						"Encountered unexpected } at index " + i);
				String item = line.substring(beginIndex, i);
				components.add(new Component(current, item, beginIndex));
				current = NOTHING;
			} else if (c == ',') {
				ParseError.validate(
						current == PARAMETER || current == CONF_NODE,
						lineN,
						"Encountered unexpected , at index " + i);
				components.add(new Component(current, line.substring(beginIndex, i), beginIndex));
				beginIndex = i + 1;
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
