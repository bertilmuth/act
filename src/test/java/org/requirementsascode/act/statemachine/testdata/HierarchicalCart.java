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

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class HierarchicalCart {	
	public static final String INVARIANT_BREAKING_ITEM = "InvariantBreakingItem";

	private HierarchicalCartState state;
	Statemachine<HierarchicalCartState, Trigger> statemachine;

	public HierarchicalCart() {
		this.statemachine = createStatemachine();
	}
	
	public void actOn(Trigger trigger) {
		Data<HierarchicalCartState, Trigger> input = data(state, trigger);
		
		Data<HierarchicalCartState, Trigger> output = statemachine.actOn(input);
		setState(output.getState());
	}
	
	public boolean subStateEntered(){
		return state.isSubStateEntered();
	}
	
	public List<String> items(){
		return state.getItems();
	}
	
	private void setState(HierarchicalCartState cartState) {
		this.state = cartState;
	}
	
	private Statemachine<HierarchicalCartState, Trigger> createStatemachine() {
		State<HierarchicalCartState, Trigger> emptyCartState = state("Empty Cart", cs -> cs != null && cs.isEmpty());

		State<HierarchicalCartState, Trigger> nonEmptyCartState = createNonEmptyCartStateWithSubstates();

		Statemachine<HierarchicalCartState, Trigger> statemachine = Statemachine.builder()
			.states(emptyCartState,nonEmptyCartState)
			.transitions(
				transition(emptyCartState, nonEmptyCartState, when(AddItem.class, transit(HierarchicalCartState::addItem))),
				transition(emptyCartState, nonEmptyCartState, when(RemoveItem.class, transit((s,t) -> {throw new RuntimeException("RemoveItem not expected");}))),
				transition(nonEmptyCartState, emptyCartState, when(RemoveItem.class, inCase(i -> i.getState().getItems().size() == 1, transit(HierarchicalCartState::removeItem))))
			)
			.flows(
				entryFlow(when(CreateHierarchicalCart.class, init(HierarchicalCartState::createCart)))
			)
			.build();
		
		return statemachine;
	}

	private State<HierarchicalCartState, Trigger> createNonEmptyCartStateWithSubstates() {
		State<HierarchicalCartState, Trigger> nonFullCartSubState = state("Non-full Cart", cs -> cs != null && cs.isSubStateEntered() && cs.getItems().size() == 1);
		State<HierarchicalCartState, Trigger> fullCartSubState = state("Full Cart", cs -> cs != null && cs.isSubStateEntered() && cs.getItems().size() >= 2);

		Statemachine<HierarchicalCartState, Trigger> nonEmptyCartStateMachine = Statemachine.builder()
			.states(nonFullCartSubState, fullCartSubState)
			.transitions(
				transition(nonFullCartSubState, fullCartSubState, when(AddItem.class, transit(HierarchicalCartState::addItem))),
				transition(fullCartSubState, nonFullCartSubState, when(RemoveItem.class, transit(HierarchicalCartState::removeItem)))
			)
			.flows(
				entryFlow(nonFullCartSubState, transit(HierarchicalCartState::enterSubstate)),
				exitFlow(nonFullCartSubState, when(RemoveItem.class, transit(HierarchicalCartState::exitSubstate)))
			)
			.build();
		
		State<HierarchicalCartState, Trigger> nonEmptyCartState = state("Non-empty Cart", cs -> cs != null && !cs.isEmpty(), nonEmptyCartStateMachine);
		return nonEmptyCartState;
	}
}






