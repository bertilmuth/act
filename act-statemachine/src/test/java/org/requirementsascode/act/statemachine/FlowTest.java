package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.testdata.HierarchicalCartStateMachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.CreateHierarchicalCart;

class FlowTest {
	private HierarchicalCartStateMachine cart;

	@BeforeEach
	void setup() {
		cart = new HierarchicalCartStateMachine();
		cart.actOn(new CreateHierarchicalCart(false));
	}

	@Test
	void entersSubState() {
		String itemToBeAdded = "SomeItem";

		cart.actOn(new AddItem(itemToBeAdded));
		assertTrue(cart.subStateEntered());
	}
}
