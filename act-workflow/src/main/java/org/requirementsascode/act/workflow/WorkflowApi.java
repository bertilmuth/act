package org.requirementsascode.act.workflow;

import java.util.Arrays;
import java.util.function.BiFunction;

import org.requirementsascode.act.workflow.behavior.PartBehavior;

public class WorkflowApi {
	public static <T extends ActionData> Port<T> port(String portName, Class<T> portType){
		return new Port<>(portName, portType);
	}
	
	public static <T extends ActionData, U extends ActionData> Action<T,U> action(String actionName, Class<T> actionType, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		return new Action<>(actionName, actionType, inputPort, outputPort, actionFunction);
	}
	
	public static <T extends ActionData, U extends ActionData> Action<T,U> action(String actionName, Class<T> actionType, Ports inputPorts, Ports outputPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		return new Action<>(actionName, actionType, inputPorts, outputPorts, actionFunction);
	}
	
	public static Token emptyToken() {
		return Token.emptyToken();
	}
	
	public static Token token(ActionData actionData) {
		return new Token(actionData);
	}
	
	@SafeVarargs
	public static Ports ports(Port<? extends ActionData>... ports) {
		return new Ports(Arrays.asList(ports));
	}
	
	public static <T extends ActionData> Flow<T,T> flow(Port<T> inPort, Port<T> outPort) {		
		return new BinaryConnection<>(inPort, outPort).asFlow();
	}
}
