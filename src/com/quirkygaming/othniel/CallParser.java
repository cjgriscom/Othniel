package com.quirkygaming.othniel;

import java.util.ArrayList;
import com.quirkygaming.othniel.pipes.GarbagePipe;

public class CallParser {
	
	public Object attachment; //TODO remove later
	
	private CodeLines lines = new CodeLines();
	
	private ArrayList<ParsedCall> calls = new ArrayList<ParsedCall>();
	public class ParsedCall {
		public int lineN;
		public boolean isBlockStart = false;
		public boolean isBlockEnd = false;
		public String[] inParams;
		public String[] outParams;
		public String[] confNodes;
		public String callName;
		
		public boolean qualifiesAsKeyword() {
			return inParams.length == 0 && outParams.length == 0 && confNodes.length == 0 && !isBlockStart && !isBlockEnd;
		}
	}
	
	public CallParser() {}
	
	public void addLine(int lineN, String line) {
		this.lines.addLine(lineN, line);
	}
	
	public void parse() {
		ArrayList<Component> components = separateComponents();
		processIntoCalls(components);
	}
	
	public void removeFirstN(int num) {
		for(int i = 0; i < num; i++) calls.remove(0);
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
		CallParser parser = new CallParser();
		
		parser.addLine(0, "static sequence [pipe]function");
		parser.addLine(1, "[o]:[p] s{var:I32=0}[ss] [s]:[^]a[var33, l] ");
		parser.addLine(2, "if{[qwerty]:{stuff}[xyz],xyz}: ");
		parser.addLine(3, "[a]asd[b]op[p]");
		parser.addLine(4, ":elseif{something}:");
		parser.addLine(5, ":elseif{somethingelse}: ");
		parser.addLine(6, "static sequence2");
		parser.addLine(7, ":end");
		parser.addLine(8, "EXECUTE{[a]:[>] [<, \" is sweet\"]PRINTLN, lol}");
		
		for (Component com : parser.separateComponents()) System.out.print(com.type + " ");
				System.out.println();
		
		parser.parse();
		
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
	
	final static int PARAMETER = 1;
	final static int SPACE     = 2;
	final static int CALL_NAME = 4;
	final static int CONF_NODE = 8;
	final static int COLON     = 16;
	private static class Component {
		int type;
		int beginIndex;
		int lineN;
		String content;
		Component(int type, String content, int beginIndex, int lineN) {
			this.type = type; this.content = content.trim(); this.beginIndex = beginIndex; this.lineN = lineN;
		}
		Component(int beginIndex, int lineN) {
			// For space
			this.type = SPACE;
			this.beginIndex = beginIndex;
			this.lineN = lineN;
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
	
	private void processIntoCalls(ArrayList<Component> components) {
		int expected = PARAMETER + CALL_NAME + COLON;
		ParsedCall currentCall = new ParsedCall();
		int lastType = SPACE; // Coming off of another line, theoretically a space exists in between
		int type = SPACE;
		Component c = null;
		for (int i = 0; i < components.size(); i++) {
			c = components.get(i);
			currentCall.lineN = c.lineN;
			ParseError.verifyExpectedBit(expected, c.type, c.lineN, "Unexpected " + c + " at index " + c.beginIndex);
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
					currentCall = advance(currentCall, c);
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
				currentCall = advance(currentCall, c);
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
		advance(currentCall, c);
	}
	
	private ParsedCall advance(ParsedCall currentCall, Component c) {
		ParseError.validate(currentCall.callName != null, c.lineN, "Floating parameters at index " + c.beginIndex); 
		if (currentCall.inParams == null) currentCall.inParams = new String[0];
		if (currentCall.outParams == null) currentCall.outParams = new String[0];
		if (currentCall.confNodes == null) currentCall.confNodes = new String[0];
		calls.add(currentCall);
		return new ParsedCall();
	}
	
	private ArrayList<Component> separateComponents() {
		// Deconstruct line byte by byte
		
		ArrayList<Component> components = new ArrayList<Component>();
		
		int btbbrackets = lines.indexOf("][");
		if (btbbrackets == -1) btbbrackets = lines.indexOf("}{");
		ParseError.validate(btbbrackets == -1, lines.lineNOfIndex(btbbrackets), "Encountered illegal brackets at index " + btbbrackets);
		
		int NOTHING = 0;
		
		int beginIndex = 0;
		
		boolean inQuotes = false;
		int curlyCount = 0;
		int current = NOTHING;
		char c;
		boolean atEnd;
		
		for (int i = 0; i <= lines.length(); i++) {
			atEnd = i+1 == lines.length(); // for colon checking
			if (i == lines.length()) {
				ParseError.validate(current == NOTHING || current == CALL_NAME, lines.lineNOfIndex(i), "Expected closing bracket");
				if (current == CALL_NAME) {
					components.add(new Component(current, lines.substring(beginIndex, i), lines.trueIndex(beginIndex), lines.lineNOfIndex(i)));
				}
				break;
			} else {
				c = lines.charAt(i);
			}
			
			if (c <= ' ' && curlyCount == 0) { // Process space or tab IF
				if (	current != PARAMETER && // Not a parameter list
						current != CONF_NODE && // Not a list of conf nodes
						(components.size() == 0 || // At beginning of list
						components.get(components.size() - 1).type != SPACE ||  // or Last one wasn't a space
						current == CALL_NAME) // or we're in a call name
						) {
					if (current == CALL_NAME) {
						components.add(new Component(current, lines.substring(beginIndex, i), lines.trueIndex(beginIndex), lines.lineNOfIndex(i)));
						current = NOTHING;
					}
					components.add(new Component(lines.trueIndex(i), lines.lineNOfIndex(i))); // Add spaces between calls
				}
			} else if (inQuotes && curlyCount == 0) { // We want to ignore other symbols when in quotation marks
				if (c == '"' || c == '\'') {
					inQuotes = false; // Encountered end
				}
			} else if ((c == '"' || c == '\'') && curlyCount == 0) { // If quotes found
				ParseError.validate(
						current == PARAMETER || current == CONF_NODE,
						lines.lineNOfIndex(i),
						"Encountered unexpected \" at index " + lines.trueIndex(i));
				inQuotes = true;
			} else if (c == '[') { // Open params
				if (curlyCount == 0) {
					ParseError.validate(
							current == NOTHING || current == CALL_NAME,
							lines.lineNOfIndex(i),
							"Encountered unexpected [ at index " + lines.trueIndex(i));
					if (current == CALL_NAME)  // End of call
						components.add(new Component(current, lines.substring(beginIndex, i), lines.trueIndex(beginIndex), lines.lineNOfIndex(i)));
					current = PARAMETER;
					beginIndex = i + 1;
				} else {
					curlyCount++;
				}
			} else if (c == '{') { // Open conf nodes
				if (curlyCount == 0) {
					ParseError.validate(
							current == NOTHING || current == CALL_NAME,
							lines.lineNOfIndex(i),
							"Encountered unexpected { at index " + lines.trueIndex(i));
					if (current == CALL_NAME) // End of call
						components.add(new Component(current, lines.substring(beginIndex, i), lines.trueIndex(beginIndex), lines.lineNOfIndex(i)));
					current = CONF_NODE;
					beginIndex = i + 1;
				}
				curlyCount++;
				
			} else if (c == ']') {
				if (curlyCount == 0) {
					ParseError.validate(
							current == PARAMETER,
							lines.lineNOfIndex(i),
							"Encountered unexpected ] at index " + lines.trueIndex(i));
					String item = lines.substring(beginIndex, i);
					components.add(new Component(current, item, lines.trueIndex(beginIndex), lines.lineNOfIndex(i)));
					current = NOTHING;
				} else {
					curlyCount--;
				}
			} else if (c == '}') {
				if (curlyCount <= 1) {
					ParseError.validate(
							current == CONF_NODE,
							lines.lineNOfIndex(i),
							"Encountered unexpected } at index " + lines.trueIndex(i));
					String item = lines.substring(beginIndex, i);
					components.add(new Component(current, item, lines.trueIndex(beginIndex), lines.lineNOfIndex(i)));
					current = NOTHING;
				}
				curlyCount--;
			} else if (c == ',' && curlyCount <= 1) {
				ParseError.validate(
						current == PARAMETER || current == CONF_NODE,
						lines.lineNOfIndex(i),
						"Encountered unexpected , at index " + lines.trueIndex(i));
				components.add(new Component(current, lines.substring(beginIndex, i), lines.trueIndex(beginIndex), lines.lineNOfIndex(i)));
				beginIndex = i + 1;
			} else if (c == ':' && (current == NOTHING || current == CALL_NAME) &&
					(
							(atEnd || lines.charAt(i+1) <= ' ') ||  // Make sure it's not a : or ?: native
							(i == 0 || lines.charAt(i-1) <= ' ')
					) ) {
				if (current == CALL_NAME) { // End of call
					components.add(new Component(current, lines.substring(beginIndex, i), lines.trueIndex(beginIndex), lines.lineNOfIndex(i)));
					current = NOTHING;
				}
				components.add(new Component(COLON, "", lines.trueIndex(i), lines.lineNOfIndex(i)));
			} else { 
				if (current == NOTHING) {
					current = CALL_NAME;
					beginIndex = i;
				}
			}
		}
		return components;
	}

	public boolean isEmpty() {
		return this.lines.length() == 0;
	}
	
}
