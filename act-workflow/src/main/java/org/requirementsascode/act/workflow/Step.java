package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class Step<T extends ActionData, U extends ActionData> implements ActionBehavior {
	private final Behavior<WorkflowState, Token, Token> stepBehavior;
	public static final ConsumeToken proceed = new ConsumeToken();

	Step(Class<T> inputClass, BiFunction<WorkflowState, T, U> function) {
		requireNonNull(inputClass, "inputClass must be non-null!");
		requireNonNull(function, "function must be non-null!");
		this.stepBehavior = new StepBehavior(inputClass, function);
	}

	@Override
	public Behavior<WorkflowState, Token, Token> asBehavior(Action owningAction) {
		return inCase(ConsumeToken::matches, d -> consumeToken(d.state(), owningAction));
	}

	private Data<WorkflowState, Token> consumeToken(WorkflowState workflowState, Action owningAction) {
		List<Token> tokensInAction = workflowState.tokens().inNode(owningAction.name()).collect(Collectors.toList());
		Data<WorkflowState, Token> result = data(workflowState, token(owningAction, actionOutputIn(workflowState)));
				
		for (Token token : tokensInAction) {
			result = stepBehavior.actOn(data(result.state(), token));
			if(hasStepActed(result)) {
				break;
			}
		}
		
		return result;
	}
	
	private boolean hasStepActed(Data<WorkflowState, Token> result) {
		return result.value().map(t -> t.actionData().isPresent()).orElse(false);
	}

	private ActionData actionOutputIn(WorkflowState workflowState) {
		return workflowState.actionOutput().orElse(null);
	}
	
	private class StepBehavior implements Behavior<WorkflowState, Token, Token> {
		private final BiFunction<WorkflowState, T, U> functionOnActionData;
		private final Behavior<WorkflowState, ActionData, ActionData> stepBehavior;

		private StepBehavior(Class<T> inputClass, BiFunction<WorkflowState, T, U> functionOnActionData) {
			this.functionOnActionData = functionOnActionData;
			this.stepBehavior = when(inputClass, this::applyFunctionOnActionData);
		}

		@Override
		public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
			Data<WorkflowState, ActionData> inputActionData = unboxActionData(inputData);
			Data<WorkflowState, ActionData> outputActionData = stepBehavior.actOn(inputActionData);
			
			Token inputToken = tokenFrom(inputData);
			Token outputToken = updateActionData(inputToken, outputActionData);
			
			Data<WorkflowState, Token> updatedWorkflow = inputData.state().replaceToken(inputToken, outputToken);
			return updatedWorkflow;
		}

		private Data<WorkflowState, ActionData> unboxActionData(Data<WorkflowState, Token> inputData) {
			return data(inputData.state(), ActionData.from(inputData));
		}

		private Token tokenFrom(Data<WorkflowState, Token> inputData) {
			return Token.from(inputData).orElseThrow(() -> new IllegalArgumentException("No token present!"));
		}

		private Token updateActionData(Token token, Data<WorkflowState, ActionData> outputActionData) {
			return token(token.node(), outputActionData.value().orElse(null));
		}


		private Data<WorkflowState, U> applyFunctionOnActionData(Data<WorkflowState, T> input) {
			WorkflowState workflowState = input.state();
			T inputActionData = input.value().orElse(null);
			U outputActionData = functionOnActionData.apply(workflowState, inputActionData);
			return data(workflowState, outputActionData);
		}
	}
}

