package org.requirementsascode.act.statemachine;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Behavior.identity;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Transition.hasFired;
import static org.requirementsascode.act.statemachine.validate.StatemachineValidator.validate;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.UnitedBehavior;
import org.requirementsascode.act.statemachine.merge.FirstOneWhoActsWins;

public class Statemachine<S, V0> implements Behavior<S, V0, V0> {
	private static final String DEFINED_STATE = "Defined State";
	private static final String DEFAULT_STATE = "Default State";

	private final States<S, V0> states;
	private final Transitions<S, V0> transitions;
	private final Flows<S, V0> flows;
	private final Behavior<S, V0, V0> statemachineBehavior;
	private final State<S, V0> defaultState;
	private final State<S, V0> definedState;

	Statemachine(States<S, V0> states, Transitions<S, V0> transitions, Flows<S, V0> flows) {
		this.states = requireNonNull(states, "states must be non-null!");
		this.transitions = requireNonNull(transitions, "transitions must be non-null!");
		this.flows = requireNonNull(flows, "flows must be non-null!");
		this.definedState = createDefinedState(states);
		this.defaultState = createDefaultState(definedState);
		this.statemachineBehavior = createStatemachineBehavior();
	}

	public final static StatemachineBuilder builder() {
		return new StatemachineBuilder();
	}

	@Override
	public Data<S, V0> actOn(Data<S, V0> before) {
		return statemachineBehavior.actOn(before);
	}

	public States<S, V0> states() {
		return states;
	}

	public Transitions<S, V0> transitions() {
		return transitions;
	}

	public Flows<S, V0> flows() {
		return flows;
	}

	public State<S, V0> defaultState() {
		return defaultState;
	}

	public State<S, V0> definedState() {
		return definedState;
	}

	private State<S, V0> createDefinedState(States<S, V0> states) {
		return state(DEFINED_STATE, states.stream()
			.map(State::invariant)
			.reduce(s -> false, Predicate::or),
			identity());
	}

	private State<S, V0> createDefaultState(State<S, V0> definedState) {
		return state(DEFAULT_STATE, definedState.invariant().negate(), identity());
	}

	private Behavior<S, V0, V0> createStatemachineBehavior() {
		validate(this);

		Behavior<S, V0, V0> behavior = 
			statesBehaviorOrIdentity().andThen(transitBehavior())
			.andThen(recallStatemachine());

		return behavior;
	}
	
	public Behavior<S, V0, V0> statesBehaviorOrIdentity() {
		Behavior<S, V0, V0> statesBehavior = states().asBehavior(this);
		return unitedBehavior(new FirstOneWhoActsWins<>(), asList(statesBehavior, identity()));
	}

	private UnitedBehavior<S, V0> transitBehavior() {
		Behavior<S, V0, V0> transitionsBehavior = transitions().asBehavior(this);
		Behavior<S, V0, V0> flowsBehavior = flows().asBehavior(this);
		return unitedBehavior(new FirstOneWhoActsWins<>(), transitionsBehavior, flowsBehavior);
	}
	
	private Behavior<S, V0, V0> recallStatemachine() {
		return inCase(this::hasFiredAndNotInDefaultState, this, identity());
	}

	private boolean hasFiredAndNotInDefaultState(Data<S, V0> d) {
		boolean b = hasFired(d) && !defaultState().matchesStateIn(d);
		return b;
	}
}
