package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class Action {
	public static State<Workflow, Token> action(String stateName, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		requireNonNull(stateName, "stateName must be non-null!");
		requireNonNull(actionBehavior, "actionBehavior must be non-null!");

		Behavior<Workflow, Token, Token> stateBehavior = d -> act(stateName, d, actionBehavior);
		
		State<Workflow, Token> state = state(stateName, workflow -> isAnyTokenInState(workflow, stateName), 
			whenInCase(Token.class, Action::isTriggerStepToken,stateBehavior));
		return state;
	}
	
	private static boolean isAnyTokenInState(Workflow workflow, String stateName) {
		return workflow.tokens().inState(stateName).count() != 0;
	}
	
	private static boolean isTriggerStepToken(Data<Workflow, Token> data) {
		return data.value().filter(t -> t.value() instanceof TriggerStep).isPresent();
	}

	private static Data<Workflow, Token> act(String stateName, Data<Workflow, Token> data, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		Tokens tokens = data.state().tokens();
		Token firstToken = tokens.firstTokenInState(stateName).orElse(null);	
		Data<Workflow, ActionData> actionInput = data(data.state(), firstToken.value());
		Data<Workflow, ActionData> actionOutput = actionBehavior.actOn(actionInput);
		return data(actionOutput.state(), Token.token(firstToken.state(), actionOutput.value().orElse(null)));
	}
}
