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
		return inCase(this::triggersStep, d -> runStep(owningAction, d));
	}

	private boolean triggersStep(Data<Workflow, Token> inputData) {
		return Token.from(inputData).map(t -> triggersStep(t)).orElse(false);
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
		Token tokenInAction = workflow.tokens().firstTokenIn(owningAction.name()).get();
		return tokenInAction;
	}

	private boolean triggersStep(Token token) {
		return token.actionData().map(ad -> ad instanceof StepTrigger).orElse(false);
	}

	public static class StepTrigger implements ActionData {
		private StepTrigger() {
		};
	}
}

class SystemFunction<T extends ActionData, U extends ActionData> implements ActionBehavior {
	private final Behavior<Workflow, ActionData, ActionData> functionBehavior;

	private SystemFunction(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		this.functionBehavior = when(inputClass, d -> apply(function, d));
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
		Workflow workflow = Workflow.from(inputData);
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, ActionData> functionInput = data(workflow, token.actionData().orElse(null));
		Data<Workflow, ActionData> functionOutput = functionBehavior.actOn(functionInput);
		Token tokenAfter = tokenFor(token.node(), functionOutput);
		Data<Workflow, Token> resultWorkflow = workflow.replaceToken(token, tokenAfter);
		return resultWorkflow;
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
