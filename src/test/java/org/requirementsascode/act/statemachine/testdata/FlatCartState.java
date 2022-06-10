package org.requirementsascode.act.statemachine.testdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class FlatCartState {
	private final List<String> items;
	private final boolean subStateEntered;
	
	private FlatCartState(List<String> items, boolean subStateEntered) {		
		this.items = new ArrayList<>(items);
		this.subStateEntered = subStateEntered;
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public List<String> getItems() {
		return Collections.unmodifiableList(items);
	}
	
	public boolean isSubStateEntered() {
		return subStateEntered;
	}
	
	static FlatCartState cartState(List<String> items, boolean subStateEntered) {
		return new FlatCartState(items, subStateEntered);
	}
	
	static FlatCartState createCart(CreateCart createCart) {
		return cartState(createCart.getItems(), createCart.isSubStateEntered());
	}
	
	FlatCartState addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(getItems());
		items.add(addItem.item());
		return cartState(items, subStateEntered);
	}

	FlatCartState removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(getItems());
		items.remove(removeItem.item());
		return cartState(items, subStateEntered);
	}
	
	FlatCartState removeAllItems(AddItem removeAllItems) {				
		return cartState(Collections.emptyList(), false);
	}
	
	FlatCartState enterSubstate(Trigger trigger) {
		return cartState(getItems(), true);
	}
	
	FlatCartState exitSubstate(Trigger trigger) {
		return cartState(getItems(), false);
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + ", subStateEntered=" + subStateEntered + "]";
	}
}