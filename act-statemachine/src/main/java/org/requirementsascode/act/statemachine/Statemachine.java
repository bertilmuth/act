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
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class Statemachine<S, V0> implements Behavior<S, V0, V0> {
	private static final String DEFINED_STATE = "Defined State";
	private static final String INITIAL_STATE = "Initial State";
	private static final String FINAL_STATE = "Final State";

	private final States<S, V0> states;
	private final Transitionables<S, V0> transitionables;
	private final Behavior<S, V0, V0> statemachineBehavior;
	private final State<S, V0> initialState;
	private final State<S, V0> definedState;
	private final State<S, V0> finalState;
	private final MergeStrategy<S, V0> mergeStrategy;
	private final boolean isRecursive;
	private final Behavior<S, V0, V0> flowsBehavior;

	Statemachine(States<S, V0> states, Transitionables<S, V0> transitionables, MergeStrategy<S, V0> mergeStrategy, boolean isRecursive) {
		this.states = requireNonNull(states, "states must be non-null!");
		this.mergeStrategy = requireNonNull(mergeStrategy, "mergeStrategy must be non-null!");
		this.definedState = createDefinedState(states);
		this.initialState = createInitialState(definedState);
		this.finalState = createFinalState(definedState);
		this.transitionables = requireNonNull(transitionables, "transitions must be non-null!");
		this.isRecursive = isRecursive;

		this.statemachineBehavior = createStatemachineBehavior();
		this.flowsBehavior = transitionables().flowsBehaviorOf(this);
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

	public Transitionables<S, V0> transitionables() {
		return transitionables;
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
	
	public Transitionables<S, V0> incomingTransitionables(State<S, V0> toState) {
		requireNonNull(toState, "toState must be non-null!");
		
		List<Transitionable<S, V0>> transitions = transitionables().stream()
			.filter(t -> t.asTransition(this).toState().equals(toState))
			.collect(Collectors.toUnmodifiableList());
		
		return new Transitionables<>(transitions);
	}
	
	public Transitionables<S, V0> outgoingTransitionables(State<S, V0> fromState) {
		requireNonNull(fromState, "fromState must be non-null!");
				
		List<Transitionable<S, V0>> transitions = transitionables().stream()
			.filter(t -> t.asTransition(this).fromState().equals(fromState))
			.collect(Collectors.toUnmodifiableList());
		
		return new Transitionables<>(transitions);
	}
	
	public boolean isRecursive() {
		return isRecursive;
	}

	private Behavior<S, V0, V0> createStatemachineBehavior() {
		validate(this);
		Behavior<S, V0, V0> behavior = 
			unitedBehavior(new OnlyOneBehaviorMayAct<>(),
				statesBehavior(),
				transitionsBehavior());
		return behavior;
	}
	private Behavior<S, V0, V0> statesBehavior() {
		return states().asBehavior(this);
	}
	
	Behavior<S, V0, V0> flowsBehavior() {
		return flowsBehavior;
	}
	
	private Behavior<S, V0, V0> transitionsBehavior() {
		return transitionables().transitionsBehaviorOf(this);
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
}
