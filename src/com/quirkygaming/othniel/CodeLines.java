package com.quirkygaming.othniel;

import java.util.TreeMap;

public class CodeLines implements CharSequence {
	
	private String contents = "";
	private TreeMap<Integer, Integer> lineNMap = new TreeMap<>();
	
	public CodeLines() {}
	
	public void addLine(int lineN, String line) {
		lineNMap.put(contents.length(), lineN);
		if (contents.length() != 0) contents += " ";
		contents += line.trim();
	}
	
	public int lineNOfIndex(int index) {
		Integer lineBase = lineNMap.floorKey(index);
		return lineBase == null ? -1 : lineNMap.get(lineBase);
	}
	
	public int trueIndex(int index) { // TODO deal with whitespace
		Integer lineBase = lineNMap.floorKey(index);
		return lineBase == null ? -1 : index - lineBase;
	}
	
	@Override
	public int length() {
		return contents.length();
	}

	@Override
	public char charAt(int paramInt) {
		return contents.charAt(paramInt);
	}
	
	public int indexOf(String seq) {
		return contents.indexOf(seq);
	}

	@Override
	public String subSequence(int paramInt1, int paramInt2) {
		return contents.substring(paramInt1, paramInt2);
	}
	
	public String substring(int paramInt1, int paramInt2) {
		return subSequence(paramInt1, paramInt2);
	}
	
	@Override
	public String toString() {
		return this.contents;
	}

}
