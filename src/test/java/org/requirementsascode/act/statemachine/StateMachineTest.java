package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.testdata.Cart;
import org.requirementsascode.act.statemachine.testdata.CreateCart;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;

class StateMachineTest {
	private Cart cart;

	@BeforeEach
	void setup() {
		cart = new Cart();
		cart.actOn(new CreateCart());
	}

	@Test
	void addsItemToEmptyCart() {
		String itemToBeAdded = "SomeItem";
		
		cart.actOn(new AddItem(itemToBeAdded));
		assertEquals(1, cart.items().size());
		assertEquals(itemToBeAdded, cart.items().get(0));
	}
	
	@Test
	void addsSecondItemToCart() {
		String item1 = "Item1";
		String item2 = "Item2";
		
		cart.actOn(new AddItem(item1));
		cart.actOn(new AddItem(item2));
		
		assertEquals(2, cart.items().size());
		assertEquals(item1, cart.items().get(0));
		assertEquals(item2, cart.items().get(1));
	}
	
	@Test
	void removesItemFromCart() {	
		String itemToBeRemoved = "Item";
		cart.actOn(new AddItem(itemToBeRemoved));
		
		cart.actOn(new RemoveItem(itemToBeRemoved));
		assertTrue(cart.items().isEmpty());
	}
	
	@Test
	void removesTwoItemsFromCart() {
		String item1 = "Item1";
		String item2 = "Item2";
		cart.actOn(new AddItem(item1));
		cart.actOn(new AddItem(item2));
		
		cart.actOn(new RemoveItem(item1));
		cart.actOn(new RemoveItem(item2));
		assertTrue(cart.items().isEmpty());
	}
	
	@Test
	void removesFirstItemFromCart() {
		String item1 = "Item1";
		String item2 = "Item2";
		cart.actOn(new AddItem(item1));
		cart.actOn(new AddItem(item2));
		
		cart.actOn(new RemoveItem(item1));
		assertEquals(1, cart.items().size());
		assertEquals(item2, cart.items().get(0));
	}
}

