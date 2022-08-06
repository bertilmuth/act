package org.requirementsascode.act.statemachine.testdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class HierarchicalCartState {
	private final List<String> items;
	private final boolean subStateEntered;
	
	private HierarchicalCartState(List<String> items, boolean subStateEntered) {		
		this.items = new ArrayList<>(items);
		this.subStateEntered = subStateEntered;
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public List<String> items() {
		return Collections.unmodifiableList(items);
	}
	
	public boolean isSubStateEntered() {
		return subStateEntered;
	}
	
	static HierarchicalCartState cartState(List<String> items, boolean subStateEntered) {
		return new HierarchicalCartState(items, subStateEntered);
	}
	
	static HierarchicalCartState createCart(CreateHierarchicalCart createCart) {
		return cartState(createCart.items(), createCart.isSubStateEntered());
	}
	
	HierarchicalCartState addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		items.add(addItem.item());
		return cartState(items, subStateEntered);
	}

	HierarchicalCartState removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		items.remove(removeItem.item());
		return cartState(items, subStateEntered);
	}
	
	HierarchicalCartState enterSubstate(Trigger trigger) {
		return cartState(items(), true);
	}
	
	HierarchicalCartState exitSubstate(Trigger trigger) {
		return cartState(items(), false);
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + ", subStateEntered=" + subStateEntered + "]";
	}
}