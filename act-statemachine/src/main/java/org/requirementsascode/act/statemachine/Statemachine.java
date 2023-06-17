package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.validate.StatemachineValidator.validate;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class Statemachine<S, V0> implements Behavior<S, V0, V0> {
	private static final String DEFINED_STATE = "Defined State";
	private static final String DEFAULT_STATE = "Default State";
	private static final String FINAL_STATE = "Final State";

	private final States<S, V0> states;
	private final Transitionables<S, V0> transitionables;
	private final Behavior<S, V0, V0> statemachineBehavior;
	private final State<S, V0> defaultState;
	private final State<S, V0> definedState;
	private final State<S, V0> finalState;
	private final MergeStrategy<S, V0> mergeStrategy;
	private final Behavior<S, V0, V0> statesBehavior;
	private final Behavior<S, V0, V0> flowsBehavior;
	private final Behavior<S, V0, V0> transitionsBehavior;

	Statemachine(States<S, V0> states, Transitionables<S, V0> transitionables, MergeStrategy<S, V0> mergeStrategy) {
		this.states = requireNonNull(states, "states must be non-null!");
		this.mergeStrategy = requireNonNull(mergeStrategy, "mergeStrategy must be non-null!");
		this.definedState = createDefinedState(states);
		this.defaultState = createDefaultState(definedState);
		this.finalState = createFinalState(definedState);
		
		this.statesBehavior = states().asBehavior(this);
		this.transitionables = requireNonNull(transitionables, "transitions must be non-null!");
		this.flowsBehavior = transitionables.flowsBehaviorOf(this);
		this.transitionsBehavior = transitionables.transitionsBehaviorOf(this);
		
		this.statemachineBehavior = createStatemachineBehavior(statesBehavior, transitionsBehavior);
	}

	public final static StatemachineBuilder builder() {
		return new StatemachineBuilder();
	}
	
	public Data<S, V0> act(Data<S, V0> before, V0 trigger) {
		Data<S, V0> d = data(before.state(), trigger);
		return actOn(d);
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
	
	public State<S, V0> defaultState() {
		return defaultState;
	}
	
	public State<S, V0> finalState() {
		return finalState;
	}
	
	public MergeStrategy<S, V0> mergeStrategy() {
		return mergeStrategy;
	}

	private Behavior<S, V0, V0> createStatemachineBehavior(Behavior<S, V0, V0> statesBehavior, Behavior<S, V0, V0> transitionsBehavior) {
		validate(this);
		Behavior<S, V0, V0> behavior = 
			unitedBehavior(new OnlyOneBehaviorMayAct<>(),
				statesBehavior,
				transitionsBehavior);
		return behavior;
	}
	
	Behavior<S, V0, V0> flowsBehavior() {
		return flowsBehavior;
	}
	
	private State<S, V0> createDefinedState(States<S, V0> states) {
		return state(DEFINED_STATE, states.stream()
			.map(State::invariant)
			.reduce(s -> false, Predicate::or));
	}

	private State<S, V0> createDefaultState(State<S, V0> definedState) {
		return state(DEFAULT_STATE, notIn(definedState));
	}
	
	private State<S, V0> createFinalState(State<S, V0> definedState) {
		return state(FINAL_STATE, notIn(definedState));
	}
	
	private Predicate<S> notIn(State<S, V0> state) {
		return state.invariant().negate();
	}
}
