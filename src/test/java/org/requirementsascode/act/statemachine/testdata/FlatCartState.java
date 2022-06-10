package org.requirementsascode.act.statemachine.testdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;

public class FlatCartState {
	private final List<String> items;
	
	private FlatCartState(List<String> items) {		
		this.items = new ArrayList<>(items);
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public List<String> getItems() {
		return Collections.unmodifiableList(items);
	}
	
	static FlatCartState cartState(List<String> items) {
		return new FlatCartState(items);
	}
	
	static FlatCartState createCart(CreateCart createCart) {
		return cartState(createCart.getItems());
	}
	
	FlatCartState addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(getItems());
		items.add(addItem.item());
		return cartState(items);
	}

	FlatCartState removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(getItems());
		items.remove(removeItem.item());
		return cartState(items);
	}

	@Override
	public String toString() {
		return "FlatCartState [items=" + items + "]";
	}
}