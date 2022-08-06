package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.EntryFlow.entryFlow;
import static org.requirementsascode.act.statemachine.Init.init;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Consume.consume;
import static org.requirementsascode.act.statemachine.Transition.transition;
import static org.requirementsascode.act.statemachine.When.when;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class Cart {	
	private CartState state;
	Statemachine<CartState, Trigger> statemachine;

	public Cart() {
		this.statemachine = createStatemachine();
	}
	
	public void actOn(Trigger trigger) {
		Data<CartState, Trigger> input = data(state, trigger);
		
		Data<CartState, Trigger> output = statemachine.actOn(input);
		setState(output.getState());
	}
	
	public List<String> items(){
		return state.getItems();
	}
	
	private void setState(CartState cartState) {
		this.state = cartState;
	}
	
	private Statemachine<CartState, Trigger> createStatemachine() {
		State<CartState, Trigger> emptyCartState = state("Empty Cart", cart -> cart != null && cart.getItems().size() == 0);
		State<CartState, Trigger> nonEmptyCartState = state("Non-Empty Cart", cart -> cart != null && cart.getItems().size() > 0);

		Statemachine<CartState, Trigger> statemachine = Statemachine.builder()
			.states(emptyCartState,nonEmptyCartState)
			.transitions(
				transition(emptyCartState, nonEmptyCartState, 
					when(AddItem.class, consume(CartState::addItem))),
				
				transition(nonEmptyCartState, nonEmptyCartState, 
					when(AddItem.class, consume(CartState::addItem))),
				
				transition(nonEmptyCartState, nonEmptyCartState, 
					when(RemoveItem.class, inCase(i -> i.getState().getItems().size() > 1, consume(CartState::removeItem)))),
				
				transition(nonEmptyCartState, emptyCartState, 
					when(RemoveItem.class, inCase(i -> i.getState().getItems().size() == 1, consume(CartState::removeItem))))
			)
			.flows(
				entryFlow(when(CreateCart.class, init(CartState::createCart)))
			)
			.build();
		
		return statemachine;
	}
}






