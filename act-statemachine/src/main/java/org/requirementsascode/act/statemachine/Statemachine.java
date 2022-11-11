package org.requirementsascode.act.statemachine;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Behavior.identity;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.validate.StatemachineValidator.validate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;
import org.requirementsascode.act.statemachine.merge.FirstOneWhoActsWins;

public class Statemachine<S, V0> implements Behavior<S, V0, V0> {
	private static final String DEFINED_STATE = "Defined State";
	private static final String DEFAULT_STATE = "Default State";

	private final States<S, V0> states;
	private final Transitions<S, V0> transitions;
	private final Behavior<S, V0, V0> statemachineBehavior;
	private final State<S, V0> defaultState;
	private final State<S, V0> definedState;
	private final MergeStrategy<S, V0> mergeStrategy;

	Statemachine(States<S, V0> states, Transitions<S, V0> transitions, MergeStrategy<S, V0> mergeStrategy) {
		this.states = requireNonNull(states, "states must be non-null!");
		this.transitions = requireNonNull(transitions, "transitions must be non-null!");
		this.mergeStrategy = mergeStrategy;
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

	public State<S, V0> defaultState() {
		return defaultState;
	}

	public State<S, V0> definedState() {
		return definedState;
	}
	
	public MergeStrategy<S, V0> mergeStrategy() {
		return mergeStrategy;
	}
	
	public Transitions<S, V0> outgoingTransitions(State<S, V0> outsideState) {
		requireNonNull(outsideState, "outsideState must be non-null!");
		
		List<Transitionable<S, V0>> transitionList = transitions.stream()
			.filter(t -> t.asTransition(this).fromState().equals(outsideState))
			.collect(Collectors.toList());
		
		Transitions<S, V0> transitions = Transitions.transitions(transitionList);
		return transitions;
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

		Behavior<S, V0, V0> behavior = unitedBehavior(new FirstOneWhoActsWins<>(), 
				states().asBehavior(this),
				transitions().asBehavior(this));

		return behavior;
	}
}
