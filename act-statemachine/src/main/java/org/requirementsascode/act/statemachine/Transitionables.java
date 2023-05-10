package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;

public class Transitionables<S, V0>{
	private final List<Transitionable<S, V0>> transitionables;

	Transitionables(List<Transitionable<S, V0>> transitionables) {
		this.transitionables = requireNonNull(transitionables, "transitionables must be non-null!");
	}

	public Behavior<S, V0, V0> transitionsOf(Statemachine<S, V0> owningStatemachine) {
		return unitedBehavior(owningStatemachine.mergeStrategy(), findTransitionsOf(owningStatemachine));
	}

	public Stream<Transitionable<S, V0>> stream() {
		return transitionables.stream();
	}
	
	private List<Behavior<S, V0, V0>> findTransitionsOf(Statemachine<S, V0> owningStatemachine) {
		List<Behavior<S, V0, V0>> behaviors = this.stream()
			.filter(this::isNotFlow)
			.map(t -> t.asTransition(owningStatemachine))
			.map(t -> t.asBehavior(owningStatemachine))
			.collect(Collectors.toList());
		return behaviors;
	}
	
	private List<Behavior<S, V0, V0>> findFlowsOf(Statemachine<S, V0> owningStatemachine) {
		List<Behavior<S, V0, V0>> behaviors = this.stream()
			.filter(this::isFlow)
			.map(t -> t.asTransition(owningStatemachine))
			.map(t -> t.asBehavior(owningStatemachine))
			.collect(Collectors.toList());
		return behaviors;
	}
	
	private boolean isFlow(Transitionable<?,?> t) {
		return t instanceof Flow;
	}
	
	private boolean isNotFlow(Transitionable<?,?> t) {
		return !isFlow(t);
	}
}
