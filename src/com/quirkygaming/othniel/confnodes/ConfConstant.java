package com.quirkygaming.othniel.confnodes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Constants;
import com.quirkygaming.othniel.ParseError;
import com.quirkygaming.othniel.Keywords.ConfNodeType;
import com.quirkygaming.othniel.pipes.Pipe;

public class ConfConstant implements ConfNode {
	
	private ConfNodeType t;
	private Pipe p;
	
	public ConfConstant(ConfNodeType type, String token, CachedCall c, int nodeIndex) {
		t = type;
		p = Constants.matchConstant(token, c.getLine());
		ParseError.validate(p != null, c.getLine(), "Conf node constant could not be parsed: " + token);
		ParseError.validate(ConfNodeType.CONSTANT(p.type()).equals(t), c.getLine(), "Incorrect type for configuration node " + nodeIndex);
	}
	
	public Pipe getPipe() {
		return p;
	}
	
	@Override
	public ConfNodeType type() {
		return t;
	}
	
}
