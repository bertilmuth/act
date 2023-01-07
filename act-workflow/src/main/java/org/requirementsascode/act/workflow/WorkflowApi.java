package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class WorkflowApi {
	public static <T extends ActionData, U extends ActionData> Node action(String name, Class<T> inputClass, BiFunction<WorkflowState, T, U> actionFunction) {
		return new ActionNode(name, inputClass, actionFunction);
	}
	
	public static Token token(ActionData actionData) {
		return new Token(actionData);
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode) {		
		return dataFlow(fromNode, toNode, d -> true);
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode, Predicate<T> guardCondition) {		
		return new DataFlow<>(fromNode, toNode, guardCondition);
	}
}
