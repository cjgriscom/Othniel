package com.quirkygaming.othniel.confnodes;

import com.quirkygaming.othniel.CachedCall;
import com.quirkygaming.othniel.Datatype;
import com.quirkygaming.othniel.Keywords.ConfNodeType;
import com.quirkygaming.othniel.ParseError;

public class ConfPipeType implements ConfNode {
	
	private Datatype t;
	
	public ConfPipeType(String token, CachedCall c, int nodeIndex) {
		t = Datatype.valueOf(token.trim());
		ParseError.validate(t != null, c.getLine(), "Datatype not recognized: " + token + " in configuration node " + nodeIndex);
	}
	
	public Datatype getPipeType() {
		return t;
	}
	
	@Override
	public ConfNodeType type() {
		return ConfNodeType.PIPETYPE;
	}
	
}
