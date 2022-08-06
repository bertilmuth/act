package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.EntryFlow.entryFlow;
import static org.requirementsascode.act.statemachine.Init.init;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Consume.consume;
import static org.requirementsascode.act.statemachine.Supply.supply;
import static org.requirementsascode.act.statemachine.Transition.transition;
import static org.requirementsascode.act.statemachine.When.when;
import static org.requirementsascode.act.statemachine.WhenInCase.whenInCase;

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
				transition(emptyCartState, nonEmptyCartState, 
					when(AddItem.class, consume(CartState::addItem))),
				
				transition(nonEmptyCartState, nonEmptyCartState, 
					when(AddItem.class, consume(CartState::addItem))),
				
				transition(nonEmptyCartState, nonEmptyCartState, 
					whenInCase(RemoveItem.class, i -> i.state().items().size() > 1, supply(CartState::removeItem), this::validateRemoval)),
				
				transition(nonEmptyCartState, emptyCartState, 
					whenInCase(RemoveItem.class, i -> i.state().items().size() == 1, supply(CartState::removeItem), this::validateRemoval))
			)
			.flows(
				entryFlow(when(CreateCart.class, init(CartState::createCart)))
			)
			.build();
		
		return statemachine;
	}
	
	private void validateRemoval(Data<CartState, RemoveItem> inputData, Data<CartState, RemoveItem> outputData) {
		if(!inputData.value().equals(outputData.value())) {
			throw new IllegalStateException("Item " + inputData.value() +" could not be removed!");
		}
	}
}






