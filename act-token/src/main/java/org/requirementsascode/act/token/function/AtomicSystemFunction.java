package org.requirementsascode.act.token.function;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.token.Token.token;

import java.util.Optional;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.token.Action;
import org.requirementsascode.act.token.ActionBehavior;
import org.requirementsascode.act.token.ActionData;
import org.requirementsascode.act.token.Node;
import org.requirementsascode.act.token.Token;
import org.requirementsascode.act.token.Workflow;

public class AtomicSystemFunction implements ActionBehavior{
	private final Behavior<Workflow, ActionData, ActionData> function;

	private AtomicSystemFunction(Behavior<Workflow, ActionData, ActionData> function) {
		this.function = function;
	}
	
	public static AtomicSystemFunction atomicSystemFunction(Behavior<Workflow, ActionData, ActionData> function) {
		return new AtomicSystemFunction(function);
	}
	
	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action callingAction) {
		return inCase(this::isTriggered, d -> triggerNextStep(callingAction, d));
	}
	
	private boolean isTriggered(Data<Workflow, Token> inputData) {
		Optional<Token> token = inputData.value();
		return token.map(Token::triggersSystemFunction).orElse(false);
	}
	
	private Data<Workflow, Token> triggerNextStep(Action action, Data<Workflow, Token> inputData){
		Workflow workflow = workflowOf(inputData);
		Token firstToken = workflow.tokens().firstTokenIn(action.name()).get();	
		Data<Workflow, ActionData> functionInput = data(workflow, firstToken.actionData());
		Data<Workflow, ActionData> functionOutput = function.actOn(functionInput);
		return data(workflowOf(functionOutput), tokenFor(firstToken.node(), functionOutput));
	}
	
	private Workflow workflowOf(Data<Workflow, ?> data) {
		return data.state();
	}

	private Token tokenFor(Node node, Data<Workflow, ActionData> actionData) {
		return token(node, actionData.value().orElse(null));
	}
}
