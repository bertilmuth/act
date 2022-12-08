package org.requirementsascode.act.workflow.trigger;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.ActionData;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class AddToken implements ActionData {
	private final Token token;

	public AddToken(Token token){
		this.token = token;
	};
	
	public Token token() {
		return token;
	}
	
	public static boolean isContained(Data<WorkflowState, Token> inputData) {
		return AddToken.isContainedInToken(Token.from(inputData));
	}

	private static boolean isContainedInToken(Token token) {
		return token.actionData().map(ad -> ad instanceof AddToken).orElse(false);
	}
}