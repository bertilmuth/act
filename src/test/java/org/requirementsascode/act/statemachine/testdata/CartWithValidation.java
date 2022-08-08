package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.ConsumeWith.consumeWith;
import static org.requirementsascode.act.statemachine.EntryFlow.entryFlow;
import static org.requirementsascode.act.statemachine.Init.init;
import static org.requirementsascode.act.statemachine.State.*;
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
import org.requirementsascode.act.statemachine.testdata.trigger.ListItems;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class CartWithValidation {	
	private CartState state;
	Statemachine<CartState, Trigger> statemachine;

	public CartWithValidation() {
		this.statemachine = createStatemachine();
	}
	
	public Data<CartState, Trigger> actOn(Trigger trigger) {
		Data<CartState, Trigger> input = data(state, trigger);
		
		Data<CartState, Trigger> output = statemachine.actOn(input);
		setState(output.state());
		
		return output;
	}
	
	public List<String> items(){
		return state.items();
	}
	
	private void setState(CartState cartState) {
		this.state = cartState;
	}
	
	private Statemachine<CartState, Trigger> createStatemachine() {
		State<CartState, Trigger> emptyCartState = state("Empty Cart", cart -> cart != null && cart.items().size() == 0);
		State<CartState, Trigger> nonEmptyCartState = state("Non-Empty Cart", cart -> cart != null && cart.items().size() > 0);

		Statemachine<CartState, Trigger> statemachine = Statemachine.builder()
			.states(emptyCartState,nonEmptyCartState)
			.transitions(
				transition(anyState(), nonEmptyCartState, 
					when(AddItem.class, consumeWith(CartState::addItem))),
				
				transition(nonEmptyCartState, nonEmptyCartState, 
					whenInCase(RemoveItem.class, i -> i.state().items().size() > 1, supplyWith(CartState::removeItem)
						.andHandleChange(this::validateRemoveItem))),
				
				transition(nonEmptyCartState, emptyCartState, 
					whenInCase(RemoveItem.class, i -> i.state().items().size() == 1, supplyWith(CartState::removeItem)
						.andHandleChange(this::validateRemoveItem))),
				
				transition(anyState(), anyState(), 
					when(ListItems.class, supplyWith(CartState::listItems)
						.andHandleChange(this::validateListItems)))
			)
			.flows(
				entryFlow(when(CreateCart.class, init(CartState::createCart)))
			)
			.build();
		
		return statemachine;
	}
	
	private void validateRemoveItem(Data<CartState, RemoveItem> before, Data<CartState, RemoveItem> after) {
		if(!before.value().equals(after.value())) {
			throw new IllegalStateException("Item " + before.value() +" could not be removed!");
		}
	}
	
	private void validateListItems(Data<CartState, ListItems> before, Data<CartState, ListItems> after) {
		if(!after.value().items().equals(items())) {
			throw new IllegalStateException("Items not correct!");
		}
	}
}






