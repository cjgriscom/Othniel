package com.quirkygaming.othniel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import com.quirkygaming.othniel.CallParser.ParsedCall;
import com.quirkygaming.othniel.pipes.GarbagePipe;
import com.quirkygaming.othniel.pipes.Node;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;
import com.quirkygaming.othniel.pipes.UndefinedPipe;

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
				
				if (!(line.startsWith("instantiated") || 
						line.startsWith("inline") || 
						line.startsWith("static"))) { //TODO ew
					if (!headersOnly) {
						ParseError.validate(newStructure != null, lineN, "Expected structure header"); // Enforce header if one hasn't occured yet
						
						CallParser parser = new CallParser(line, lineN, false);
						
						for (ParsedCall call : parser.getCalls()) { // Loop through each individual call and add to structure
							newStructure.callList.add(
									parseCall(call, newStructure, line, lineN, newStructure.callList.size()));
						}
						
					}
				} else {
					// Header
					newStructure = parseHeader(line, !headersOnly);
					if (headersOnly) {
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
		
		Structure.ExecutionMode em = null;
		
		if (splitInfo[0].equals("instantiated")) {
			em = Structure.ExecutionMode.INSTANTIATED;
		} else if (splitInfo[0].equals("static")) {
			em = Structure.ExecutionMode.STATIC;
		} else if (splitInfo[0].equals("inline")) {
			em = Structure.ExecutionMode.INLINE;
		}
		
		ParseError.validate(em != null, lineN, "Malformed callable header");
		
		Structure.RunMode rm = null;
		
		if (splitInfo[1].equals("parallel")) {
			rm = Structure.RunMode.PARALLEL;
		} else if (splitInfo[1].equals("sequence")) {
			rm = Structure.RunMode.SEQUENTIAL;
		}
		
		ParseError.validate(rm != null, lineN, "Malformed callable header");
		
		ParsedCall call = new CallParser(line.substring(splitInfo[0].length() + splitInfo[1].length() + 2).trim(), lineN, true).firstCall();
		
		if (exists) return (Structure)Callable.getCallable(call.callName);
		else {
			StructInput[] inputNodes = new StructInput[call.inParams.length];
			StructOutput[] outputNodes = new StructOutput[call.outParams.length];
			for (int i = 0; i < call.inParams.length; i++) {
				inputNodes[i] = new StructInput(call.inParams[i], inputNodes, lineN);
			}
			for (int i = 0; i < call.outParams.length; i++) {
				outputNodes[i] = new StructOutput(call.outParams[i], inputNodes, lineN);
			}
			return new Structure(em, rm, inputNodes, outputNodes, call.callName, lineN);
		}
	}
	
	public static CachedCall parseCall(ParsedCall call, Structure structure, String line, int lineN, int callN) {
		Callable targetCall = Callable.getCallable(call.callName);

		ParseError.validate(targetCall != null, lineN, "Call not found: " + call.callName);

		ParseError.validate(call.inParams.length == targetCall.inSize() || targetCall.inputsArbitrary(), lineN, "Number of inputs does not match");
		ParseError.validate(call.outParams.length == targetCall.outSize(), lineN, "Number of outputs does not match");
		
		Pipe inPipes[] = new Pipe[call.inParams.length];
		Pipe outPipes[] = new Pipe[call.outParams.length];
		
		CachedCall currentCall = new CachedCall(inPipes, targetCall, outPipes, lineN);
		
		int refInOccurance = 0; // For < and >
		int refOutOccurance = 0; // For < and >
		
		for (int i = 0; i < call.inParams.length; i++) {
			String token = call.inParams[i].trim();
			ParseError.throwIf(token.equals("^"), lineN, "^ not allowed in inputs");
			ParseError.throwIf(token.startsWith(">"), lineN, "> not allowed in inputs");
			if (token.equals("<")) {
				token = "<" + (callN-1) + "." + refInOccurance; // Reference > from last call
				refInOccurance++;
			}
			
			if (token.equals("?")) { // Optional pipe
				inPipes[i] = null;
				ParseError.validate(targetCall.getIn(i).isOptional(), lineN, "Param " + i + " is not optional for " + call.callName);
			} else {
				Pipe cnst = Constants.matchConstant(token, lineN);
				if (cnst == null) {
					ParseError.verifySymbolRecognized(structure.pipeDefs.containsKey(token), lineN, token);
					if (structure.pipeDefs.get(token) instanceof Pipe) {
						inPipes[i] = (Pipe)structure.pipeDefs.get(token);
					} else { //instanceof node
						inPipes[i] = new UndefinedPipe((Node)structure.pipeDefs.get(token)); // The structure will handle the replacement
					}
				} else {
					inPipes[i] = cnst;
				}
				targetCall.getIn(i).checkCompatWith(inPipes[i], lineN, currentCall);
			}
			
		}
		
		for (int i = 0; i < call.outParams.length; i++) {
			String token = call.outParams[i].trim();
			
			ParseError.throwIf(token.equals("?"), lineN, "? not allowed in outputs");
			ParseError.throwIf(token.equals("<"), lineN, "< not allowed in outputs");
			if (token.equals(">")) {
				token = "<" + (callN) + "." + refOutOccurance; // Create pipe reference
				refOutOccurance++;
			}
			
			if (token.equals(GarbagePipe.INSTANCE.getLabel())) {
				outPipes[i] = GarbagePipe.INSTANCE;
			} else if (structure.pipeDefs.containsKey(token)) {
				if (structure.pipeDefs.get(token) instanceof Pipe) {
					outPipes[i] = (Pipe)structure.pipeDefs.get(token);
				} else { //instanceof node
					outPipes[i] = new UndefinedPipe((Node)structure.pipeDefs.get(token)); // The structure will handle the replacement
				}
			} else {
				ParseError.validate(Constants.matchConstant(token, lineN) == null, lineN, "A constant can not be used as an output pipe: " + token);
				Pipe p = targetCall.getOut(i).getCopy(token, currentCall);
				structure.pipeDefs.put(token, p);
				outPipes[i] = p;
			}
			targetCall.getOut(i).checkCompatWith(outPipes[i], lineN, currentCall); // TODO why did it say 0?
		}
		return currentCall;
		
	}
}
