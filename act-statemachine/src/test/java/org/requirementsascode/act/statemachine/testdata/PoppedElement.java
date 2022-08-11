package org.requirementsascode.act.statemachine.testdata;

public class PoppedElement implements Value {
	private String text;

	public PoppedElement(String text) {
		this.text = text;
	}

	public String text() {
		return text;
	}

	@Override
	public String toString() {
		return "PoppedElement [text=" + text + "]";
	}
};