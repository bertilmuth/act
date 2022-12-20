package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;
import org.requirementsascode.act.workflow.trigger.ConsumeToken;
import org.requirementsascode.act.workflow.trigger.StoreToken;

public class ExecutableNode<T> implements Node {
	private final String name;
	private final Class<T> inputClass;
	private final Behavior<WorkflowState, Token, Token> executableBehavior;

	ExecutableNode(String name, Class<T> inputClass, Behavior<WorkflowState, Token, Token> executableBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.inputClass = requireNonNull(inputClass, "inputClass must be non-null!");
		this.executableBehavior = requireNonNull(executableBehavior, "executableBehavior must be non-null!");
	}

	@Override
	public String name() {
		return name;
	}
	
	public Class<T> inputClass(){
		return inputClass;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		State<WorkflowState, Token> state = state(name(), s -> s.areTokensIn(this), nodeBehavior());
		return state;
	}
	
	private Behavior<WorkflowState, Token, Token> nodeBehavior() {
		return unitedBehavior(
			new OnlyOneBehaviorMayAct<>(),
			inCase(StoreToken::isContained, this::storeToken),
			inCase(ConsumeToken::isContained, this::consumeToken)
		);
	}
	
	private Data<WorkflowState, Token> storeToken(Data<WorkflowState, Token> inputData) {
		Token token = Token.from(inputData).actionData().map(t -> (StoreToken)t).map(StoreToken::token).orElse(null);
		return moveTokenToMe(inputData.state(), token);
	}

	private Data<WorkflowState, Token> consumeToken(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		List<Token> tokensInNode = state.tokensIn(this).collect(Collectors.toList());
		
		Data<WorkflowState, Token> result = data(state, null);
		for (Token tokenInNode : tokensInNode) {
			result = executableBehavior.actOn(data(result.state(), tokenInNode));
			if (hasExecuted(result)) {
				break;
			}
		}

		return result;
	}

	private boolean hasExecuted(Data<WorkflowState, Token> result) {
		return result.value().map(t -> t.actionData().isPresent()).orElse(false);
	}

	@Override
	public String toString() {
		return "Action[" + name + "]";
	}
}
