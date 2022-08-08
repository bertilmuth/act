package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.CreateCart;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;

public class Cart {
	private final List<String> items;
	
	private Cart(List<String> items) {		
		this.items = new ArrayList<>(items);
	}
	
	public List<String> items() {
		return Collections.unmodifiableList(items);
	}
	
	static Cart cartState(List<String> items) {
		return new Cart(items);
	}
	
	static Cart createCart(CreateCart createCart) {
		return cartState(createCart.items());
	}
	
	Cart addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		items.add(addItem.item());
		return cartState(items);
	}

	Data<Cart, RemoveItem> removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		boolean actuallyRemoved = items.remove(removeItem.item());
		RemoveItem removedItem = actuallyRemoved? removeItem : null;
		return data(cartState(items), removedItem);
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + "]";
	}
}