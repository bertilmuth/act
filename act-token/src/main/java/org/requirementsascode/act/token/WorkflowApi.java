package org.requirementsascode.act.token;

import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;

import java.util.function.BiFunction;

public class WorkflowApi {
	public static Action action(String name, ActionBehavior actionBehavior) {
		return Action.action(name, actionBehavior);
	}
	
	public static <T extends ActionData, U extends ActionData> Step<T, U> step(Class<T> inputClass,
			BiFunction<WorkflowState, T, U> function) {
		return Step.step(inputClass, function);
	}
}
