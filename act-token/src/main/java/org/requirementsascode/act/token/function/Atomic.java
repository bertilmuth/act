package org.requirementsascode.act.token.function;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.token.function.SystemFunction.systemFunction;

import java.util.Optional;
import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.token.Action;
import org.requirementsascode.act.token.ActionBehavior;
import org.requirementsascode.act.token.ActionData;
import org.requirementsascode.act.token.Token;
import org.requirementsascode.act.token.Workflow;

public class Atomic<T extends ActionData, U extends ActionData> implements ActionBehavior {
	private final SystemFunction<T, U> systemFunction;

	private Atomic(SystemFunction<T,U> systemFunction) {
		this.systemFunction = systemFunction;
	}

	public static <T extends ActionData, U extends ActionData> Atomic<T,U> atomic(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		return new Atomic<>(systemFunction(inputClass, function));
	}

	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action owningAction) {
		return inCase(this::triggersAtomicSystemFunction, d -> triggerAtomicSystemFunction(owningAction, d));
	}

	private boolean triggersAtomicSystemFunction(Data<Workflow, Token> inputData) {
		Optional<Token> token = Token.from(inputData);
		return token.map(t -> triggersAtomicSystemFunction(t)).orElse(false);
	}

	private Data<Workflow, Token> triggerAtomicSystemFunction(Action owningAction, Data<Workflow, Token> inputData) {
		Workflow workflow = Workflow.from(inputData);
		Token tokenInAction = workflow.tokens().firstTokenIn(owningAction.name()).get();
		Data<Workflow, Token> inputDataWithTokenInAction = data(workflow, tokenInAction);
		Data<Workflow,Token> outputData = systemFunction.executeFunction(inputDataWithTokenInAction);
		return outputData;
	}
	
	private boolean triggersAtomicSystemFunction(Token token) {
		return token.actionData() instanceof AtomicSystemFunction;
	}
}
