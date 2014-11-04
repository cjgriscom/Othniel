package com.quirkygaming.othniel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Interpreter {
	
	public static void main(String[] args) { //TODO remove extra spaces in tokenization
		Natives.initNatives();
		Callable main = cacheFile("test.othsrc").get("test");
		
		main.call(new Pipe[0], new Pipe[0], new CachedCall(new Pipe[0], main, new Pipe[0], -1));
	}
	
	static int lineN;
	
	public static void parseStage(boolean headersOnly, String filename, File file, HashMap<String, Callable> retrievedCalls) {
		lineN = 0; // Reset line number
		try (Scanner in = new Scanner(file)){ // Attempt to read file
			
			Structure newStructure = null;
			while (in.hasNextLine()) {
				
				String line = in.nextLine().trim(); lineN++;
				int commentStart = line.indexOf("//");
				if (commentStart >= 0) line = line.substring(0, commentStart); // Strip comments from line
				if (line.equals("")) continue;
				
				if (!(line.startsWith("inline") || line.startsWith("static"))) { //TODO ew
					if (!headersOnly) {
						ParseError.validate(newStructure != null, lineN, "Expected structure header"); // Enforce header if one hasn't occured yet
						newStructure.callList.add(parseCall(newStructure, line, lineN));
					}
				} else {
					// Header
					newStructure = parseHeader(line, !headersOnly);
					if (headersOnly) {
						CallableContainer.calls.put(newStructure.name(), newStructure);
						retrievedCalls.put(newStructure.name(), newStructure);
					}
					
				}
				
				
			}
		} catch (FileNotFoundException e) {
			System.err.println("Could not find " + filename);
		}
	}
	
	public static HashMap<String, Callable> cacheFile(String filename) {
		File file = new File(filename);
		
		HashMap<String, Callable> retrievedCalls = new HashMap<String, Callable>();
		
		parseStage(true, filename, file, retrievedCalls);
		parseStage(false, filename, file, retrievedCalls);
		
		return retrievedCalls;
	}
	
	public static Structure parseHeader(String line, boolean exists) {
		String[] splitInfo = line.split(" ");
		
		ParseError.validate(splitInfo.length > 2, lineN, "Malformed callable header");
		ParseError.validate(splitInfo[1].equals("seq"), lineN, "Malformed callable header");
		
		Structure.Type t = null;
		
		if (splitInfo[0].equals("inline")) {
			t = Structure.Type.INLINE;
		} else if (splitInfo[0].equals("static")) {
			t = Structure.Type.STATIC;
		}
		
		ParseError.validate(t != null, lineN, "Malformed callable header");
		
		CallParser call = new CallParser(line.substring(splitInfo[0].length() + splitInfo[1].length() + 2).trim(), lineN);
		
		if (exists) return (Structure)CallableContainer.getCallable(call.callName);
		else {
			StructInput[] inputNodes = new StructInput[call.inParams.length];
			StructOutput[] outputNodes = new StructOutput[call.outParams.length];
			for (int i = 0; i < call.inParams.length; i++) {
				inputNodes[i] = new StructInput(call.inParams[i], lineN);
			}
			for (int i = 0; i < call.outParams.length; i++) {
				outputNodes[i] = new StructOutput(call.outParams[i], inputNodes, lineN);
			}
			return new Structure(t, inputNodes, outputNodes, call.callName, lineN);
		}
	}
	
	public static CachedCall parseCall(Structure structure, String line, int lineN) {
		CallParser cp = new CallParser(line, lineN);

		Pipe inPipes[] = new Pipe[cp.inParams.length];
		Pipe outPipes[] = new Pipe[cp.outParams.length];

		Callable nativ = Natives.getCallable(cp.callName);
		ParseError.validate(nativ != null, lineN, "Call not found: " + cp.callName);
		CachedCall currentCall = new CachedCall(inPipes, nativ, outPipes, lineN);
		
		for (int i = 0; i < cp.inParams.length; i++) {
			String token = cp.inParams[i].trim();
			Pipe cnst = Constants.matchConstant(token);
			if (cnst == null) {
				ParseError.validate(structure.pipeDefs.containsKey(token), lineN, "Symbol not recognized: " + token);
				if (structure.pipeDefs.get(token) instanceof Pipe) {
					inPipes[i] = (Pipe)structure.pipeDefs.get(token);
				} else {
					inPipes[i] = new UndefinedPipe(structure.pipeDefs.get(token)); // The structure will handle the replacement
				}
			} else {
				inPipes[i] = cnst;
			}
		}
		
		for (int i = 0; i < cp.outParams.length; i++) {
			String token = cp.outParams[i].trim();
			
			if (structure.pipeDefs.containsKey(token)) {
				if (structure.pipeDefs.get(token) instanceof Pipe) {
					outPipes[i] = (Pipe)structure.pipeDefs.get(token);
				} else {
					outPipes[i] = new UndefinedPipe(structure.pipeDefs.get(token)); // The structure will handle the replacement
				}
			} else {
				ParseError.validate(Constants.matchConstant(token) == null, lineN, "A constant can not be used as an output pipe: " + token);
				Pipe p = nativ.outs[i].newPipe(token, currentCall);
				structure.pipeDefs.put(token, p);
				outPipes[i] = p;
			}
		}
		return currentCall;
		
	}
}
