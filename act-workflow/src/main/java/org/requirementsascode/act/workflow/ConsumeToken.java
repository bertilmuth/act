package org.requirementsascode.act.workflow;

import org.requirementsascode.act.core.Data;

class ConsumeToken implements ActionData {
	ConsumeToken() {
	};
	
	static boolean matches(Data<WorkflowState, Token> inputData) {
		return Token.from(inputData).map(ConsumeToken::isContained).orElse(false);
	}
	
	private static boolean isContained(Token token) {
		return token.actionData().map(ad -> ad instanceof ConsumeToken).orElse(false);
	}
}