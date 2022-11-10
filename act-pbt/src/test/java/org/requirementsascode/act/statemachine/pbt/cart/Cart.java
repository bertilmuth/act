package org.requirementsascode.act.statemachine.pbt.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Cart {
	public static interface Value { }
	public static class CreateCart implements Value { }
	public static class AddItem implements Value { 
		private final String item;

		AddItem(String item){
			this.item = item;
		}
		
		public String item() {
			return item;
		}
	}
	public static class RemoveItem implements Value {
		private final String item;

		RemoveItem(String item){
			this.item = item;
		}
		
		public String item() {
			return item;
		}
	}	
	
	private final List<String> items;
	
	private Cart(List<String> items) {		
		this.items = new ArrayList<>(items);
	}
	
	public List<String> items() {
		return items.stream().distinct().collect(Collectors.toList());
	}
	
	public long quantityOf(String item) {
		return items.stream().filter(item::equals).count();
	}
	
	static Cart cart(List<String> items) {
		return new Cart(items);
	}
	
	static Cart create(CreateCart createCart) {
		return cart(Collections.emptyList());
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