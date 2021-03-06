package org.requirementsascode.act.statemachine.testdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;

public class CartState {
	private final List<String> items;
	
	private CartState(List<String> items) {		
		this.items = new ArrayList<>(items);
	}
	
	public List<String> getItems() {
		return Collections.unmodifiableList(items);
	}
	
	static CartState cartState(List<String> items) {
		return new CartState(items);
	}
	
	static CartState createCart(CreateCart createCart) {
		return cartState(createCart.getItems());
	}
	
	CartState addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(getItems());
		items.add(addItem.item());
		return cartState(items);
	}

	CartState removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(getItems());
		items.remove(removeItem.item());
		return cartState(items);
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + "]";
	}
}