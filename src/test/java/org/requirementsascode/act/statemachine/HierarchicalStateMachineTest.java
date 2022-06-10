package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.testdata.HierarchicalCart;
import org.requirementsascode.act.statemachine.testdata.CreateHierarchicalCart;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;

class HierarchicalStateMachineTest {
	private HierarchicalCart cart;

	@BeforeEach
	void setup() {
		cart = new HierarchicalCart();
		cart.actOn(new CreateHierarchicalCart(false));
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
	void addingThirdItemToCartHasNoEffectCauseCartIsFull() {
		String item1 = "Item1";
		String item2 = "Item2";
		String item3 = "Item3";
		
		cart.actOn(new AddItem(item1));
		cart.actOn(new AddItem(item2));
		cart.actOn(new AddItem(item3));

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
	
	@Test
	void transitionToStateMustNotBreakItsInvariant() {
		String item = HierarchicalCart.INVARIANT_BREAKING_ITEM;
		cart = new HierarchicalCart();
		cart.actOn(new CreateHierarchicalCart(true, item, item, item));
				
		assertThrows(IllegalStateException.class, () -> cart.actOn(new RemoveItem(item)));
	}
}

