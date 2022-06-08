package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.EntryFlow.entryFlow;
import static org.requirementsascode.act.statemachine.ExitFlow.exitFlow;
import static org.requirementsascode.act.statemachine.Init.init;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Transit.transit;
import static org.requirementsascode.act.statemachine.Transition.transition;
import static org.requirementsascode.act.statemachine.When.when;

import java.util.Arrays;
import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.ConflictingTrigger;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class Cart {	
	public static final String INVARIANT_BREAKING_ITEM = "InvariantBreakingItem";

	private CartState state;
	Statemachine<CartState, Trigger> statemachine;

	public Cart() {
		this.statemachine = createStatemachine();
	}
	
	public void actOn(Trigger trigger) {
		Data<CartState, Trigger> input = data(state, trigger);
		
		Data<CartState, Trigger> output = statemachine.actOn(input);
		if(output != null) {
			setState(output.getState());
		}
	}
	
	public boolean subStateEntered(){
		return state.isSubStateEntered();
	}
	
	public List<String> items(){
		return state.getItems();
	}
	
	private void setState(CartState cartState) {
		this.state = cartState;
	}
	
	private Statemachine<CartState, Trigger> createStatemachine() {
		State<CartState, Trigger> emptyCartState = state("Empty Cart", cs -> cs != null && cs.isEmpty());

		State<CartState, Trigger> nonEmptyCartState = createNonEmptyCartStateWithSubstates();

		Statemachine<CartState, Trigger> statemachine = Statemachine.builder()
			.states(emptyCartState,nonEmptyCartState)
			.transitions(
				transition(emptyCartState, nonEmptyCartState, when(AddItem.class, transit(CartState::addItem))),
				transition(emptyCartState, nonEmptyCartState, when(RemoveItem.class, transit((s,t) -> {throw new RuntimeException("RemoveItem not expected");}))),
				transition(nonEmptyCartState, emptyCartState, when(RemoveItem.class, inCase(i -> i.getState().getItems().size() == 1, transit(CartState::removeItem)))),
				
				// The following is a deliberately invalid transition, since removing 1 item from  
				// a cart that contains 3 item doesn't make the cart empty.
				// This is tested by method transitionToStateMustNotBreakItsInvariant()
				transition(nonEmptyCartState, emptyCartState, 
					when(RemoveItem.class, 
						inCase(input -> input != null && input.getState().getItems().size() == 3 && input.getState().getItems().get(0).equals(INVARIANT_BREAKING_ITEM), 
							transit(CartState::removeItem)))),
				
				// The following transitions are used to see if exceptions are thrown for transitions that conflict (i.e. are both enabled)
				transition(emptyCartState, nonEmptyCartState, when(ConflictingTrigger.class, 
					transit((s,t) -> CartState.cartState(Arrays.asList("PASS"), false)))),
				
				transition(emptyCartState, nonEmptyCartState, when(ConflictingTrigger.class, 
					transit((s,t) -> CartState.cartState(Arrays.asList("FAIL"), false))))
			)
			.flows(
				entryFlow(when(CreateCart.class, init(CartState::createCart)))
			)
			.build();
		
		return statemachine;
	}

	private State<CartState, Trigger> createNonEmptyCartStateWithSubstates() {
		State<CartState, Trigger> nonFullCartSubState = state("Non-full Cart", cs -> cs != null && cs.isSubStateEntered() && cs.getItems().size() == 1);
		State<CartState, Trigger> fullCartSubState = state("Full Cart", cs -> cs != null && cs.isSubStateEntered() && cs.getItems().size() >= 2);

		Statemachine<CartState, Trigger> nonEmptyCartStateMachine = Statemachine.builder()
			.states(nonFullCartSubState, fullCartSubState)
			.transitions(
				transition(nonFullCartSubState, fullCartSubState, when(AddItem.class, transit(CartState::addItem))),
				transition(fullCartSubState, nonFullCartSubState, when(RemoveItem.class, transit(CartState::removeItem)))
			)
			.flows(
				entryFlow(nonFullCartSubState, transit(CartState::enterSubstate)),
				exitFlow(nonFullCartSubState, when(RemoveItem.class, transit(CartState::exitSubstate)))
			)
			.build();
		
		State<CartState, Trigger> nonEmptyCartState = state("Non-empty Cart", cs -> cs != null && !cs.isEmpty(), nonEmptyCartStateMachine);
		return nonEmptyCartState;
	}
}






