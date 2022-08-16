package org.requirementsascode.act.statemachine.pbt.cart;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.pbt.StateMachineEvent.stateMachineEvent;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.AddItem;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.CreateCart;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.RemoveItem;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.Value;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.lifecycle.BeforeProperty;
import net.jqwik.api.state.ActionChain;

/**
 * This class uses the jqwik library for property based testing
 * to generate randomized event instances (jqwik calls them "actions").
 * 
 * The test then fires these events at a state machine, that controls the behavior of a shopping cart.
 * (see https://github.com/bertilmuth/act/blob/main/act-pbt/src/test/java/org/requirementsascode/act/statemachine/pbt/cart/CartStateMachine.java)
 * 
 * The properties to test are attached to the transitions of the state machine.
 * That's why this test class doesn't need assert statements.
 * 
 * @author BertilMuth
 *
 */
class CartStateMachineTest {
	private Behavior<Cart,Value,Value> stateMachine;
	private Cart initialState;
	
	@BeforeProperty
	void setup() {
		stateMachine = new CartStateMachine();
		Data<Cart, Value> createCartFromScratch = data(null, new CreateCart());
		initialState = stateMachine.actOn(createCartFromScratch).state();
	}
	
	@Property
	void checkCart(@ForAll("myCartActions") ActionChain<Cart> chain) {
		chain.run();
	}
	
	@Provide
	Arbitrary<ActionChain<Cart>> myCartActions() {		
		Arbitrary<AddItem> addItems = Arbitraries.strings().alpha().ofMaxLength(5).map(AddItem::new);
		Arbitrary<RemoveItem> removeItems = Arbitraries.strings().alpha().ofMaxLength(5).map(RemoveItem::new);
		
		return ActionChain.startWith(() -> initialState)
		  .addAction(stateMachineEvent(addItems, stateMachine))
		  .addAction(stateMachineEvent(removeItems, stateMachine));
	}
}

