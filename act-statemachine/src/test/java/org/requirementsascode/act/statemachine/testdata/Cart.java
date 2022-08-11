package org.requirementsascode.act.statemachine.testdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	static Cart cart(List<String> items) {
		return new Cart(items);
	}
	
	static Cart createCart(CreateCart createCart) {
		return cart(createCart.items());
	}
	
	Cart addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		items.add(addItem.item());
		return cart(items);
	}

	Cart removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(items());
		items.remove(removeItem.item());
		return cart(items);
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + "]";
	}
}