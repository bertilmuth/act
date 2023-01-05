package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;

public class WorkflowApi {
	public static <T extends ActionData, U extends ActionData> Node action(String name, Class<T> inputClass, BiFunction<WorkflowState, T, U> function) {
		return executableNode(name, inputClass, new ActionBehavior<>(inputClass, function));
	}
	
	public static <T extends ActionData> ExecutableNode executableNode(String name, Class<T> inputClass, Behavior<WorkflowState,Token,Token> behavior) {
		return new ExecutableNode(name, inputClass, behavior);
	}
	
	public static Token token(Node node, ActionData actionData) {
		return new Token(node, actionData);
	}
	
	public static DataFlow<? extends ActionData> dataFlow(Node fromNode, Node toNode) {		
		return dataFlow(fromNode, toNode, toNode.inputClass());
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode, Class<T> inputClass) {		
		return dataFlow(fromNode, toNode, inputClass, d -> true);
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode, Class<T> inputClass, Predicate<T> guardCondition) {		
		return new DataFlow<>(fromNode, toNode, inputClass, guardCondition);
	}
}
