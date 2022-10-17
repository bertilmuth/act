package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.token.Token.token;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class Action {
	private final String stateName;
	private final Behavior<Workflow, ActionData, ActionData> actionBehavior;
	
	private Action(String stateName, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		this.stateName = stateName;
		this.actionBehavior = actionBehavior;
	}
	
	public static Action action(String stateName, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		requireNonNull(stateName, "stateName must be non-null!");
		requireNonNull(actionBehavior, "actionBehavior must be non-null!");
		return new Action(stateName, actionBehavior);
	}

	public State<Workflow, Token> asState() {
		Behavior<Workflow, Token, Token> stateBehavior = d -> act(stateName, d.state(), actionBehavior);
		
		State<Workflow, Token> state = state(stateName, workflow -> isAnyTokenInState(workflow, stateName), 
			whenInCase(Token.class, Action::isTriggerStepToken,stateBehavior));
		return state;
	}
	
	private static boolean isAnyTokenInState(Workflow workflow, String stateName) {
		return workflow.tokens().inState(stateName).count() != 0;
	}
	
	private static boolean isTriggerStepToken(Data<Workflow, Token> data) {
		return data.value().filter(t -> t.actionData() instanceof TriggerStep).isPresent();
	}

	private static Data<Workflow, Token> act(String stateName, Workflow workflow, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		Token firstToken = workflow.tokens().firstTokenInState(stateName).get();	
		Data<Workflow, ActionData> actionInput = data(workflow, firstToken.actionData());
		Data<Workflow, ActionData> actionOutput = actionBehavior.actOn(actionInput);
		return data(actionOutput.state(), token(firstToken.state(), actionOutput.value().orElse(null)));
	}
}
