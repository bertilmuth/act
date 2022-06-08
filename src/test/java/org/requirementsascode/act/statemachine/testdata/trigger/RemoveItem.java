package org.requirementsascode.act.statemachine.testdata.trigger;

public class RemoveItem implements Trigger {
	private final String item;

	public RemoveItem(String item) {
		this.item = item;
	}
	
	public String item() {
		return item;
	}

	@Override
	public String toString() {
		return "RemoveItem [item=" + item + "]";
	}
}	