package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Behavior.identity;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.unitedbehavior.StatesBehaviorOrIdentity.statesBehaviorOrIdentity;
import static org.requirementsascode.act.statemachine.unitedbehavior.TransitionsBehavior.transitionsBehavior;
import static org.requirementsascode.act.statemachine.validate.StatemachineValidator.validate;

import java.util.List;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.merge.FirstOneWhoActsWins;
import org.requirementsascode.act.statemachine.unitedbehavior.Flows;

public class Statemachine<S, V0> implements Behavior<S, V0, V0> {
	private static final String DEFINED_STATE = "Defined State";
	private static final String DEFAULT_STATE = "Default State";

	private final List<State<S, V0>> states;
	private final List<Transition<S, V0>> transitions;
	private final Flows<S, V0> flows;
	private final Behavior<S, V0, V0> statemachineBehavior;
	private final State<S, V0> defaultState;
	private final State<S, V0> definedState;

	Statemachine(List<State<S, V0>> states, List<Transition<S, V0>> transitions,
		Flows<S, V0> flows) {
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

	public List<State<S, V0>> states() {
		return states;
	}

	public List<Transition<S, V0>> transitions() {
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

	private State<S, V0> createDefinedState(List<State<S, V0>> states) {
		return state(DEFINED_STATE, states.stream().map(State::invariant).reduce(s -> false, Predicate::or), identity());
	}

	private State<S, V0> createDefaultState(State<S, V0> definedState) {
		return state(DEFAULT_STATE, definedState.invariant().negate(), identity());
	}

	private Behavior<S, V0, V0> createStatemachineBehavior() {
		validate(this);

		Behavior<S, V0, V0> statesBehaviorOrIdentity = statesBehaviorOrIdentity(states());
		Behavior<S, V0, V0> transitionsBehavior = transitionsBehavior(transitions());
		Behavior<S, V0, V0> flowsBehavior = flows().asBehavior(definedState(), defaultState());

		Behavior<S, V0, V0> behavior = unitedBehavior(new FirstOneWhoActsWins<>(),
			statesBehaviorOrIdentity.andThen(transitionsBehavior.andThen(inCase(this::isOutputPresent, this, identity()))),
			flowsBehavior
		);

		return behavior;
	}

	private boolean isOutputPresent(Data<S, V0> data) {
		return data.value() != null;
	}
}
