package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;

public class WorkflowApi {
	public static <T extends ActionData, U extends ActionData> Action action(String name, Class<T> inputClass, BiFunction<WorkflowState, T, U> function) {
		return new Action(name, step(inputClass, function));
	}
	
	public static Action action(String name, Behavior<WorkflowState,Token,Token> actionBehavior) {
		return new Action(name, actionBehavior);
	}
	
	public static <T extends ActionData, U extends ActionData> StepBehavior<T, U> step(Class<T> inputClass,
			BiFunction<WorkflowState, T, U> function) {
		return new StepBehavior<>(inputClass, function);
	}
	
	public static Token token(Node node, ActionData actionData) {
		return new Token(node, actionData);
	}
	
	public static DataFlow dataFlow(Node fromNode, Node toNode) {		
		return new DataFlow(fromNode, toNode);
	}
}
