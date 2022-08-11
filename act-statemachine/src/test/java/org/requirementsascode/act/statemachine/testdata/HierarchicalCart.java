package org.requirementsascode.act.statemachine.testdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.CreateHierarchicalCart;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class HierarchicalCart {
	private final List<String> items;
	private final boolean subStateEntered;
	
	private HierarchicalCart(List<String> items, boolean subStateEntered) {		
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
	
	static HierarchicalCart cartState(List<String> items, boolean subStateEntered) {
		return new HierarchicalCart(items, subStateEntered);
	}
	
	static HierarchicalCart createCart(CreateHierarchicalCart createCart) {
		return cartState(createCart.items(), createCart.isSubStateEntered());
	}
	
	HierarchicalCart addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		items.add(addItem.item());
		return cartState(items, subStateEntered);
	}

	HierarchicalCart removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		items.remove(removeItem.item());
		return cartState(items, subStateEntered);
	}
	
	HierarchicalCart enterSubstate(Trigger trigger) {
		return cartState(items(), true);
	}
	
	HierarchicalCart exitSubstate(Trigger trigger) {
		return cartState(items(), false);
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + ", subStateEntered=" + subStateEntered + "]";
	}
}