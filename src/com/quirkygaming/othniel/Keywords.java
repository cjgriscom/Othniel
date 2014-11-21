package com.quirkygaming.othniel;

public class Keywords {
	
	// TODO Maybe add directive keywords, like #VALIDATE{}
	
	public enum Blocks {
		ENDTAG("end");
		
		private String name;
		private Blocks(String s) {
			name = s;
		}
		public String toString() {
			return name;
		}
	}
	
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
		TYPEDEF("typedef"), STRUCTURE("structure"), BLOCKDEF("blockdef");
		
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
