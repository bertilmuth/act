package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.ConsumeWith.consumeWith;
import static org.requirementsascode.act.statemachine.EntryFlow.entryFlow;
import static org.requirementsascode.act.statemachine.Init.init;
import static org.requirementsascode.act.statemachine.State.anyState;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.SupplyWith.supplyWith;
import static org.requirementsascode.act.statemachine.Transition.transition;
import static org.requirementsascode.act.statemachine.When.when;
import static org.requirementsascode.act.statemachine.WhenInCase.whenInCase;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.CreateCart;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class CartStateMachineWithValidation {	
	private Cart cart;
	Statemachine<Cart, Trigger> statemachine;

	public CartStateMachineWithValidation() {
		this.statemachine = createStatemachine();
	}
	
	public Data<Cart, Trigger> actOn(Trigger trigger) {
		Data<Cart, Trigger> input = data(cart, trigger);
		
		Data<Cart, Trigger> output = statemachine.actOn(input);
		setState(output.state());
		
		return output;
	}
	
	public List<String> items(){
		return cart.items();
	}
	
	private void setState(Cart cartState) {
		this.cart = cartState;
	}
	
	private Statemachine<Cart, Trigger> createStatemachine() {
		State<Cart, Trigger> emptyCartState = state("Empty Cart", cart -> cart != null && cart.items().size() == 0);
		State<Cart, Trigger> nonEmptyCartState = state("Non-Empty Cart", cart -> cart != null && cart.items().size() > 0);

		Statemachine<Cart, Trigger> statemachine = Statemachine.builder()
			.states(emptyCartState,nonEmptyCartState)
			.transitions(
				transition(anyState(), nonEmptyCartState, 
					when(AddItem.class, consumeWith(Cart::addItem))),
				
				transition(nonEmptyCartState, nonEmptyCartState, 
					whenInCase(RemoveItem.class, i -> i.state().items().size() > 1, supplyWith(Cart::removeItem)
						.andHandleChange(this::validateRemoveItem))),
				
				transition(nonEmptyCartState, emptyCartState, 
					whenInCase(RemoveItem.class, i -> i.state().items().size() == 1, supplyWith(Cart::removeItem)
						.andHandleChange(this::validateRemoveItem)))
			)
			.flows(
				entryFlow(when(CreateCart.class, init(Cart::createCart)))
			)
			.build();
		
		return statemachine;
	}
	
	private void validateRemoveItem(Data<Cart, RemoveItem> before, Data<Cart, RemoveItem> after) {
		if(!before.value().equals(after.value())) {
			throw new IllegalStateException("Item " + before.value() +" could not be removed!");
		}
	}
}






