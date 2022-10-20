package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.token.Token.token;

import java.util.Optional;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.InCase;
import org.requirementsascode.act.statemachine.State;

public class Action implements Node{
	private final String name;
	private final Behavior<Workflow, ActionData, ActionData> behavior;
	
	private Action(String name, Behavior<Workflow, ActionData, ActionData> behavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.behavior = requireNonNull(behavior, "behavior must be non-null!");
	}
	
	public static Action action(String name, Behavior<Workflow, ActionData, ActionData> behavior) {
		return new Action(name, behavior);
	}
	
	@Override
	public String name() {
		return name;
	}
	
	public Behavior<Workflow, ActionData, ActionData> behavior(){
		return behavior;
	}

	@Override
	public State<Workflow, Token> asState() {		
		State<Workflow, Token> state = state(name(), wf -> wf.tokens().isAnyTokenIn(name()), 
			when(Token.class, actionFunction(this)));
		return state;
	}

	private Behavior<Workflow, Token, Token> actionFunction(Action action) {
		return InCase.inCase(Action::isTriggerOfNextStep, d -> triggerNextStep(action, d));
	}
	
	private static boolean isTriggerOfNextStep(Data<Workflow, Token> inputData) {
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

	@Override
	public String toString() {
		return "Action[" + name + "]";
	}
}
