package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.token.SystemFunction.systemFunction;
import static org.requirementsascode.act.token.Token.token;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class Step<T extends ActionData, U extends ActionData> implements ActionBehavior {
	private final SystemFunction<T, U> systemFunction;
	public static final StepTrigger stepTrigger = new StepTrigger();

	private Step(SystemFunction<T, U> systemFunction) {
		this.systemFunction = systemFunction;
	}

	public static <T extends ActionData, U extends ActionData> Step<T, U> step(Class<T> inputClass,
			BiFunction<Workflow, T, U> function) {
		return new Step<>(systemFunction(inputClass, function));
	}

	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action owningAction) {
		return inCase(this::isStepTriggering, d -> runStep(owningAction, d));
	}

	private boolean isStepTriggering(Data<Workflow, Token> inputData) {
		return Token.from(inputData).map(this::containsStepTrigger).orElse(false);
	}
	
	private boolean containsStepTrigger(Token token) {
		return token.actionData().map(ad -> ad instanceof StepTrigger).orElse(false);
	}

	private Data<Workflow, Token> runStep(Action owningAction, Data<Workflow, Token> inputData) {
		Data<Workflow, Token> workflowWithFirstTokenInAction = workflowWithFirstTokenInAction(inputData, owningAction);
		Data<Workflow, Token> outputData = systemFunction.asBehavior(owningAction).actOn(workflowWithFirstTokenInAction);
		return outputData;
	}

	private Data<Workflow, Token> workflowWithFirstTokenInAction(Data<Workflow, Token> inputData, Action owningAction) {
		Workflow workflow = Workflow.from(inputData);
		Data<Workflow, Token> workflowWithFirstTokenInAction = data(workflow, firstTokenInAction(workflow, owningAction));
		return workflowWithFirstTokenInAction;
	}

	private Token firstTokenInAction(Workflow workflow, Action owningAction) {
		return workflow.tokens().firstTokenIn(owningAction.name()).get();
	}

	public static class StepTrigger implements ActionData {
		private StepTrigger() {
		};
	}
}

class SystemFunction<T extends ActionData, U extends ActionData> implements ActionBehavior {
	private final Behavior<Workflow, ActionData, ActionData> functionOnActionData;

	private SystemFunction(Class<T> inputClass, BiFunction<Workflow, T, U> functionOnActionData) {
		Behavior<Workflow, T, U> behavior = d -> apply(functionOnActionData, d);
		this.functionOnActionData = when(inputClass, behavior);
	}

	public static <T extends ActionData, U extends ActionData> SystemFunction<T, U> systemFunction(Class<T> inputClass,
			BiFunction<Workflow, T, U> function) {
		return new SystemFunction<>(inputClass, function);
	}

	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action owningAction) {
		return this::executeFunction;
	}

	private Data<Workflow, Token> executeFunction(Data<Workflow, Token> inputData) {
		Data<Workflow, ActionData> unboxedActionData = unboxActionData(inputData);
		Data<Workflow, ActionData> functionOutput = functionOnActionData.actOn(unboxedActionData);
		Token tokenAfter = tokenFor(Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!")).node(), functionOutput);
		Data<Workflow, Token> resultWorkflow = Workflow.from(inputData).replaceToken(Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!")), tokenAfter);
		return resultWorkflow;
	}

	private Data<Workflow, ActionData> unboxActionData(Data<Workflow, Token> inputData) {
		return data(Workflow.from(inputData), actionDataFrom(inputData));
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

	private Token tokenFor(Node node, Data<Workflow, ActionData> actionData) {
		return token(node, actionData.value().orElse(null));
	}
}
