package com.quirkygaming.othniel.confnodes;

import java.util.ArrayList;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.CallParser;
import com.quirkygaming.othniel.CallParser.ParsedCall;
import com.quirkygaming.othniel.Interpreter;
import com.quirkygaming.othniel.Keywords.ConfNodeType;
import com.quirkygaming.othniel.PipeMap;
import com.quirkygaming.othniel.PipeOwner;
import com.quirkygaming.othniel.Structure;

public class StatementSet implements ConfNode, PipeOwner {
	
	ArrayList<CachedCall> callList = new ArrayList<CachedCall>();
	private final String name;
	private final int nodeIndex;
	
	private final PipeMap pipeDefs;
	
	public StatementSet(String code, CachedCall c, String parentName, Structure parent, int nodeIndex) {
		name = parentName + ".ConfNode" + nodeIndex + "StatementSet";
		this.nodeIndex = nodeIndex;
		this.pipeDefs = new PipeMap(parent.pipeDefs());
		
		CallParser parser = new CallParser();
		parser.addLine(c.getLine(), code);
		parser.parse();
		
		for (ParsedCall call : parser.getCalls()) {
			callList.add(
					Interpreter.parseCall(call, this, call.lineN, callList.size()));
		}
	}

	@Override
	public ConfNodeType type() {
		return ConfNodeType.STATEMENTSET;
	}
	
	public void call() {
		for (CachedCall c : callList) {
			c.call();
		}
	}

	@Override
	public PipeMap pipeDefs() {
		return pipeDefs;
	}

}
