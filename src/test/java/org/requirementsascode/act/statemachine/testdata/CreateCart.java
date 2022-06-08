package org.requirementsascode.act.statemachine.testdata;

import java.util.Arrays;
import java.util.List;

import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class CreateCart implements Trigger {
	private final boolean subStateEntered;
	private final List<String> items;

	public CreateCart(boolean subStateEntered, String... items) {
		this.subStateEntered = subStateEntered;
		this.items = Arrays.asList(items);
	}

	public List<String> getItems() {
		return items;
	}

	public boolean isSubStateEntered() {
		return subStateEntered;
	}	
}
