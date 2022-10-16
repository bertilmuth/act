package org.requirementsascode.act.token;

public class Workflow {
	private final Tokens tokens;
	
	private Workflow(Tokens tokens) {
		this.tokens = tokens;
	}

	public static  Workflow workflow(Tokens tokens){
		return new Workflow(tokens);
	}
	
	public Tokens tokens(){
		return tokens;
	}

	@Override
	public String toString() {
		return "Workflow [" + tokens + "]";
	}
}
