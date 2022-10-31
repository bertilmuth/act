package org.requirementsascode.act.token.function;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.token.function.SystemFunction.systemFunction;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.token.Action;
import org.requirementsascode.act.token.ActionBehavior;
import org.requirementsascode.act.token.ActionData;
import org.requirementsascode.act.token.Token;
import org.requirementsascode.act.token.Workflow;

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
		Workflow workflow = Workflow.from(inputData);
		Token tokenInAction = workflow.tokens().firstTokenIn(owningAction.name()).get();
		Data<Workflow, Token> inputDataWithTokenInAction = data(workflow, tokenInAction);
		Data<Workflow, Token> outputData = systemFunction.asBehavior(owningAction).actOn(inputDataWithTokenInAction);
		return outputData;
	}

	private boolean triggersStep(Token token) {
		return token.actionData()
			.map(ad -> ad instanceof StepTrigger)
			.orElse(false);
	}
	
	public static class StepTrigger implements ActionData{
		private StepTrigger() {};
	}
}
