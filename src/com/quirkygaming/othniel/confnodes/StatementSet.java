package com.quirkygaming.othniel.confnodes;

import java.util.ArrayList;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.CallParser;
import com.quirkygaming.othniel.CallParser.ParsedCall;
import com.quirkygaming.othniel.Interpreter;
import com.quirkygaming.othniel.Keywords.ConfNodeType;
import com.quirkygaming.othniel.PipeMap;
import com.quirkygaming.othniel.PipeOwner;

public class StatementSet implements ConfNode, PipeOwner {
	
	ArrayList<CachedCall> callList = new ArrayList<CachedCall>();
	private final String name;
	
	private final PipeMap pipeDefs;
	
	public StatementSet(String code, CachedCall c, PipeOwner parent, int nodeIndex) {
		name = parent.name() + ".ConfNode" + nodeIndex + "StatementSet";
		
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

	@Override
	public String name() {
		return name;
	}

}
