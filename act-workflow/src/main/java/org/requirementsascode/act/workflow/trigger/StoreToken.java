package org.requirementsascode.act.workflow.trigger;

import org.requirementsascode.act.workflow.ActionData;
import org.requirementsascode.act.workflow.Token;

public class StoreToken implements ActionData {
	private final Token token;

	public StoreToken(Token token){
		this.token = token;
	};
	
	public Token token() {
		return token;
	}
}