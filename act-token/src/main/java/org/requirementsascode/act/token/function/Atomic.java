package org.requirementsascode.act.token.function;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.function.SystemFunction.systemFunction;

import java.util.Optional;
import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.token.Action;
import org.requirementsascode.act.token.ActionBehavior;
import org.requirementsascode.act.token.ActionData;
import org.requirementsascode.act.token.Node;
import org.requirementsascode.act.token.Token;
import org.requirementsascode.act.token.Workflow;

public class Atomic implements ActionBehavior {
	private final Behavior<Workflow, ActionData, ActionData> function;

	private Atomic(Behavior<Workflow, ActionData, ActionData> function) {
		this.function = function;
	}

	public static <T extends ActionData, U extends ActionData> Atomic atomic(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		return new Atomic(systemFunction(inputClass, function));
	}

	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action owningAction) {
		return inCase(this::isTriggered, d -> triggerAtomicSystemFunction(owningAction, d));
	}

	private boolean isTriggered(Data<Workflow, Token> inputData) {
		Optional<Token> token = Token.from(inputData);
		return token.map(t -> triggersAtomicSystemFunction(t)).orElse(false);
	}

	private Data<Workflow, Token> triggerAtomicSystemFunction(Action action, Data<Workflow, Token> inputData) {
		Workflow workflow = Workflow.from(inputData);
		Token tokenInAction = workflow.tokens().firstTokenIn(action.name()).get();
		Data<Workflow, Token> inputDataWithTokenInAction = data(workflow, tokenInAction);
		return executeFunction(function, inputDataWithTokenInAction);
	}

	private Data<Workflow, Token> executeFunction(Behavior<Workflow, ActionData, ActionData> function, Data<Workflow, Token> inputDataWithTokenInAction) {
		Workflow workflow = Workflow.from(inputDataWithTokenInAction);
		Token token = Token.from(inputDataWithTokenInAction).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, ActionData> functionInput = data(workflow, token.actionData());
		Data<Workflow, ActionData> functionOutput = function.actOn(functionInput);
		Token tokenAfter = tokenFor(token.node(), functionOutput);

		return workflow.replaceToken(token, tokenAfter);
	}
	
	private boolean triggersAtomicSystemFunction(Token token) {
		return token.actionData() instanceof AtomicSystemFunction;
	}

	private Token tokenFor(Node node, Data<Workflow, ActionData> actionData) {
		return token(node, actionData.value().orElse(null));
	}
}
