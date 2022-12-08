package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;

public class WorkflowApi {
	public static <T extends ActionData, U extends ActionData> ExecutableNode action(String name, Class<T> inputClass, BiFunction<WorkflowState, T, U> function) {
		return executableNode(name, actionBehavior(inputClass, function));
	}
	
	public static ExecutableNode executableNode(String name, Behavior<WorkflowState,Token,Token> behavior) {
		return new ExecutableNode(name, behavior);
	}
	
	public static <T extends ActionData, U extends ActionData> StepBehavior<T, U> actionBehavior(Class<T> inputClass,
			BiFunction<WorkflowState, T, U> function) {
		return new StepBehavior<>(inputClass, function);
	}
	
	public static Token token(Node node, ActionData actionData) {
		return new Token(node, actionData);
	}
	
	public static DataFlow<ActionData> dataFlow(Node fromNode, Node toNode) {		
		return dataFlow(fromNode, toNode, ActionData.class, d -> true);
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode, Class<T> inputClass, Predicate<T> guardCondition) {		
		return new DataFlow<>(fromNode, toNode, inputClass, guardCondition);
	}
}
