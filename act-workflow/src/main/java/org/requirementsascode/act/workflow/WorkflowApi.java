package org.requirementsascode.act.workflow;

import java.util.Arrays;
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
	
	public static <T extends ActionData> Flow<T,T> flow(Port<T> inPort, Port<T> outPort) {		
		return flow(inPort, outPort, (s,ad) -> ad);
	}
	
	@SafeVarargs
	static Ports ports(Port<? extends ActionData>... ports) {
		return new Ports(Arrays.asList(ports));
	}
	
	static <T extends ActionData, U extends ActionData> Flow<T,U> flow(Port<T> inPort, Port<U> outPort, BiFunction<WorkflowState, T, U> actionFunction) {		
		Ports inPorts = ports(inPort);
		Ports outPorts = ports(outPort);
		return flow(inPorts, outPorts, actionFunction);
	}
	
	static <T extends ActionData, U extends ActionData> Flow<T,U> flow(Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {		
		return new Flow<>(inPorts, outPorts, actionFunction);
	}
}
