package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;

public class WorkflowApi {
	public static <T extends ActionData> Port<T> port(String portName, Class<T> portType){
		return new Port<>(portName, portType);
	}
	
	public static <T extends ActionData, U extends ActionData> Action<T,U> action(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		return new Action<>(actionName, inputPort, outputPort, actionFunction);
	}
	
	public static Token token(ActionData actionData) {
		return new Token(actionData);
	}
	
	public static <T extends ActionData> Flow<T,T> flow(Port<T> inputPort, Port<T> outputPort) {		
		return flow(inputPort, outputPort, (s,ad) -> ad);
	}
	
	static <T extends ActionData, U extends ActionData> Flow<T,U> flow(Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {		
		return new Flow<>(inputPort, outputPort, actionFunction);
	}
}
