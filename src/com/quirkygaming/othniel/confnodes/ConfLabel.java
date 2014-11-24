package com.quirkygaming.othniel.confnodes;

import com.quirkygaming.othniel.Keywords.ConfNodeType;

public class ConfLabel implements ConfNode {
	
	private String token;
	
	public ConfLabel(String token, int nodeIndex) {
		this.token = token;
	}
	
	public String getLabel() {
		return token;
	}
	
	@Override
	public ConfNodeType type() {
		return ConfNodeType.LABEL;
	}
	
}
