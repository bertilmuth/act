package org.requirementsascode.act.statemachine.testdata;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.CreateHierarchicalCart;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class HierarchicalCartStateMachine {	
	public static final String INVARIANT_BREAKING_ITEM = "InvariantBreakingItem";

	private HierarchicalCart state;
	Statemachine<HierarchicalCart, Trigger> statemachine;

	public HierarchicalCartStateMachine() {
		this.statemachine = createStatemachine();
	}
	
	public void actOn(Trigger trigger) {
		Data<HierarchicalCart, Trigger> before = data(state, trigger);
		
		Data<HierarchicalCart, Trigger> after = statemachine.actOn(before);
		setState(after.state());
	}
	
	public boolean subStateEntered(){
		return state.isSubStateEntered();
	}
	
	public List<String> items(){
		return state.items();
	}
	
	private void setState(HierarchicalCart cartState) {
		this.state = cartState;
	}
	
	private Statemachine<HierarchicalCart, Trigger> createStatemachine() {
		State<HierarchicalCart, Trigger> emptyCartState = state("Empty Cart", cs -> cs != null && cs.isEmpty());

		State<HierarchicalCart, Trigger> nonEmptyCartState = createNonEmptyCartStateWithSubstates();

		Statemachine<HierarchicalCart, Trigger> statemachine = Statemachine.builder()
			.states(emptyCartState,nonEmptyCartState)
			.transitions(
				transition(emptyCartState, nonEmptyCartState, when(AddItem.class, consumeWith(HierarchicalCart::addItem))),
				transition(emptyCartState, emptyCartState, when(RemoveItem.class, consumeWith((s,t) -> {throw new RuntimeException("RemoveItem not expected");}))),
				transition(nonEmptyCartState, emptyCartState, whenInCase(RemoveItem.class, i -> i.state().items().size() == 1, consumeWith(HierarchicalCart::removeItem))),
				entryFlow(when(CreateHierarchicalCart.class, init(HierarchicalCart::createCart)))
			)
			.build();
		
		return statemachine;
	}

	private State<HierarchicalCart, Trigger> createNonEmptyCartStateWithSubstates() {
		State<HierarchicalCart, Trigger> nonFullCartSubState = state("Non-full Cart", cs -> cs != null && cs.isSubStateEntered() && cs.items().size() == 1);
		State<HierarchicalCart, Trigger> fullCartSubState = state("Full Cart", cs -> cs != null && cs.isSubStateEntered() && cs.items().size() >= 2);

		Statemachine<HierarchicalCart, Trigger> nonEmptyCartStateMachine = Statemachine.builder()
			.states(nonFullCartSubState, fullCartSubState)
			.transitions(
				transition(nonFullCartSubState, fullCartSubState, when(AddItem.class, consumeWith(HierarchicalCart::addItem))),
				transition(fullCartSubState, nonFullCartSubState, when(RemoveItem.class, consumeWith(HierarchicalCart::removeItem))),
				entryFlow(nonFullCartSubState, consumeWith(HierarchicalCart::enterSubstate)),
				exitFlow(nonFullCartSubState, when(RemoveItem.class, consumeWith(HierarchicalCart::exitSubstate)))
			)
			.build();
		
		State<HierarchicalCart, Trigger> nonEmptyCartState = state("Non-empty Cart", cs -> cs != null && !cs.isEmpty(), nonEmptyCartStateMachine);
		return nonEmptyCartState;
	}
}






