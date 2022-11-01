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
	private final SystemFunction systemFunction;
	public static final StepTrigger stepTrigger = new StepTrigger();

	private Step(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		requireNonNull(inputClass, "inputClass must be non-null!");
		requireNonNull(function, "function must be non-null!");
		this.systemFunction = new SystemFunction(inputClass, function);
	}

	public static <T extends ActionData, U extends ActionData> Step<T, U> step(Class<T> inputClass,
			BiFunction<Workflow, T, U> function) {
		return new Step<>(inputClass, function);
	}

	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action owningAction) {
		return inCase(Token::isStepTriggering, d -> runStep(Workflow.from(d), owningAction));
	}

	private Data<Workflow, Token> runStep(Workflow workflow, Action owningAction) {
		Behavior<Workflow, Token, Token> stepBehavior = systemFunction.asBehavior(owningAction);
		return stepBehavior.actOn(firstTokenInAction(workflow, owningAction));
	}

	private Data<Workflow, Token> firstTokenInAction(Workflow workflow, Action owningAction) {
		return data(workflow, firstTokenIn(workflow, owningAction));
	}

	private Token firstTokenIn(Workflow workflow, Action owningAction) {
		return workflow.tokens().firstTokenIn(owningAction.name()).get();
	}

	public static class StepTrigger implements ActionData {
		private StepTrigger() {
		};
	}
	
	private class SystemFunction implements ActionBehavior {
		private final Behavior<Workflow, ActionData, ActionData> functionOnActionData;

		private SystemFunction(Class<T> inputClass, BiFunction<Workflow, T, U> functionOnActionData) {
			Behavior<Workflow, T, U> behavior = d -> apply(functionOnActionData, d);
			this.functionOnActionData = when(inputClass, behavior);
		}

		@Override
		public Behavior<Workflow, Token, Token> asBehavior(Action owningAction) {
			return this::executeFunction;
		}

		private Data<Workflow, Token> executeFunction(Data<Workflow, Token> inputData) {
			Data<Workflow, ActionData> inputActionData = unboxActionData(inputData);
			Data<Workflow, ActionData> outputActionData = functionOnActionData.actOn(inputActionData);
			
			Token inputToken = tokenFrom(inputData);
			Token outputToken = updateActionData(inputToken, outputActionData);
			
			return Workflow.from(inputData).replaceToken(inputToken, outputToken);
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

		private Data<Workflow, U> apply(BiFunction<Workflow, T, U> function, Data<Workflow, T> input) {
			Workflow workflow = Workflow.from(input);
			T inputActionData = input.value().orElse(null);
			U outputActionData = function.apply(workflow, inputActionData);
			return data(workflow, outputActionData);
		}
	}
}

