package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.testdata.CartStateMachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.CreateCart;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;

class CartStateMachineTest {
	private CartStateMachine stateMachine;

	@BeforeEach
	void setup() {
		stateMachine = new CartStateMachine();
		stateMachine.actOn(new CreateCart());
	}

	@Test
	void addsItemToEmptyCart() {
		String itemToBeAdded = "SomeItem";
		
		stateMachine.actOn(new AddItem(itemToBeAdded));
		assertEquals(1, stateMachine.items().size());
		assertEquals(itemToBeAdded, stateMachine.items().get(0));
	}
	
	@Test
	void removesItemFromEmptyCartThatsNotInIt() {		
		stateMachine.actOn(new RemoveItem("ItemThatsNotInCart"));
		assertTrue(stateMachine.items().isEmpty());
	}
	
	@Test
	void removesItemFromNonEmptyCartThatsNotInIt() {
		String itemToBeAdded = "SomeItem";
		
		stateMachine.actOn(new AddItem(itemToBeAdded));
		stateMachine.actOn(new RemoveItem("ItemThatsNotInCart"));
		assertEquals(1, stateMachine.items().size());
		assertEquals(itemToBeAdded, stateMachine.items().get(0));
	}
	
	@Test
	void addsSecondItemToCart() {
		String item1 = "Item1";
		String item2 = "Item2";
		
		stateMachine.actOn(new AddItem(item1));
		stateMachine.actOn(new AddItem(item2));
		
		assertEquals(2, stateMachine.items().size());
		assertEquals(item1, stateMachine.items().get(0));
		assertEquals(item2, stateMachine.items().get(1));
	}
	
	@Test
	void removesItemFromCart() {	
		String itemToBeRemoved = "Item";
		stateMachine.actOn(new AddItem(itemToBeRemoved));
		
		stateMachine.actOn(new RemoveItem(itemToBeRemoved));
		assertTrue(stateMachine.items().isEmpty());
	}
	
	@Test
	void removesTwoItemsFromCart() {
		String item1 = "Item1";
		String item2 = "Item2";
		stateMachine.actOn(new AddItem(item1));
		stateMachine.actOn(new AddItem(item2));
		
		stateMachine.actOn(new RemoveItem(item1));
		stateMachine.actOn(new RemoveItem(item2));
		assertTrue(stateMachine.items().isEmpty());
	}
	
	@Test
	void removesFirstItemFromCart() {
		String item1 = "Item1";
		String item2 = "Item2";
		stateMachine.actOn(new AddItem(item1));
		stateMachine.actOn(new AddItem(item2));
		
		stateMachine.actOn(new RemoveItem(item1));
		assertEquals(1, stateMachine.items().size());
		assertEquals(item2, stateMachine.items().get(0));
	}
}

