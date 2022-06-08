package org.requirementsascode.act.statemachine.testdata.trigger;

public class AddItem implements Trigger {
	private final String item;

	public AddItem(String item) {
		this.item = item;
	}
	
	public String item() {
		return item;
	}

	@Override
	public String toString() {
		return "AddItem [item=" + item + "]";
	}
}