package org.requirementsascode.act.statemachine.pbt.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
	public interface Value { }
	public record CreateCart() implements Value { }
	public record AddItem(String item) implements Value { }
	public record RemoveItem(String item) implements Value {}	
	
	private final List<String> items;
	
	private Cart(List<String> items) {		
		this.items = new ArrayList<>(items);
	}
	
	public List<String> distinctItems() {
		return items.stream().distinct().toList();
	}
	
	static Cart cart(List<String> items) {
		return new Cart(items);
	}
	
	static Cart create(CreateCart createCart) {
		return cart(Collections.emptyList());
	}
	
	Cart addItem(AddItem addItem) {		
		ArrayList<String> items = new ArrayList<>(distinctItems());
		items.add(addItem.item());
		return cart(items);
	}

	Cart removeItem(RemoveItem removeItem) {		
		ArrayList<String> items = new ArrayList<>(distinctItems());
		items.remove(removeItem.item());
		return cart(items);
	}

	@Override
	public String toString() {
		return "CartState [items=" + items + "]";
	}
}