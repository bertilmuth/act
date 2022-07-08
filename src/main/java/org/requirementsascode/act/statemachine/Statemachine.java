package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Behavior.identity;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.unitedbehavior.FlowsBehavior.flowsBehavior;
import static org.requirementsascode.act.statemachine.unitedbehavior.StatesBehaviorOrIdentity.statesBehaviorOrIdentity;
import static org.requirementsascode.act.statemachine.unitedbehavior.TransitionsBehavior.transitionsBehavior;
import static org.requirementsascode.act.statemachine.validate.StatemachineValidator.validate;

import java.util.List;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.merge.FirstOneWhoActsWins;

public class Statemachine<S, V0> implements Behavior<S, V0> {
	private static final String DEFINED_STATE = "Defined State";
	private static final String DEFAULT_STATE = "Default State";

	private final List<State<S, V0>> states;
	private final List<Transition<S, ? extends V0, V0>> transitions;
	private final List<Flow<S, ? extends V0, V0>> flows;
	private final Behavior<S, V0> statemachineBehavior;
	private final State<S, V0> defaultState;
	private final State<S, V0> definedState;

	Statemachine(List<State<S, V0>> states, List<Transition<S, ? extends V0, V0>> transitions,
		List<Flow<S, ? extends V0, V0>> flows) {
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
	public Data<S, V0> actOn(Data<S, V0> input) {
		return statemachineBehavior.actOn(input);
	}

	public List<State<S, V0>> getStates() {
		return states;
	}

	public List<Transition<S, ? extends V0, V0>> getTransitions() {
		return transitions;
	}

	public List<Flow<S, ? extends V0, V0>> getFlows() {
		return flows;
	}

	public State<S, V0> getDefaultState() {
		return defaultState;
	}

	public State<S, V0> getDefinedState() {
		return definedState;
	}

	private State<S, V0> createDefinedState(List<State<S, V0>> states) {
		return state(DEFINED_STATE, states.stream().map(State::getInvariant).reduce(s -> false, Predicate::or), identity());
	}

	private State<S, V0> createDefaultState(State<S, V0> definedState) {
		return state(DEFAULT_STATE, definedState.getInvariant().negate(), identity());
	}

	private Behavior<S, V0> createStatemachineBehavior() {
		validate(this);

		Behavior<S, V0> statesBehaviorOrIdentity = statesBehaviorOrIdentity(getStates());
		Behavior<S, V0> transitionsBehavior = transitionsBehavior(getTransitions());
		Behavior<S, V0> flowsBehavior = flowsBehavior(getFlows(), getDefinedState(), getDefaultState());

		Behavior<S, V0> behavior = unitedBehavior(new FirstOneWhoActsWins<>(),
			statesBehaviorOrIdentity.andThen(transitionsBehavior.andThen(inCase(this::isOutputPresent, this, identity()))),
			flowsBehavior
		);

		return behavior;
	}

	private boolean isOutputPresent(Data<S, V0> data) {
		return data.getValue() != null;
	}
}
