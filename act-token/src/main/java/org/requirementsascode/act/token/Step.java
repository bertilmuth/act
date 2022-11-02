package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.token.Token.token;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class Step<T extends ActionData, U extends ActionData> implements ActionBehavior {
	private final Behavior<Workflow, Token, Token> stepBehavior;
	public static final Proceed proceed = new Proceed();

	private Step(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		requireNonNull(inputClass, "inputClass must be non-null!");
		requireNonNull(function, "function must be non-null!");
		this.stepBehavior = new StepBehavior(inputClass, function);
	}

	public static <T extends ActionData, U extends ActionData> Step<T, U> step(Class<T> inputClass,
			BiFunction<Workflow, T, U> function) {
		return new Step<>(inputClass, function);
	}

	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action owningAction) {
		return inCase(Token::isForProceeding, d -> proceed(Workflow.from(d), owningAction));
	}

	private Data<Workflow, Token> proceed(Workflow workflow, Action owningAction) {
		return stepBehavior.actOn(firstTokenInAction(workflow, owningAction));
	}

	private Data<Workflow, Token> firstTokenInAction(Workflow workflow, Action owningAction) {
		return data(workflow, firstTokenIn(workflow, owningAction));
	}

	private Token firstTokenIn(Workflow workflow, Action owningAction) {
		return workflow.state().tokens().firstTokenIn(owningAction.name()).get();
	}

	static class Proceed implements ActionData {
		private Proceed() {
		};
	}
	
	private class StepBehavior implements Behavior<Workflow, Token, Token> {
		private final Class<T> inputClass;
		private final BiFunction<Workflow, T, U> functionOnActionData;
		private final Behavior<Workflow, ActionData, ActionData> stepBehavior;


		private StepBehavior(Class<T> inputClass, BiFunction<Workflow, T, U> functionOnActionData) {
			this.inputClass = inputClass;
			this.functionOnActionData = functionOnActionData;
			this.stepBehavior = createStepBehavior();
		}

		@Override
		public Data<Workflow, Token> actOn(Data<Workflow, Token> inputData) {
			Data<Workflow, ActionData> inputActionData = unboxActionData(inputData);
			Data<Workflow, ActionData> outputActionData = stepBehavior.actOn(inputActionData);
			
			Token inputToken = tokenFrom(inputData);
			Token outputToken = updateActionData(inputToken, outputActionData);
			
			Data<Workflow, Token> updatedWorkflow = Workflow.from(inputData).state().replaceToken(inputToken, outputToken);
			return updatedWorkflow;
		}

		private Data<Workflow, ActionData> unboxActionData(Data<Workflow, Token> inputData) {
			return data(Workflow.from(inputData), actionDataFrom(inputData));
		}

		private Token tokenFrom(Data<Workflow, Token> inputData) {
			return Token.from(inputData).orElseThrow(() -> new IllegalArgumentException("No token present!"));
		}

		private Token updateActionData(Token token, Data<Workflow, ActionData> data) {
			return token(token.node(), data.value().orElse(null));
		}

		private ActionData actionDataFrom(Data<Workflow, Token> inputData) {
			return Token.from(inputData).flatMap(Token::actionData).orElse(null);
		}
		
		private Behavior<Workflow, ActionData, ActionData> createStepBehavior() {
			return when(inputClass, this::applyFunctionOnActionData);
		}

		private Data<Workflow, U> applyFunctionOnActionData(Data<Workflow, T> input) {
			Workflow workflow = Workflow.from(input);
			T inputActionData = input.value().orElse(null);
			U outputActionData = functionOnActionData.apply(workflow, inputActionData);
			return data(workflow, outputActionData);
		}
	}
}

