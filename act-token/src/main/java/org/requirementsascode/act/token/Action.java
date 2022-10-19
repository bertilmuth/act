package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.token.Token.token;

import java.util.Optional;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class Action implements Node{
	private final String name;
	private final Behavior<Workflow, ActionData, ActionData> actionBehavior;
	
	private Action(String stateName, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		this.name = stateName;
		this.actionBehavior = actionBehavior;
	}
	
	public static Action action(String name, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		requireNonNull(name, "name must be non-null!");
		requireNonNull(actionBehavior, "actionBehavior must be non-null!");
		return new Action(name, actionBehavior);
	}
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public State<Workflow, Token> asState() {		
		State<Workflow, Token> state = state(name(), wf -> wf.tokens().isAnyTokenIn(name()), 
			whenInCase(Token.class, Action::isTriggerOfNextStep,this::triggerNextStep));
		return state;
	}
	
	private static boolean isTriggerOfNextStep(Data<Workflow, Token> data) {
		Optional<Token> token = data.value();
		return token.map(Token::isTriggerOfNextStep).orElse(false);
	}
	
	private Data<Workflow, Token> triggerNextStep(Data<Workflow, Token> inputData){
		Workflow workflow = inputData.state();
		return act(name, workflow, actionBehavior);
	}

	private Data<Workflow, Token> act(String stateName, Workflow workflow, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		Token firstToken = workflow.tokens().firstTokenIn(stateName).get();	
		Data<Workflow, ActionData> actionInput = data(workflow, firstToken.actionData());
		Data<Workflow, ActionData> actionOutput = actionBehavior.actOn(actionInput);
		return data(actionOutput.state(), tokenForNodeAndOutput(firstToken, actionOutput));
	}

	private Token tokenForNodeAndOutput(Token firstToken, Data<Workflow, ActionData> actionData) {
		return token(firstToken.node(), actionData.value().orElse(null));
	}

	@Override
	public String toString() {
		return "Action[" + name + "]";
	}
}
