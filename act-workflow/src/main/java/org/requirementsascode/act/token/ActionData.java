package org.requirementsascode.act.token;

import org.requirementsascode.act.core.Data;

public interface ActionData{ 
	static ActionData from(Data<WorkflowState, Token> inputData) {
		return Token.from(inputData).flatMap(Token::actionData).orElse(null);
	}
};
