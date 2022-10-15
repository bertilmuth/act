package org.requirementsascode.act.token;

public class WorkflowState<V> {
	private final Tokens<V> tokens;
	
	private WorkflowState(Tokens<V> tokens) {
		this.tokens = tokens;
	}

	public static <V> WorkflowState<V> workflowState(Tokens<V> tokens){
		return new WorkflowState<>(tokens);
	}
	
	public Tokens<V> tokens(){
		return tokens;
	}
}
