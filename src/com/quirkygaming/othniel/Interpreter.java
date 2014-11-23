package com.quirkygaming.othniel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.quirkygaming.othniel.CallParser.ParsedCall;
import com.quirkygaming.othniel.Keywords.ExecutionMode;
import com.quirkygaming.othniel.Keywords.RunMode;
import com.quirkygaming.othniel.confnodes.ConfNode;
import com.quirkygaming.othniel.pipes.GarbagePipe;
import com.quirkygaming.othniel.pipes.Terminal;
import com.quirkygaming.othniel.pipes.Pipe;
import com.quirkygaming.othniel.pipes.StructInput;
import com.quirkygaming.othniel.pipes.StructOutput;
import com.quirkygaming.othniel.pipes.UndefinedPipe;

public class Interpreter {
	
	public static void main(String[] args) {
		Natives.initNatives();
		Callable main = cacheFile("test.othsrc").get("test");
		
		main.call(new Pipe[0], new Pipe[0], new CachedCall(new Pipe[0], main, new ConfNode[0], new Pipe[0], -1));
	}

	public static HashMap<String, Callable> cacheFile(String filename) {
		File file = new File(filename);
		
		ArrayList<CallParser> parsers = new ArrayList<>();
		
		HashMap<String, Callable> retrievedCalls = new HashMap<String, Callable>();
		
		try (Scanner in = new Scanner(file)){ // Attempt to read file
			CallParser curParser = new CallParser();
			int lineN = 0;
			while (in.hasNextLine()) {
				
				String line = in.nextLine().trim(); lineN++;
				int commentStart = line.indexOf("//");
				if (commentStart >= 0) line = line.substring(0, commentStart); // Strip comments from line
				if (line.equals("")) continue;
				
				if (!(line.startsWith("instantiated") || 
						line.startsWith("inline") || 
						line.startsWith("static"))) { //TODO ew, use Keywords instead
					ParseError.throwIf(curParser.isEmpty(), lineN, "Expected structure header in " + filename); // Enforce header if one hasn't occured yet
				} else {
					// Encountered new header; stash the previous one
					curParser = new CallParser(); // Start new
					parsers.add(curParser);
				}

				curParser.addLine(lineN, line);
				
			}
		} catch (FileNotFoundException e) {
			System.err.println("Could not find " + filename);
		}
		
		//Done scanning file
		
		// Create structures
		for (CallParser parser : parsers) {
			parser.parse();
			Structure newStructure = parseHeader(parser);
			retrievedCalls.put(newStructure.name(), newStructure);
			parser.attachment = newStructure;
		}
		
		for (CallParser parser : parsers) {
			Structure newStructure = (Structure) parser.attachment;
			for (ParsedCall call : parser.getCalls()) { // Loop through each individual call and add to structure
				newStructure.callList.add(
						parseCall(call, newStructure, call.lineN, newStructure.callList.size()));
			}
		}
		
		return retrievedCalls;
	}
	
	private static Structure parseHeader(CallParser parser) {
		ParseError.validate(parser.size() > 2, parser.firstCall().lineN, "Malformed callable header");
		ParseError.validate(parser.get(0).qualifiesAsKeyword(), parser.firstCall().lineN, "Malformed callable header");
		ParseError.validate(parser.get(1).qualifiesAsKeyword(), parser.firstCall().lineN, "Malformed callable header");
		
		ExecutionMode em = null;
		
		if (parser.get(0).callName.equals("instantiated")) { // TODO switch block
			em = ExecutionMode.INSTANTIATED;
		} else if (parser.get(0).callName.equals("static")) {
			em = ExecutionMode.STATIC;
		} else if (parser.get(0).callName.equals("inline")) {
			em = ExecutionMode.INLINE;
		}
		
		ParseError.validate(em != null, parser.firstCall().lineN, "Malformed callable header (no ExecutionMode indicated)");
		
		RunMode rm = null;
		
		if (parser.get(1).callName.equals("parallel")) {
			rm = RunMode.PARALLEL;
		} else if (parser.get(1).callName.equals("sequence")) {
			rm = RunMode.SEQUENTIAL;
		}
		
		ParseError.validate(rm != null, parser.firstCall().lineN, "Malformed callable header (no RunMode indicated)");
		
		ParsedCall call = parser.get(2);
		parser.removeFirstN(3); // Remove the two keywords and the declaration
		
		StructInput[] inputNodes = new StructInput[call.inParams.length];
		StructOutput[] outputNodes = new StructOutput[call.outParams.length];
		for (int i = 0; i < call.inParams.length; i++) {
			inputNodes[i] = new StructInput(call.inParams[i], inputNodes, call.lineN);
		}
		for (int i = 0; i < call.outParams.length; i++) {
			outputNodes[i] = new StructOutput(call.outParams[i], inputNodes, call.lineN);
		}
		return new Structure(em, rm, inputNodes, outputNodes, call.callName, call.lineN);
	}
	
	public static CachedCall parseCall(ParsedCall call, PipeOwner structure, int lineN, int callN) {
		Callable targetCall = Callable.getCallable(call.callName);

		ParseError.validate(targetCall != null, lineN, "Call not found: " + call.callName);

		ParseError.validate(call.inParams.length == targetCall.inSize() || targetCall.inputsArbitrary(), lineN, "Number of inputs does not match");
		ParseError.validate(call.outParams.length == targetCall.outSize(), lineN, "Number of outputs does not match");
		
		Pipe inPipes[] = new Pipe[call.inParams.length];
		Pipe outPipes[] = new Pipe[call.outParams.length];
		ConfNode confNodes[] = new ConfNode[call.confNodes.length];
		
		CachedCall currentCall = new CachedCall(inPipes, targetCall, confNodes, outPipes, lineN);
		
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
					ParseError.verifySymbolRecognized(structure.pipeDefs().containsKey(token), lineN, token);
					if (structure.pipeDefs().get(token) instanceof Pipe) {
						inPipes[i] = (Pipe)structure.pipeDefs().get(token);
					} else { //instanceof node
						inPipes[i] = new UndefinedPipe((Terminal)structure.pipeDefs().get(token)); // The structure will handle the replacement
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
			} else if (structure.pipeDefs().containsKey(token)) {
				if (structure.pipeDefs().get(token) instanceof Pipe) {
					outPipes[i] = (Pipe)structure.pipeDefs().get(token);
				} else { //instanceof node
					outPipes[i] = new UndefinedPipe((Terminal)structure.pipeDefs().get(token)); // The structure will handle the replacement
				}
			} else {
				ParseError.validate(Constants.matchConstant(token, lineN) == null, lineN, "A constant can not be used as an output pipe: " + token);
				Pipe p = targetCall.getOut(i).getCopy(token, currentCall);
				structure.pipeDefs().put(token, p);
				outPipes[i] = p;
			}
			targetCall.getOut(i).checkCompatWith(outPipes[i], lineN, currentCall);
		}
		return currentCall;
		
	}
}
