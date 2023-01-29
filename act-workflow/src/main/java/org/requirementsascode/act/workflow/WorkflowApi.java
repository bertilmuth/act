package org.requirementsascode.act.workflow;

import java.util.Arrays;
import java.util.function.BiFunction;

import org.requirementsascode.act.workflow.behavior.Apply;

public class WorkflowApi {
	public static <T extends ActionData> Port<T> port(String portName, Class<T> portType){
		return new Port<>(portName, portType);
	}
	
	public static <T extends ActionData, U extends ActionData> Action action(String actionName, Port<T> inputPort, Port<U> outputPort, Class<T> actionType, BiFunction<WorkflowState, T, U> actionFunction) {
		Apply<T,U> apply = new Apply<>(actionType, actionFunction);
		return new Action(actionName, inputPort, outputPort, apply);
	}
	
	public static <T extends ActionData, U extends ActionData> Action action(String actionName, Ports inputPorts, Ports outputPorts, Class<T> actionType, BiFunction<WorkflowState, T, U> actionFunction) {
		Apply<T,U> apply = new Apply<>(actionType, actionFunction);
		return new Action(actionName, inputPorts, outputPorts, apply);
	}
	
	public static Token emptyToken() {
		return Token.empty();
	}
	
	public static Token token(ActionData actionData) {
		return new Token(actionData);
	}
	
	@SafeVarargs
	public static Ports ports(Port<? extends ActionData>... ports) {
		return new Ports(Arrays.asList(ports));
	}
	
	public static <T extends ActionData> Flow flow(Port<T> inPort, Port<T> outPort) {		
		return new BinaryConnection<>(inPort, outPort).asFlow();
	}
}
