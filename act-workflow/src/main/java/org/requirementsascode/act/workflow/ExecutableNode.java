package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class ExecutableNode implements Node {
	private final String name;
	private final Class<? extends ActionData> inputClass;
	private final Behavior<WorkflowState, Token, Token> executableBehavior;

	ExecutableNode(String name, Class<? extends ActionData> inputClass, Behavior<WorkflowState, Token, Token> executableBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.inputClass = requireNonNull(inputClass, "inputClass must be non-null!");
		this.executableBehavior = requireNonNull(executableBehavior, "executableBehavior must be non-null!");
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Class<? extends ActionData> inputClass(){
		return inputClass;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(name(), s -> s.areTokensIn(this), this::consumeToken);
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
