package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;
import org.requirementsascode.act.workflow.trigger.ConsumeToken;
import org.requirementsascode.act.workflow.trigger.MoveToken;

public class Action implements Node {
	private final String name;
	private final Behavior<WorkflowState, Token, Token> actionBehavior;

	Action(String name, Behavior<WorkflowState, Token, Token> actionBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.actionBehavior = requireNonNull(actionBehavior, "actionBehavior must be non-null!");
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		State<WorkflowState, Token> state = state(name(), s -> s.areTokensIn(this), actionBehavior());
		return state;
	}
	
	private Behavior<WorkflowState, Token, Token> actionBehavior() {
		Behavior<WorkflowState, Token, Token> behavior = 
			unitedBehavior(
				new OnlyOneBehaviorMayAct<>(),
				inCase(ConsumeToken::isContained, this::consumeToken),
				inCase(MoveToken::isContained, d -> {
					Token tokenToMove = Token.from(d).actionData().map(t -> (MoveToken)t).map(MoveToken::token).orElse(null);
					System.out.println("ACTION: Moving " + tokenToMove + " to " + this);
					Data<WorkflowState, Token> output = moveTokenToMe(d.state(), tokenToMove);
					return data(output.state(), null);
				})
			);
		return behavior;
	}

	private Data<WorkflowState, Token> consumeToken(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		List<Token> tokensInAction = state.tokensIn(this).collect(Collectors.toList());
		System.out.println("Consuming: " + tokensInAction);
		
		Data<WorkflowState, Token> result = data(state, null);
		for (Token token : tokensInAction) {
			result = actionBehavior.actOn(data(result.state(), token));
			if (hasStepActed(result)) {
				break;
			}
		}

		return result;
	}

	private boolean hasStepActed(Data<WorkflowState, Token> result) {
		return result.value().map(t -> t.actionData().isPresent()).orElse(false);
	}

	@Override
	public String toString() {
		return "Action[" + name + "]";
	}
}
