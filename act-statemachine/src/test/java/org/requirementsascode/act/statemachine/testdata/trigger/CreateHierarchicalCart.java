package org.requirementsascode.act.statemachine.testdata.trigger;

import java.util.Arrays;
import java.util.List;

public class CreateHierarchicalCart implements Trigger {
	private final boolean subStateEntered;
	private final List<String> items;

	public CreateHierarchicalCart(boolean subStateEntered, String... items) {
		this.subStateEntered = subStateEntered;
		this.items = Arrays.asList(items);
	}

	public List<String> items() {
		return items;
	}

	public boolean isSubStateEntered() {
		return subStateEntered;
	}	
}
