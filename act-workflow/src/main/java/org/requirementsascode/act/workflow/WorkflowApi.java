package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;

public class WorkflowApi {
	public static <T extends ActionData> Port<T> port(String portName, Class<T> portType){
		return new Port<>(portName, portType);
	}
	
	public static <T extends ActionData, U extends ActionData> Node action(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		return new ActionNode<>(actionName, inputPort, outputPort, actionFunction);
	}
	
	public static Token token(ActionData actionData) {
		return new Token(actionData);
	}
	
	public static <T extends ActionData> Flow<T,T> dataFlow(Port<T> inputPort, Port<T> outputPort) {		
		return dataFlow(inputPort, outputPort, (s,ad) -> ad);
	}
	
	static <T extends ActionData, U extends ActionData> Flow<T,U> dataFlow(Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {		
		return new Flow<>(inputPort, outputPort, actionFunction);
	}
}
