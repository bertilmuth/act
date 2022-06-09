package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.testdata.HierarchicalCart;
import org.requirementsascode.act.statemachine.testdata.CreateCart;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;

class FlowTest {
	private HierarchicalCart cart;

	@BeforeEach
	void setup() {
		cart = new HierarchicalCart();
		cart.actOn(new CreateCart(false));
	}

	@Test
	void entersSubState() {
		String itemToBeAdded = "SomeItem";

		cart.actOn(new AddItem(itemToBeAdded));
		assertTrue(cart.subStateEntered());
	}

	@Test
	void exitsSubState() {
		String item1 = "Item1";
		cart.actOn(new AddItem(item1));

		cart.actOn(new RemoveItem(item1));
		assertFalse(cart.subStateEntered());
	}
}
