package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.Token.token;

import java.util.Optional;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.InCase;

public class AtomicSystemFunction implements AsBehavior{
	private AtomicSystemFunction() {
	}
	
	public static AtomicSystemFunction atomicSystemFunction() {
		return new AtomicSystemFunction();
	}
	
	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action callingAction) {
		return InCase.inCase(this::isTriggered, d -> triggerNextStep(callingAction, d));
	}
	
	private boolean isTriggered(Data<Workflow, Token> inputData) {
		Optional<Token> token = inputData.value();
		return token.map(Token::isTriggerOfNextStep).orElse(false);
	}
	
	private Data<Workflow, Token> triggerNextStep(Action action, Data<Workflow, Token> inputData){
		Workflow workflow = workflowOf(inputData);
		Token firstToken = workflow.tokens().firstTokenIn(action.name()).get();	
		Data<Workflow, ActionData> functionInput = data(workflow, firstToken.actionData());
		Data<Workflow, ActionData> functionOutput = action.behavior().actOn(functionInput);
		return data(workflowOf(functionOutput), tokenFor(firstToken.node(), functionOutput));
	}
	
	private Workflow workflowOf(Data<Workflow, ?> data) {
		return data.state();
	}

	private Token tokenFor(Node node, Data<Workflow, ActionData> actionData) {
		return token(node, actionData.value().orElse(null));
	}
}
