package org.requirementsascode.act.statemachine.testdata;

import java.util.Arrays;
import java.util.List;

import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class CreateCart implements Trigger {
	private final List<String> items;

	public CreateCart(String... items) {
		this.items = Arrays.asList(items);
	}

	public List<String> items() {
		return items;
	}
}
