package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.ListItems;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;

public class CartState {
	private final List<String> items;
	
	private CartState(List<String> items) {		
		this.items = new ArrayList<>(items);
	}
	
	public List<String> items() {
		return Collections.unmodifiableList(items);
	}
	
	static CartState cartState(List<String> items) {
		return new CartState(items);
	}
	
	static CartState createCart(CreateCart createCart) {
		return cartState(createCart.items());
	}
	
	CartState addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		items.add(addItem.item());
		return cartState(items);
	}

	Data<CartState, RemoveItem> removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		boolean actuallyRemoved = items.remove(removeItem.item());
		RemoveItem removedItem = actuallyRemoved? removeItem : null;
		return data(cartState(items), removedItem);
	}
	
	Data<CartState,ListItems> listItems(ListItems listItems) {		
		Data<CartState, ListItems> data = data(cartState(items), new ListItems(items()));
		return data;
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + "]";
	}
}