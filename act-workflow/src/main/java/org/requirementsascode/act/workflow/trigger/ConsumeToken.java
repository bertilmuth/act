package org.requirementsascode.act.workflow.trigger;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.ActionData;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class ConsumeToken implements ActionData {
	public ConsumeToken() {
	};

	public static boolean isContained(Data<WorkflowState, Token> inputData) {
		return Token.from(inputData).map(ConsumeToken::isContained).orElse(false);
	}

	private static boolean isContained(Token token) {
		return token.actionData().map(ad -> ad instanceof ConsumeToken).orElse(false);
	}
}