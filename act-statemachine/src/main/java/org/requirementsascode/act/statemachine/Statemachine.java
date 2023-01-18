package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Behavior.identity;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.validate.StatemachineValidator.validate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;
import org.requirementsascode.act.statemachine.merge.FirstOneWhoActsWins;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class Statemachine<S, V0> implements Behavior<S, V0, V0> {
	private static final String DEFINED_STATE = "Defined State";
	private static final String INITIAL_STATE = "Initial State";
	private static final String FINAL_STATE = "Final State";

	private final States<S, V0> states;
	private final Transitions<S, V0> transitions;
	private final Behavior<S, V0, V0> statemachineBehavior;
	private final State<S, V0> initialState;
	private final State<S, V0> definedState;
	private final State<S, V0> finalState;
	private final MergeStrategy<S, V0> mergeStrategy;

	Statemachine(States<S, V0> states, Transitions<S, V0> transitions, MergeStrategy<S, V0> mergeStrategy) {
		this.states = requireNonNull(states, "states must be non-null!");
		this.mergeStrategy = requireNonNull(mergeStrategy, "mergeStrategy must be non-null!");
		this.definedState = createDefinedState(states);
		this.initialState = createInitialState(definedState);
		this.finalState = createFinalState(definedState);
		this.transitions = requireNonNull(transitions, "transitions must be non-null!");

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

	public State<S, V0> definedState() {
		return definedState;
	}
	
	public State<S, V0> initialState() {
		return initialState;
	}
	
	public State<S, V0> finalState() {
		return finalState;
	}
	
	public MergeStrategy<S, V0> mergeStrategy() {
		return mergeStrategy;
	}
	
	public Transitions<S, V0> incomingTransitions(State<S, V0> toState) {
		requireNonNull(toState, "toState must be non-null!");
		
		List<Transitionable<S, V0>> transitions = transitions().stream()
			.filter(t -> t.asTransition(this).toState().equals(toState))
			.collect(Collectors.toList());
		
		return new Transitions<>(transitions);
	}
	
	public Transitions<S, V0> outgoingTransitions(State<S, V0> fromState) {
		requireNonNull(fromState, "fromState must be non-null!");
				
		List<Transitionable<S, V0>> transitions = transitions().stream()
			.filter(t -> t.asTransition(this).fromState().equals(fromState))
			.collect(Collectors.toList());
		
		return new Transitions<>(transitions);
	}
	
	boolean isTerminal(State<S, V0> state) {
		long outgoingTransitionsSize = outgoingTransitions(state).stream().count();
		return outgoingTransitionsSize == 0;
	}

	private State<S, V0> createDefinedState(States<S, V0> states) {
		return state(DEFINED_STATE, states.stream()
			.map(State::invariant)
			.reduce(s -> false, Predicate::or),
			identity());
	}

	private State<S, V0> createInitialState(State<S, V0> definedState) {
		return state(INITIAL_STATE, notIn(definedState), identity());
	}
	
	private State<S, V0> createFinalState(State<S, V0> definedState) {
		return state(FINAL_STATE, notIn(definedState), identity());
	}
	
	private Predicate<S> notIn(State<S, V0> state) {
		return state.invariant().negate();
	}

	private Behavior<S, V0, V0> createStatemachineBehavior() {
		validate(this);

		Behavior<S, V0, V0> behavior = 
			unitedBehavior(new OnlyOneBehaviorMayAct<>(), 
				states().asBehavior(this),
				transitions().asBehavior(this));

		return behavior;
	}
}
