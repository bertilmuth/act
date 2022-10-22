package org.requirementsascode.act.token.function;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.token.Token.token;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.token.ActionData;
import org.requirementsascode.act.token.Node;
import org.requirementsascode.act.token.Token;
import org.requirementsascode.act.token.Workflow;

public class SystemFunction{	
	public static <T extends ActionData, U extends ActionData> Behavior<Workflow, ActionData, ActionData> systemFunction(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		Behavior<Workflow, T, U> functionBehavior = d -> apply(function, d);
		return when(inputClass, functionBehavior);
	}
	
	private static <T extends ActionData, U extends ActionData> Data<Workflow, U> apply(BiFunction<Workflow, T, U> function, Data<Workflow, T> input){
		Workflow workflow = Workflow.from(input);
		T inputActionData = input.value().orElse(null);
		U outputActionData = function.apply(workflow, inputActionData);
		return data(workflow, outputActionData);
	}
	
	static Data<Workflow, Token> executeFunction(Behavior<Workflow, ActionData, ActionData> function, Data<Workflow, Token> inputDataWithTokenInAction) {
		Workflow workflow = Workflow.from(inputDataWithTokenInAction);
		Token token = Token.from(inputDataWithTokenInAction).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, ActionData> functionInput = data(workflow, token.actionData());
		Data<Workflow, ActionData> functionOutput = function.actOn(functionInput);
		Token tokenAfter = tokenFor(token.node(), functionOutput);

		return workflow.replaceToken(token, tokenAfter);
	}
	
	private static Token tokenFor(Node node, Data<Workflow, ActionData> actionData) {
		return token(node, actionData.value().orElse(null));
	}
}
