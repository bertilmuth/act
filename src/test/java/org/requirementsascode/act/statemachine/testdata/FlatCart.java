package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.EntryFlow.entryFlow;
import static org.requirementsascode.act.statemachine.Init.init;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Transit.transit;
import static org.requirementsascode.act.statemachine.Transition.transition;
import static org.requirementsascode.act.statemachine.When.when;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class FlatCart {	
	private FlatCartState state;
	Statemachine<FlatCartState, Trigger> statemachine;

	public FlatCart() {
		this.statemachine = createStatemachine();
	}
	
	public void actOn(Trigger trigger) {
		Data<FlatCartState, Trigger> input = data(state, trigger);
		
		Data<FlatCartState, Trigger> output = statemachine.actOn(input);
		if(output != null) {
			setState(output.getState());
		}
	}
	
	public List<String> items(){
		return state.getItems();
	}
	
	private void setState(FlatCartState cartState) {
		this.state = cartState;
	}
	
	private Statemachine<FlatCartState, Trigger> createStatemachine() {
		State<FlatCartState, Trigger> emptyCartState = state("Empty Cart", cart -> cart != null && cart.getItems().size() == 0);
		State<FlatCartState, Trigger> nonEmptyCartState = state("Non-Empty Cart", cart -> cart != null && cart.getItems().size() > 0);

		Statemachine<FlatCartState, Trigger> statemachine = Statemachine.builder()
			.states(emptyCartState,nonEmptyCartState)
			.transitions(
				transition(emptyCartState, nonEmptyCartState, 
					when(AddItem.class, transit(FlatCartState::addItem))),
				
				transition(nonEmptyCartState, nonEmptyCartState, 
					when(AddItem.class, transit(FlatCartState::addItem))),
				
				transition(nonEmptyCartState, nonEmptyCartState, 
					when(RemoveItem.class, inCase(i -> i.getState().getItems().size() > 1, transit(FlatCartState::removeItem)))),
				
				transition(nonEmptyCartState, emptyCartState, 
					when(RemoveItem.class, inCase(i -> i.getState().getItems().size() == 1, transit(FlatCartState::removeItem))))
			)
			.flows(
				entryFlow(when(CreateCart.class, init(FlatCartState::createCart)))
			)
			.build();
		
		return statemachine;
	}
}






