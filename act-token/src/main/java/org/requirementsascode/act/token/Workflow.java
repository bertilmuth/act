package org.requirementsascode.act.token;

public class Workflow<V> {
	private final Tokens<V> tokens;
	
	private Workflow(Tokens<V> tokens) {
		this.tokens = tokens;
	}

	public static <V> Workflow<V> workflow(Tokens<V> tokens){
		return new Workflow<>(tokens);
	}
	
	public Tokens<V> tokens(){
		return tokens;
	}

	@Override
	public String toString() {
		return "Workflow [" + tokens + "]";
	}
}
