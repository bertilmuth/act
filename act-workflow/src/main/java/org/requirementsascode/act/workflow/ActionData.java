package org.requirementsascode.act.workflow;

import org.requirementsascode.act.core.Data;

public interface ActionData{ 
	static ActionData from(Data<WorkflowState, Token> inputData) {
		return Token.from(inputData).actionData().orElse(null);
	}
};
