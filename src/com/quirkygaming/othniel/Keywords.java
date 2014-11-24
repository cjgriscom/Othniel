package com.quirkygaming.othniel;

public class Keywords {
	
	// TODO Maybe add directive keywords, like #VALIDATE{}
	
	public static class ConfNodeType {
		public static ConfNodeType STATEMENTSET = new ConfNodeType("statements");
		public static ConfNodeType PIPETYPE = new ConfNodeType("datatype");
		
		private String name;
		private Object data = null;
		private ConfNodeType(String s) {
			name = s;
		}
		
		public static ConfNodeType CONSTANT(Datatype type) {
			ConfNodeType t = new ConfNodeType("constant");
			t.data = type;
			return t;
		}
		public boolean isConstant() {
			return this.name.equals("constant");
		}
		
		@Override
		public boolean equals(Object other) {
			return this.toString().equals(other.toString());
		}
		
		public String toString() {
			return name + (data == null ? "" : ":" + data.toString());
		}
	}
	
	public static enum Blocks {
		ENDTAG("end");
		
		private String name;
		private Blocks(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
	public static enum Privacy {
		LOCAL("local"), GLOBAL("global");
		
		private String name;
		private Privacy(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
	public static enum StructureType {
		TYPEDEF("typedef"), STRUCTURE("structure"), BLOCKDEF("blockdef");
		
		private String name;
		private StructureType(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
	public static enum ExecutionMode {
		STATIC("static"), INSTANTIATED("instantiated"), INLINE("inline");
		
		private String name;
		private ExecutionMode(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}

	public static enum RunMode {
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
