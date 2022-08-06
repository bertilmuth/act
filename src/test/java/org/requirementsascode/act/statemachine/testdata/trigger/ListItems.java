package org.requirementsascode.act.statemachine.testdata.trigger;

import java.util.List;

public class ListItems implements Trigger {
	private List<String> items;

	public ListItems() {
	}
	
	public ListItems(List<String> items) {
		this.items = items;
	}
	
	public List<String> items() {
		return items;
	}
	
	@Override
	public String toString() {
		return "ListItems [items=" + items + "]";
	}
}