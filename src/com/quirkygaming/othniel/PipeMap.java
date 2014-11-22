package com.quirkygaming.othniel;

import java.util.HashMap;

import com.quirkygaming.othniel.pipes.PipeDef;

public class PipeMap {
	
	private HashMap<String, PipeDef> localPipes = new HashMap<>();
	
	private PipeMap greaterScope = null;
	
	public PipeMap() {}
	
	public PipeMap(PipeMap scope) {
		greaterScope = scope;
	}
	
	public boolean containsKey(String pipeName) {
		if (localPipes.containsKey(pipeName)) {
			return true;
		} else {
			if (greaterScope != null) {
				return greaterScope.containsKey(pipeName);
			} else {
				return false;
			}
		}
	}
	
	public int size() {
		int size = localPipes.size();
		if (greaterScope != null) {
			size += greaterScope.size();
		}
		return size;
	}
	
	public PipeDef get(String pipeName) {
		if (localPipes.containsKey(pipeName)) {
			return localPipes.get(pipeName);
		} else {
			if (greaterScope != null) {
				return greaterScope.get(pipeName);
			} else {
				return null;
			}
		}
	}
	
	public void put(String pipeName, PipeDef pipe) {
		this.localPipes.put(pipeName, pipe);
	}
	
}
