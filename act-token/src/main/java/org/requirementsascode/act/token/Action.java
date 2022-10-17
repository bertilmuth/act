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
		State<Workflow, Token> state = state(name(), wf -> wf.tokens().areSufficientForNode(name()), 
			whenInCase(Token.class, Action::isTriggerNextStep,this::triggerNextStep));
		return state;
	}
	
	private static boolean isTriggerNextStep(Data<Workflow, Token> data) {
		Optional<Token> token = data.value();
		return token.filter(t -> t.actionData() instanceof TriggerNextStep).isPresent();
	}
	
	private Data<Workflow, Token> triggerNextStep(Data<Workflow, Token> inputData){
		Workflow workflow = inputData.state();
		return act(name, workflow, actionBehavior);
	}

	private static Data<Workflow, Token> act(String stateName, Workflow workflow, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		Token firstToken = workflow.tokens().firstTokenInNode(stateName).get();	
		Data<Workflow, ActionData> actionInput = data(workflow, firstToken.actionData());
		Data<Workflow, ActionData> actionOutput = actionBehavior.actOn(actionInput);
		return data(actionOutput.state(), token(firstToken.node(), actionOutput.value().orElse(null)));
	}
}
