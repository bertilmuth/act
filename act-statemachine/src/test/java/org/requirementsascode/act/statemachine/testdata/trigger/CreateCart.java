package org.requirementsascode.act.statemachine.testdata.trigger;

import java.util.Arrays;
import java.util.List;

public class CreateCart implements Trigger {
	private final List<String> items;

	public CreateCart(String... items) {
		this.items = Arrays.asList(items);
	}

	public List<String> items() {
		return items;
	}
}
