package org.requirementsascode.act.statemachine.testdata;

public class Push implements Value {
		private String text;
				
		public Push(String text) {
			this.text = text;
		}
		
		public String text() {
			return text;
		}
		
		@Override
		public String toString() {
			return "Push [text=" + text + "]";
		}
	};