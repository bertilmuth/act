package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;

public class Step<T extends ActionData, U extends ActionData> implements ActionBehavior {
	private final Behavior<WorkflowState, Token, Token> stepBehavior;
	public static final ConsumeToken consumeToken = new ConsumeToken();

	Step(Class<T> inputClass, BiFunction<WorkflowState, T, U> function) {
		requireNonNull(inputClass, "inputClass must be non-null!");
		requireNonNull(function, "function must be non-null!");
		this.stepBehavior = new StepBehavior<>(inputClass, function);
	}

	@Override
	public Behavior<WorkflowState, Token, Token> asBehavior(Action owningAction) {
		return inCase(ConsumeToken::isContained, d -> owningAction.consumeToken(d, stepBehavior));
	}
}

