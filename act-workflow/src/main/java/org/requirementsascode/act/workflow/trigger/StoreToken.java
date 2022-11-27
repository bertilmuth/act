package org.requirementsascode.act.workflow.trigger;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.ActionData;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class StoreToken implements ActionData {
	private final Token token;

	public StoreToken(Token token){
		this.token = token;
	};
	
	public Token token() {
		return token;
	}
	
	public static boolean isContained(Data<WorkflowState, Token> inputData) {
		return Token.from(inputData).map(StoreToken::isContained).orElse(false);
	}

	private static boolean isContained(Token token) {
		return token.actionData().map(ad -> ad instanceof StoreToken).orElse(false);
	}
}