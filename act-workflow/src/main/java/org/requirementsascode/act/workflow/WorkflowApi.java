package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class WorkflowApi {
	public static <T extends ActionData, U extends ActionData> Node action(Port<T> inputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		return new ActionNode<>(inputPort.name(), inputPort.type(), actionFunction);
	}
	
	public static Token token(ActionData actionData) {
		return new Token(actionData);
	}
	
	public static DataFlow<? extends ActionData> dataFlow(Node fromNode, Node toNode) {		
		return dataFlow(fromNode, toNode, toNode.type());
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode, Class<T> inputClass) {		
		return dataFlow(fromNode, toNode, inputClass, ad -> true);
	}
	
	public static <T extends ActionData> DataFlow<T> dataFlow(Node fromNode, Node toNode, Class<T> inputClass, Predicate<T> guardCondition) {		
		return new DataFlow<>(fromNode, toNode, inputClass, guardCondition);
	}
}
