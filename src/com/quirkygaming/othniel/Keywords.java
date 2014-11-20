package com.quirkygaming.othniel;

public class Keywords {
	
	public enum Privacy {
		LOCAL("local"), GLOBAL("global");
		
		private String name;
		private Privacy(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
	public enum StructureType {
		TYPEDEF("type"), STRUCTURE("structure"), DEFINITION("define");
		
		private String name;
		private StructureType(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
	public enum ExecutionMode {
		STATIC("static"), INSTANTIATED("instantiated"), INLINE("inline");
		
		private String name;
		private ExecutionMode(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}

	public enum RunMode {
		SEQUENTIAL("sequence"), PARALLEL("parallel");
		
		private String name;
		private RunMode(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
}
