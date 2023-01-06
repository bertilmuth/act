package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;

public class WorkflowApi {
	public static <T extends ActionData, U extends ActionData> Node action(String name, Class<T> inputClass, BiFunction<WorkflowState, T, U> function) {
		return executableNode(name, new ActionBehavior<>(inputClass, function));
	}
	
	public static <T extends ActionData> ExecutableNode executableNode(String name, Behavior<WorkflowState,ActionData,ActionData> actionBehavior) {
		return new ExecutableNode(name, actionBehavior);
	}
	
	public static Token token(Node node, ActionData actionData) {
		return new Token(node, actionData);
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode) {		
		return dataFlow(fromNode, toNode, d -> true);
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode, Predicate<T> guardCondition) {		
		return new DataFlow<>(fromNode, toNode, guardCondition);
	}
}
