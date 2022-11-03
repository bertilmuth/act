package org.requirementsascode.act.token;

import java.util.function.BiFunction;

public class WorkflowApi {
	public static Action action(String name, ActionBehavior actionBehavior) {
		return Action.action(name, actionBehavior);
	}
	
	public static <T extends ActionData, U extends ActionData> Step<T, U> step(Class<T> inputClass,
			BiFunction<WorkflowState, T, U> function) {
		return Step.step(inputClass, function);
	}
	
	public static Token token(Node node, ActionData actionData) {
		return Token.token(node, actionData);
	}
	
	public static DataFlow dataFlow(Node fromNode, Node toNode) {		
		return DataFlow.dataFlow(fromNode, toNode);
	}
}
