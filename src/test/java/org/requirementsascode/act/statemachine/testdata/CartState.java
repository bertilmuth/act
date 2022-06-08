package org.requirementsascode.act.statemachine.testdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class CartState {
	private final List<String> items;
	private final boolean subStateEntered;
	
	private CartState(List<String> items, boolean subStateEntered) {		
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
	
	static CartState cartState(List<String> items, boolean subStateEntered) {
		return new CartState(items, subStateEntered);
	}
	
	static CartState createCart(CreateCart createCart) {
		return cartState(createCart.getItems(), createCart.isSubStateEntered());
	}
	
	CartState addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(getItems());
		items.add(addItem.item());
		return cartState(items, subStateEntered);
	}

	CartState removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(getItems());
		items.remove(removeItem.item());
		return cartState(items, subStateEntered);
	}
	
	CartState removeAllItems(AddItem removeAllItems) {				
		return cartState(Collections.emptyList(), false);
	}
	
	CartState enterSubstate(Trigger trigger) {
		return cartState(getItems(), true);
	}
	
	CartState exitSubstate(Trigger trigger) {
		return cartState(getItems(), false);
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + ", subStateEntered=" + subStateEntered + "]";
	}
}