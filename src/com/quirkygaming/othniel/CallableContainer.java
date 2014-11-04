package com.quirkygaming.othniel;

import java.util.HashMap;


public abstract class CallableContainer {
	protected static HashMap<String, Callable> calls = new HashMap<String, Callable>();
	
	public static Callable getCallable(String callable) {
		return calls.get(callable);
	}
}
